package com.taskmanagement

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api")
class Controller(private val taskService: TaskManagementService) {

    @PostMapping("/projects")
    fun createProject(@RequestBody project: ProjectRequest) =
        ResponseEntity.ok(taskService.createProject(project))

    @PutMapping("/projects/{id}")
    fun updateProject(@RequestBody project: ProjectRequest, @PathVariable id: Long) =
        ResponseEntity.ok(taskService.updateProject(project, id))

    @GetMapping("/projects")
    fun getAllProjects(
        @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val projects = taskService.getAllProjects(pageable)
        model.addAttribute("projects", projects)
        return "projects"
    }

    @DeleteMapping("/projects/{id}")
    fun deleteProject(@PathVariable id: Long) = ResponseEntity.ok(taskService.deleteProject(id))

    @PostMapping("/tasks")
    fun createTask(@RequestBody task: TaskRequest) = ResponseEntity.ok(taskService.createTask(task))

    @PutMapping("/tasks/{id}")
    fun updateTask(@RequestBody task: TaskRequest, @PathVariable id: Long) =
        ResponseEntity.ok(taskService.updateTask(task, id))

    @DeleteMapping("/tasks/{id}")
    fun deleteTask(@PathVariable id: Long) = ResponseEntity.ok(taskService.deleteTask(id))

    @GetMapping("/tasks")
    fun getAllTasks(@PageableDefault(size = 20) pageable: Pageable, model: Model): String {
        val tasks = taskService.getAllTasks(pageable)
        model.addAttribute("tasks", tasks)
        return "tasks"
    }
}
