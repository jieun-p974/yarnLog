package com.yarnlog.yarnlog.service

import com.yarnlog.yarnlog.domain.Tag
import com.yarnlog.yarnlog.domain.Yarn
import com.yarnlog.yarnlog.domain.YarnTag
import com.yarnlog.yarnlog.dto.*
import com.yarnlog.yarnlog.repository.TagRepository
import com.yarnlog.yarnlog.repository.UserRepository
import com.yarnlog.yarnlog.repository.YarnRepository
import com.yarnlog.yarnlog.repository.YarnTagRepository
import com.yarnlog.yarnlog.util.SlugUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.util.*

@Service
class YarnService (
    private val yarnRepository: YarnRepository,
    private val userRepository: UserRepository,
    private val yarnTagRepository: YarnTagRepository,
    private val tagRepository: TagRepository,
    @Value("\${file.upload-dir}") private val uploadDir: String
){
    @Transactional
    fun createYarn(userId: Long, request: YarnCreateRequest): YarnResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val yarn = Yarn(
            user = user,
            name = request.name,
            brand = request.brand,
            colorName = request.colorName,
            colorCode = request.colorCode,
            weightGram = request.weightGram,
            remainingLength = request.remainingLength,
            memo = request.memo,
            imageUrl = request.imageUrl
            )

        val saved = yarnRepository.save(yarn)

        // null이 아닐 때 태그 추가
        request.tags?.let{ attachTags(saved, it) }

        val tags = getTagResponses(yarn.id)

        return saved.toResponse(tags)
    }

    // 목록 조회
    @Transactional
    fun getYarns(userId: Long, tag: String?): List<YarnResponse>{
        // 태그 값 없으면 사용자 아이디로 검색(최신순)
        // 태그 값 있으면 사용자 아이디, 태그로 검색(최신순)
        val yarns = if (tag.isNullOrBlank()){
            yarnRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId)
        }else{
            val slug = SlugUtil.toSlug(tag)
            yarnRepository.findAllByUserIdAndTagSlugOrderByUpdatedAtDesc(userId, slug)
        }

        if(yarns.isEmpty()) return emptyList()

        val yarnIds = yarns.map { it.id }
        // 태그 한 번에 조회해서 실 아이디 별로 그룹핑
        val yarnTags = yarnTagRepository.findAllWithTagByYarnIdIn(yarnIds)
        val tagsByYarnId: Map<Long, List<TagResponse>> =
            yarnTags.groupBy{ it.yarn.id }
                .mapValues { (_, list) ->
                    list.map{ yt ->
                        TagResponse(
                            id = yt.tag.id,
                            name = yt.tag.name,
                            slug = yt.tag.slug
                        )
                    }
                }

        return yarns.map{ yarn ->
            val tags = tagsByYarnId[yarn.id] ?: emptyList()
            yarn.toResponse(tags)
        }
    }

    // 상세 목록 조회
    @Transactional(readOnly = true)
    fun getYarn(userId: Long, yarnId: Long): YarnResponse{
        val yarn = yarnRepository.findById(yarnId)
            .orElseThrow{
                ResponseStatusException(HttpStatus.NOT_FOUND, "실 정보를 찾을 수 없습니다. id=$yarnId")
            }

        // 자기 실만 조회
        if(yarn.user.id != userId){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 실에는 접근할 수 없습니다.")
        }

        val tags = getTagResponses(yarn.id)

        return yarn.toResponse(tags)
    }

    // 수정
    @Transactional
    fun updateYarn(userId: Long, yarnId: Long, request: YarnUpdateRequest): YarnResponse{
        val yarn = yarnRepository.findById(yarnId)
            .orElseThrow{
                ResponseStatusException(HttpStatus.NOT_FOUND, "실 정보를 찾을 수 없습니다. id=$yarnId")
            }

        // 자기 실만 수정
        if(yarn.user.id != userId){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 실은 수정할 수 없습니다.")
        }

        // null이 아닌 값만 수정
        request.name?.let { yarn.name = it }
        request.brand?.let { yarn.brand = it }
        request.colorName?.let { yarn.colorName = it }
        request.colorCode?.let { yarn.colorCode = it }
        request.weightGram?.let { yarn.weightGram = it }
        request.remainingLength?.let { yarn.remainingLength = it }
        request.memo?.let { yarn.memo = it }
        request.imageUrl?.let { yarn.imageUrl = it }
        // tags = null이면 변경 없음, tags=[]이면 모두 제거, tags=[...]이면 태그 교체
        request.tags?.let { attachTags(yarn, it) }
        // 수정 시간 갱신
        yarn.updatedAt = LocalDateTime.now()

        val saved = yarnRepository.save(yarn)
        val tags = getTagResponses(saved.id)

        return saved.toResponse(tags)
    }

    @Transactional
    fun deleteYarn(userId: Long, yarnId: Long){
        val yarn = yarnRepository.findById(yarnId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "실 정보를 찾을 수 없습니다. id=$yarnId")
            }

        if(yarn.user.id != userId){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 실은 삭제할 수 없습니다.")
        }

        yarnRepository.delete(yarn)
    }

    @Transactional
    fun uploadYarnImage(userId: Long, yarnId: Long, file: MultipartFile): YarnResponse{
        // 파일 없는 경우
        if(file.isEmpty){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다.")
        }

        // 이미지 파일이 아닌 경우
        val contentType = file.contentType ?: ""

        if(!contentType.startsWith("image/")){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다.")
        }

        // 실 정보 없는 경우
        val yarn = yarnRepository.findById(yarnId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "실 정보를 찾을 수 없습니다. id=$yarnId")
        }

        // 다른 사용자의 실일 경우
        if(yarn.user.id != userId){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 실에는 접근할 수 없습니다.")
        }

        // 업로드 폴더 생성
        val uploadPath: Path = Paths.get(uploadDir).toAbsolutePath().normalize()
        Files.createDirectories(uploadPath)

        // 확장자 결정(기본은 bin, 가능하면 원본에서 추출)
        val originalName = file.originalFilename ?: "file"
        val ext = originalName.substringAfterLast('.',"").takeIf { it.isNotBlank() } ?: "bin"
        // 파일명 충돌 방지
        val storedFileName = "${UUID.randomUUID()}.$ext"
        val targetPath = uploadPath.resolve(storedFileName)

        Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

        // DB에 이미지 접근 가능 URL 저장
        yarn.imageUrl = "/uploads/$storedFileName"
        yarn.updatedAt = LocalDateTime.now()

        val saved = yarnRepository.save(yarn)
        val tags = getTagResponses(saved.id)
        return saved.toResponse(tags)
    }

    private fun attachTags(yarn: Yarn, tagNames: List<String>){
        // 1) 입력 정리: 공백 제거 + 빈값 제거
        val cleaned = tagNames
            .map { it.trim() }
            .filter { it.isNotBlank() }

        // 2) slug 생성 후 "slug 기준" 중복 제거 (핵심!)
        val uniqueBySlug: List<Pair<String, String>> = cleaned
            .map { name -> name to SlugUtil.toSlug(name) } // (name, slug)
            .distinctBy { it.second }                      // slug 기준 중복 제거

        // 3) 기존 태그 삭제 (재등록 방식)
        yarnTagRepository.deleteAllByYarn_Id(yarn.id)
        yarnTagRepository.flush() // ✅ 삭제 먼저 DB에 반영 (중요)

        // 4) 재등록
        uniqueBySlug.forEach { (name, slug) ->
            val tag = tagRepository.findBySlug(slug)
                ?: tagRepository.save(Tag(name = name, slug = slug))

            yarnTagRepository.save(
                YarnTag(
                    yarn = yarn,
                    tag = tag
                )
            )
        }
    }

    private fun getTagResponses(yarnId: Long): List<TagResponse>{
        return yarnTagRepository.findAllByYarn_Id(yarnId)
            .map{
                TagResponse(
                    id = it.tag.id,
                    name = it.tag.name,
                    slug = it.tag.slug
                )
            }
    }
}