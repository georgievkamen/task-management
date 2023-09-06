package com.taskmanagement

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service

interface TaskManagementService {
    fun createProject(project: ProjectRequest): Response
    fun updateProject(project: ProjectRequest, id: Long): Response
    fun getAllProjects(): Slice<ProjectResponse>
    fun deleteProject(id: Long): Response
    fun createTask(task: TaskRequest): Response
    fun updateTask(task: TaskRequest, id: Long): Response
    fun deleteTask(id: Long): Response
    fun getAllTasks(): Slice<Task>
}

@Service
class Service(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val clientRepository: ClientRepository,
    private val companyRepository: CompanyRepository
) : TaskManagementService {
    @Transactional
    override fun createProject(project: ProjectRequest): Response {
        val errors = validateProject(project)
        val (company, client) = getCompanyOrClient(errors, project)
        val tasks = getTasks(errors, project.taskIds)

        if (errors.isNotEmpty()) {
            return Response(1, PROJECT_PERSISTENCE_ERROR_MESSAGE, errors)
        }

        val projectId = projectRepository.save(
            Project(
                title = project.title,
                description = project.description,
                client = client,
                company = company,
                tasks = tasks
            )
        ).id

        return Response(0, "Successfully persisted project with id: $projectId")
    }

    @Transactional
    override fun updateProject(project: ProjectRequest, id: Long): Response {
        val errors = validateProject(project)
        val (company, client) = getCompanyOrClient(errors, project)
        val tasks = getTasks(errors, project.taskIds)

        if (errors.isNotEmpty()) {
            return Response(1, PROJECT_UPDATE_ERROR_MESSAGE, errors)
        }

        val projectEntity =
            try {
                projectRepository.getReferenceById(id)
            } catch (ex: EntityNotFoundException) {
                return Response(1, PROJECT_UPDATE_ERROR_MESSAGE, listOf(ex.message.toString()))
            }

        projectEntity.apply {
            title = project.title
            description = project.description
            this.company = company
            this.client = client
            this.tasks = tasks
        }

        projectRepository.save(projectEntity)

        return Response(0, "Successfully updated project with id: $id")
    }

    override fun getAllProjects() = projectRepository.findAllByDeletedFalse().map { project ->
        ProjectResponse(
            id = project.id,
            title = project.title,
            description = project.description,
            company = project.company,
            client = project.client,
            duration = project.calculateDuration(),
            status = project.tasks.groupBy { it.status }.maxByOrNull { it.value.size }?.key ?: Status.NEW
        )
    }

    @Transactional
    override fun deleteProject(id: Long) =
        try {
            val project = projectRepository.getReferenceById(id)
            projectRepository.delete(project)
            Response(0, "Successfully deleted project with id: ${project.id}")
        } catch (ex: EntityNotFoundException) {
            Response(1, "Could not find project with id: $id", listOf(ex.message.toString()))
        }

    @Transactional
    override fun createTask(task: TaskRequest): Response {
        val errors = mutableListOf<String>()
        val project = getProject(errors, task)
        val duration = task.getDurationAsLong(errors)

        if (errors.isNotEmpty()) {
            return Response(1, TASK_PERSISTENCE_ERROR_MESSAGE, errors)
        }

        val taskId = taskRepository.save(
            Task(
                name = task.name,
                duration = duration!!,
                project = project,
                status = task.status
            )
        ).id

        return Response(0, "Successfully persisted task with id: $taskId")
    }

    @Transactional
    override fun updateTask(task: TaskRequest, id: Long): Response {
        val errors = mutableListOf<String>()
        val project = getProject(errors, task)
        val duration = task.getDurationAsLong(errors)

        if (errors.isNotEmpty()) {
            return Response(1, TASK_PERSISTENCE_ERROR_MESSAGE, errors)
        }

        val taskEntity =
            try {
                taskRepository.getReferenceById(id)
            } catch (ex: EntityNotFoundException) {
                return Response(1, TASK_UPDATE_ERROR_MESSAGE, listOf(ex.message.toString()))
            }

        taskEntity.apply {
            name = task.name
            status = task.status
            this.duration = duration!!
            this.project = project
        }

        taskRepository.save(taskEntity)

        return Response(0, "Successfully updated task with id: ${taskEntity.id}")
    }

    @Transactional
    override fun deleteTask(id: Long) =
        try {
            val project = taskRepository.getReferenceById(id)
            taskRepository.delete(project)
            Response(0, "Successfully deleted task with id: ${project.id}")
        } catch (ex: EntityNotFoundException) {
            Response(1, "Could not find task with id: $id", listOf(ex.message.toString()))
        }

    override fun getAllTasks() = taskRepository.findAllByDeletedFalse()

    private fun validateProject(project: ProjectRequest): MutableList<String> {
        val errors = mutableListOf<String>()

        if (project.clientId == null && project.companyId == null) {
            errors.add("Missing client and company id")
        }

        if (project.clientId != null && project.companyId != null) {
            errors.add("You should provide either company or client")
        }

        return errors
    }

    private fun getProject(errors: MutableList<String>, task: TaskRequest): Project? {
        var project: Project? = null

        if (task.projectId != null) {
            try {
                project = projectRepository.getReferenceById(task.projectId)
            } catch (ex: EntityNotFoundException) {
                errors.add(ex.message.toString())
            }
        }
        return project
    }

    private fun getCompanyOrClient(
        errors: MutableList<String>,
        project: ProjectRequest
    ): Pair<Company?, Client?> {
        var client: Client? = null
        var company: Company? = null

        if (project.clientId != null) {
            try {
                client = clientRepository.getReferenceById(project.clientId)
            } catch (ex: EntityNotFoundException) {
                errors.add(ex.message.toString())
            }
        } else if (project.companyId != null) {
            try {
                company = companyRepository.getReferenceById(project.companyId)
            } catch (ex: EntityNotFoundException) {
                errors.add(ex.message.toString())
            }
        }

        return company to client
    }

    private fun getTasks(
        errors: MutableList<String>,
        ids: List<Long>
    ): List<Task> {
        val tasks = mutableListOf<Task>()

        ids.forEach {
            try {
                val task = taskRepository.getReferenceById(it)
                tasks.add(task)
            } catch (ex: EntityNotFoundException) {
                errors.add(ex.message.toString())
            }
        }

        return tasks
    }

    private fun Project.calculateDuration(): String {
        val durationMillis = this.tasks.sumOf { it.duration }

        return "${durationMillis / 3600000} hours ${(durationMillis % 3600000) / 60000} minutes"
    }

    private fun TaskRequest.getDurationAsLong(errors: MutableList<String>): Long? {
        val regex = """(\d+)h(\d+)m""".toRegex()
        val match = regex.matchEntire(this.duration)

        val result = match?.destructured?.let { (hours, minutes) ->
            hours.toLongOrNull()?.let { hoursLong ->
                minutes.toLongOrNull()?.let { minutesLong ->
                    (hoursLong * 60 + minutesLong) * 60 * 1000
                }
            }
        }
        if (result == null) {
            errors.add("The provided duration does not match the desired format")
        }

        return result
    }


    companion object {
        private const val PROJECT_PERSISTENCE_ERROR_MESSAGE = "Could not persist project due to validation errors"
        private const val PROJECT_UPDATE_ERROR_MESSAGE = "Could not update project due to validation errors"
        private const val TASK_PERSISTENCE_ERROR_MESSAGE = "Could not persist task due to validation errors"
        private const val TASK_UPDATE_ERROR_MESSAGE = "Could not update task due to validation errors"
    }
}

data class Response(
    val code: Short,
    val data: String,
    @JsonProperty("validation_errors")
    val validationErrors: List<String> = listOf()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectRequest(
    val title: String,
    val description: String,
    val companyId: Long?,
    val clientId: Long?,
    val taskIds: List<Long>
)

data class ProjectResponse(
    val id: Long?,
    val title: String,
    val description: String,
    val company: Company?,
    val client: Client?,
    val duration: String?,
    val status: Status
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskRequest(
    val name: String,
    val status: Status,
    val duration: String,
    val projectId: Long?
)
