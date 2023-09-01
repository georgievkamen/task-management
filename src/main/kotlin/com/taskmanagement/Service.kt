package com.taskmanagement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class Service(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val clientRepository: ClientRepository,
    private val companyRepository: CompanyRepository
) {
    @Transactional
    fun createProject(project: ProjectDTO): Response {
        val errors = validateProject(project)
        if (errors.isNotEmpty()) {
            return Response(1, PROJECT_PERSISTENCE_ERROR_MESSAGE, errors)
        }

        var client: Client? = null
        var company: Company? = null

        if (project.clientId != null) {
            try {
                client = clientRepository.getReferenceById(project.clientId)
            } catch (ex: EntityNotFoundException) {
                return Response(1, PROJECT_PERSISTENCE_ERROR_MESSAGE, validationErrors = listOf(ex.message.toString()))
            }
        } else if (project.companyId != null) {
            try {
                company = companyRepository.getReferenceById(project.companyId)
            } catch (ex: EntityNotFoundException) {
                return Response(1, PROJECT_PERSISTENCE_ERROR_MESSAGE, validationErrors = listOf(ex.message.toString()))
            }
        }

        val projectId = projectRepository.save(
            Project(
                title = project.title,
                description = project.description,
                client = client,
                company = company
            )
        ).id

        return Response(0, "Successfully persisted project with id: $projectId")
    }

    fun getAllProjects() = Response(0, projectRepository.findAllByDeletedFalse().toString())

    fun deleteProject(id: Long) =
        try {
            val project = projectRepository.getReferenceById(id)
            projectRepository.delete(project)
            Response(0, "Successfully deleted project with id: ${project.id}")
        } catch (ex: EntityNotFoundException) {
            Response(1, "Could not find project with id: $id", listOf(ex.message.toString()))
        }


    private fun validateProject(project: ProjectDTO): List<String> {
        val errors = mutableListOf<String>()

        if (project.clientId == null && project.companyId == null) {
            errors.add("Missing client and company id")
        }

        if (project.clientId != null && project.companyId != null) {
            errors.add("You should provide either company or client")
        }

        return errors.toList()
    }

    companion object {
        private const val PROJECT_PERSISTENCE_ERROR_MESSAGE = "Could not persist project due to validation errors"
    }
}

data class Response(
    val code: Short,
    val data: String,
    @JsonProperty("validation_errors")
    val validationErrors: List<String> = listOf()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectDTO(
    val title: String,
    val description: String,
    val companyId: Long?,
    val clientId: Long?
)
