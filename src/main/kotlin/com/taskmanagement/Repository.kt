package com.taskmanagement

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, Long> {
    fun findAllByDeletedFalse(pageable: Pageable): Slice<Project>

    override fun delete(entity: Project) {
        entity.deleted = true
        save(entity)
    }
}

interface TaskRepository : JpaRepository<Task, Long> {
    fun findAllByDeletedFalse(pageable: Pageable): Slice<Task>

    override fun delete(entity: Task) {
        entity.deleted = true
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
    @JsonIgnore
    var client: Client?,

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonIgnore
    var company: Company?,

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    var tasks: List<Task> = mutableListOf(),

    @Column(name = "deleted")
    var deleted: Boolean = false
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
}

@Entity
@Table(name = "tasks")
class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @Column(name = "name")
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: Status,

    @Column(name = "duration")
    var duration: Long,

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    var project: Project?,

    @Column(name = "deleted")
    var deleted: Boolean = false
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
}

enum class Status {
    NEW,
    PENDING,
    FAILED,
    DONE
}
