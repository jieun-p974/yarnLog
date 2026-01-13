package com.yarnlog.yarnlog.service

import com.yarnlog.yarnlog.domain.Project
import com.yarnlog.yarnlog.domain.ProjectTag
import com.yarnlog.yarnlog.domain.ProjectYarn
import com.yarnlog.yarnlog.domain.Tag
import com.yarnlog.yarnlog.dto.*
import com.yarnlog.yarnlog.repository.*
import com.yarnlog.yarnlog.util.SlugUtil
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class ProjectService (
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    private val projectYarnRepository: ProjectYarnRepository,
    private val yarnRepository: YarnRepository,
    private val projectTagRepository: ProjectTagRepository,
    private val tagRepository: TagRepository
){
    @Transactional
    fun createProject(userId: Long, request: ProjectCreateRequest): ProjectResponse{
        val user = userRepository.findById(userId)
            .orElseThrow{ ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.") }

        // 날짜 검증
        if(request.startedAt != null && request.finishedAt != null && request.finishedAt.isBefore(request.startedAt)){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "finishedAt은 startAt 이후여야 합니다.")
        }

        val project = Project(
            user = user,
            title = request.title,
            memo = request.memo,
            imageUrl = request.imageUrl,
            startedAt = request.startedAt,
            finishedAt = request.finishedAt,
            updatedAt = LocalDateTime.now()
        )

        val saved = projectRepository.save(project)

        request.yarnIds?.let { attachProjectYarns(userId, saved, it) }
        request.tags?.let { attachProjectTags(saved, it) }

        val yarnIds = getProjectYarnIds(saved.id)
        val tags = getProjectTags(saved.id)

        return saved.toResponse(yarnIds, tags)
    }

    @Transactional(readOnly = true)
    fun getProjects(userId: Long): List<ProjectResponse>{
        return projectRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getProject(userId: Long, projectId: Long): ProjectResponse{
        val project = projectRepository.findById(projectId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다. id=$projectId")
            }

        if(project.user.id != userId){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 프로젝트는 조회할 수 없습니다.")
        }

        val yarnIds = getProjectYarnIds(project.id)
        val tags = getProjectTags(project.id)

        return project.toResponse(yarnIds, tags)
    }

    @Transactional
    fun updateProject(userId: Long, projectId: Long, request: ProjectUpdateRequest): ProjectResponse{
        val project = projectRepository.findById(projectId)
            .orElseThrow{
                ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다. id=$projectId")
            }

        if (project.user.id != userId){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 프로젝트는 수정할 수 없습니다.")
        }

        request.title?.let { project.title = it }
        request.memo?.let { project.memo = it }
        request.imageUrl?.let { project.imageUrl = it }
        request.startedAt?.let { project.startedAt = it }
        request.finishedAt?.let { project.finishedAt = it }
        request.yarnIds?.let { attachProjectYarns(userId, project, it) }
        request.tags?.let { attachProjectTags(project, it) }

        if (project.startedAt != null && project.finishedAt != null && project.finishedAt!!.isBefore(project.startedAt)){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "finishedAt은 startedAt 이후여야 합니다.")
        }

        project.updatedAt = LocalDateTime.now()

        val saved = projectRepository.save(project)
        val yarnIds = getProjectYarnIds(saved.id)
        val tags = getProjectTags(saved.id)

        println("DEBUG tags=${request.tags}, yarnIds=${request.yarnIds}")

        return saved.toResponse(yarnIds, tags)
    }

    @Transactional
    fun deleteProject(userId: Long, projectId: Long){
        val project = projectRepository.findById(projectId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다. id=$projectId")
            }

        if (project.user.id != userId){
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "다른 사용자의 프로젝트는 삭제할 수 없습니다.")
        }

        projectRepository.delete(project)
    }

    @Transactional(readOnly = true)
    fun getProjects(userId: Long, tag: String?, yarnId: Long?, keyword: String?): List<ProjectResponse>{
        val slug = tag?.takeIf { it.isNotBlank() }?.let { SlugUtil.toSlug(it) }
        val kw = keyword?.trim()?.takeIf { it.isNotBlank() }
        val projects: List<Project> = when{
            // 태그, 실, 키워드 값이 있을 때
            slug != null && yarnId != null && kw != null -> projectRepository.searchByUserIdWithAllFilters(userId, slug, yarnId, kw)
            // 태그, 실 값이 있을 때
            slug != null && yarnId != null -> projectRepository.searchByUserIdWithTagAndYarn(userId, slug, yarnId)
            // 태그, 키워드 값이 있을 때
            slug != null && kw != null -> projectRepository.searchByUserIdWithTagAndKeyword(userId, slug, kw)
            // 실, 키워드 값이 있을 때
            yarnId != null && kw != null -> projectRepository.searchByUserIdWithYarnAndKeyword(userId, yarnId, kw)
            // 태그만 있을 때
            slug != null -> projectRepository.findAllByUserIdAndTagSlugOrderByUpdatedAtDesc(userId, slug)
            // 실만 있을 때
            yarnId != null -> projectRepository.findAllByUserIdAndYarnIdOrderByUpdatedAtDesc(userId, yarnId)
            // 키워드만 있을 때
            kw != null -> projectRepository.searchByUserIdWithKeyword(userId, kw)

            else -> projectRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId)
        }

        if(projects.isEmpty()) return emptyList()

        val projectIds = projects.map { it.id }
        val projectYarns = projectYarnRepository.findAllByProjectIdIn(projectIds)
        val yarnIdsByProjectId: Map<Long, List<Long>> =
            projectYarns.groupBy { it.project.id }
                .mapValues { (_, list) -> list.map { it.yarn.id } }
        val projectTags = projectTagRepository.findAllWithTagByProjectIdIn(projectIds)
        val tagsByProjectId =
            projectTags.groupBy { it.project.id }
                .mapValues { (_, list) ->
                    list.map {
                        TagResponse(
                            id = it.tag.id,
                            name = it.tag.name,
                            slug = it.tag.slug
                        )
                    }
                }

        return projects.map { p ->
            val yarnIds = yarnIdsByProjectId[p.id] ?: emptyList()
            val tagsResp = tagsByProjectId[p.id] ?: emptyList()

            p.toResponse(yarnIds, tagsResp)
        }
    }

    private fun attachProjectYarns(userId: Long, project: Project, yarnIds: List<Long>){
        val uniqueIds = yarnIds.distinct()

        // 기존 연결 제거 후 재등록
        projectYarnRepository.deleteAllByProject_Id(project.id)
        projectYarnRepository.flush()

        if(uniqueIds.isEmpty()) return

        val yarns = yarnRepository.findAllByIdInAndUser_Id(uniqueIds, userId)

        // 요청 개수 =/= 조회된 개수면 예외처리(다른 유저 실이 섞여있거나 존재하지 않는 id)
        if(yarns.size != uniqueIds.size){
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "연결할 수 없는 실이 포함되어 있습니다.")
        }

        yarns.forEach{ yarn ->
            projectYarnRepository.save(
                ProjectYarn(
                    project = project,
                    yarn = yarn
                )
            )
        }
    }

    private fun getProjectYarnIds(projectId: Long): List<Long>{
        return projectYarnRepository.findAllByProject_Id(projectId)
            .map{ it.yarn.id }
    }

    private fun getProjectTags(projectId: Long): List<TagResponse>{
        return projectTagRepository.findAllWithTagByProjectId(projectId)
            .map {
                TagResponse(
                    id = it.tag.id,
                    name = it.tag.name,
                    slug = it.tag.slug
                )
            }
    }

    private fun attachProjectTags(project: Project, tagNames: List<String>){
        val cleaned = tagNames.map { it.trim() }.filter { it.isNotBlank() }
        val uniqueBySlug: List<Pair<String, String>> = cleaned
            .map { name -> name to SlugUtil.toSlug(name) }
            .distinctBy { it.second }

        projectTagRepository.deleteAllByProject_Id(project.id)
        projectTagRepository.flush()

        uniqueBySlug.forEach { (name, slug) ->
            if(slug.isBlank()){
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 태그입니다: '$name'")
            }

            val tag = tagRepository.findBySlug(slug)?: tagRepository.save(Tag(name = name, slug = slug))

            projectTagRepository.save(
                ProjectTag(
                    project = project,
                    tag = tag
                )
            )
        }
    }
}