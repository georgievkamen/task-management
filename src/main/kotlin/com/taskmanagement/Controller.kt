package com.taskmanagement

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class Controller(private val taskService: TaskManagementService) {

    @PostMapping("/projects")
    fun createProject(@RequestBody project: ProjectRequest) =
        ResponseEntity.ok(taskService.createProject(project))

    @PutMapping("/projects/{id}")
    fun updateProject(@RequestBody project: ProjectRequest, @PathVariable id: Long) =
        ResponseEntity.ok(taskService.updateProject(project, id))

    @GetMapping("/projects")
    fun getAllProjects() = ResponseEntity.ok(taskService.getAllProjects())

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
    fun getAllTasks() = ResponseEntity.ok(taskService.getAllTasks())
}
