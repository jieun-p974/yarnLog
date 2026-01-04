package com.yarnlog.yarnlog.service

import com.yarnlog.yarnlog.domain.Yarn
import com.yarnlog.yarnlog.dto.YarnCreateRequest
import com.yarnlog.yarnlog.dto.YarnResponse
import com.yarnlog.yarnlog.dto.YarnUpdateRequest
import com.yarnlog.yarnlog.dto.toResponse
import com.yarnlog.yarnlog.repository.UserRepository
import com.yarnlog.yarnlog.repository.YarnRepository
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

        return saved.toResponse()
    }

    // 목록 조회
    @Transactional
    fun getYarns(userId: Long): List<YarnResponse>{
        val yarns = yarnRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId)

        return yarns.map{it.toResponse()}
    }

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

        return yarn.toResponse()
    }

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

        // 수정 시간 갱신
        yarn.updatedAt = LocalDateTime.now()

        val saved = yarnRepository.save(yarn)

        return saved.toResponse()
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

        return yarnRepository.save(yarn).toResponse()
    }
}