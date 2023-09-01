import java.util.*
import jakarta.persistence.*

@Entity
@Table(name = "projects")
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    var projectId: Long,

    @Column(name = "title")
    var title: String,

    @Column(name = "description")
    var description: String,

    @Column(name = "status")
    var status: String,

    @Column(name = "duration")
    var duration: Date,

    @ManyToOne
    @JoinColumn(name = "client_id")
    var client: Client?,

    @ManyToOne
    @JoinColumn(name = "company_id")
    var company: Company?,

    @OneToMany(mappedBy = "project")
    var tasks: List<Task>,

    @Column(name = "is_deleted")
    var isDeleted: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherProject = other as Project
        return projectId == otherProject.projectId
    }

    override fun hashCode(): Int {
        return projectId.hashCode()
    }

    override fun toString(): String {
        return "Project(projectId=$projectId, title='$title', status='$status', description='$description', duration=$duration, isDeleted=$isDeleted, client=${client?.clientName}, company=${company?.companyName})"
    }
}

@Entity
@Table(name = "tasks")
class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    var taskId: Long,

    @Column(name = "task_name")
    var taskName: String,

    @Column(name = "task_description")
    var taskDescription: String,

    @Column(name = "status")
    var status: String,

    @Column(name = "duration")
    var duration: Date,

    @ManyToOne
    @JoinColumn(name = "project_id")
    var project: Project?,

    @Column(name = "is_deleted")
    var isDeleted: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherTask = other as Task
        return taskId == otherTask.taskId
    }

    override fun hashCode(): Int {
        return taskId.hashCode()
    }

    override fun toString(): String {
        return "Task(taskId=$taskId, taskName='$taskName', status='$status', taskDescription='$taskDescription', duration=$duration, isDeleted=$isDeleted)"
    }
}

@Entity
@Table(name = "clients")
class Client(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    var clientId: Long,

    @Column(name = "client_name")
    var clientName: String,

    @Column(name = "contact_info")
    var contactInfo: String,

    @ManyToOne
    @JoinColumn(name = "company_id")
    var company: Company?
) {

    @OneToMany(mappedBy = "client")
    lateinit var projects: List<Project>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherClient = other as Client
        return clientId == otherClient.clientId
    }

    override fun hashCode(): Int {
        return clientId.hashCode()
    }

    override fun toString(): String {
        return "Client(clientId=$clientId, clientName='$clientName', contactInfo='$contactInfo', company=${company?.companyName})"
    }
}

@Entity
@Table(name = "companies")
class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    var companyId: Long,

    @Column(name = "company_name")
    var companyName: String,

    @Column(name = "address")
    var address: String,

    @Column(name = "contact_info")
    var contactInfo: String
) {

    @OneToMany(mappedBy = "company")
    lateinit var clients: List<Client>

    @OneToMany(mappedBy = "company")
    lateinit var projects: List<Project>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val otherCompany = other as Company
        return companyId == otherCompany.companyId
    }

    override fun hashCode(): Int {
        return companyId.hashCode()
    }

    override fun toString(): String {
        return "Company(companyId=$companyId, companyName='$companyName', address='$address', contactInfo='$contactInfo')"
    }
}
