package com.cdq.task;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdq.task.dtos.InsertTaskDto;
import com.cdq.task.dtos.ResultTaskDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private static final String CREATE_RESPONSE_TEMPLATE = "{\"id\": %d}";

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody InsertTaskDto request) {
        int taskId = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.format(CREATE_RESPONSE_TEMPLATE, taskId));
    }

    @GetMapping
    public ResponseEntity<Map<Integer, ResultTaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultTaskDto> getTask(@PathVariable int id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
