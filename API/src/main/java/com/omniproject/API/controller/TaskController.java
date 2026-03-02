package com.omniproject.API.controller;

import com.omniproject.API.model.Task;
import com.omniproject.API.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    @PostMapping
    public Task criarTask(@RequestBody Task task) {
        return repository.save(task);
    }

    @GetMapping
    public List<Task> listarTasks() {
        return repository.findAll();
    }
}