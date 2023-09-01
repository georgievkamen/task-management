package com.taskmanagement

import jakarta.persistence.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import kotlin.time.Duration

interface ProjectRepository : JpaRepository<Project, Long> {
    fun findAllByDeletedFalse(pageable: Pageable = Pageable.ofSize(20)): Slice<Project>

    override fun delete(entity: Project) {
        entity.isDeleted = true
        save(entity)
    }
}

interface TaskRepository : JpaRepository<Task, Long> {
    fun findAllByDeletedFalse(pageable: Pageable = Pageable.ofSize(20)): Slice<Task>

    override fun delete(entity: Task) {
        entity.isDeleted = true
        save(entity)
    }
}

interface ClientRepository : JpaRepository<Client, Long>

interface CompanyRepository : JpaRepository<Company, Long>

@Entity
@Table(name = "projects")
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "title")
    var title: String,

    @Column(name = "description")
    var description: String,

    @ManyToOne
    @JoinColumn(name = "client_id")
    var client: Client?,

    @ManyToOne
    @JoinColumn(name = "company_id")
    var company: Company?,

    @OneToMany(mappedBy = "project")
    var tasks: List<Task> = mutableListOf(),

    @Column(name = "is_deleted")
    var isDeleted: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherProject = other as Project
        return id == otherProject.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Project(projectId=$id, title='$title', description='$description', isDeleted=$isDeleted, client=${client?.clientName}, company=${company?.companyName})"
    }
}

@Entity
@Table(name = "tasks")
class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "task_name")
    var taskName: String,

    @Column(name = "task_description")
    var taskDescription: String,

    @Column(name = "status")
    var status: Status,

    @Column(name = "duration")
    var duration: Duration,

    @ManyToOne
    @JoinColumn(name = "project_id")
    var project: Project?,

    @Column(name = "is_deleted")
    var isDeleted: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherTask = other as Task
        return id == otherTask.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Task(taskId=$id, taskName='$taskName', status='$status', taskDescription='$taskDescription', duration=$duration, isDeleted=$isDeleted)"
    }
}

@Entity
@Table(name = "clients")
class Client(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long,

    @Column(name = "client_name")
    var clientName: String,

    @Column(name = "contact_info")
    var contactInfo: String,
) {

    @OneToMany(mappedBy = "client")
    lateinit var projects: List<Project>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherClient = other as Client
        return id == otherClient.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Client(clientId=$id, clientName='$clientName', contactInfo='$contactInfo')"
    }
}

@Entity
@Table(name = "companies")
class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long,

    @Column(name = "company_name")
    var companyName: String,

    @Column(name = "address")
    var address: String,

    @Column(name = "contact_info")
    var contactInfo: String
) {
    @OneToMany(mappedBy = "company")
    lateinit var projects: List<Project>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherCompany = other as Company
        return id == otherCompany.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Company(companyId=$id, companyName='$companyName', address='$address', contactInfo='$contactInfo')"
    }
}

enum class Status {
    NEW,
    PENDING,
    FAILED,
    DONE
}
