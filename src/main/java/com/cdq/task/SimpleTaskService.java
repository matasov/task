package com.cdq.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cdq.task.dtos.InsertTaskDto;
import com.cdq.task.dtos.ResultTaskDto;
import com.cdq.task.exceptions.TaskInterruptedException;
import com.cdq.task.exceptions.TaskNotFoundException;
import com.cdq.task.models.CompareItem;
import com.cdq.task.models.MatchedItem;
import com.cdq.task.models.Task;
import com.cdq.task.models.TaskStatus;
import com.cdq.task.processors.AbstractTaskProcessor;
import com.cdq.task.processors.StreamBasedTaskProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleTaskService implements TaskService, TaskListener {

    private static final String TASK_WAS_INTERRUPTED = "Task with id {} was interrupted";

    private final Map<Integer, AbstractTaskProcessor> processedTasks = new ConcurrentHashMap<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final TaskRepository taskRepository;

    @CacheEvict(value = "tasks", allEntries = true)
    @Override
    public int createTask(InsertTaskDto request) {
        int taskId = createTaskEntity(request.getPattern(), request.getInput());
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(taskId, request.getPattern(), request.getInput(),
                taskRepository, this);
        processedTasks.put(taskId, task);
        executorService.execute(task);
        return taskId;
    }

    private int createTaskEntity(String template, String input) {
        Task task = new Task();
        task.setProgress(0);
        task.setTemplate(template);
        task.setInput(input);
        task.setStatus(TaskStatus.IDLE);
        taskRepository.save(task);
        return task.getId();
    }

    @Override
    public Map<Integer, ResultTaskDto> getAllTasks() {
        return taskRepository.findAll().stream().collect(Collectors.toMap(Task::getId, this::getTaskDto));
    }

    @Override
    public boolean isExistsTask(int id) {
        return taskRepository.existsById(id);
    }

    @Override
    public ResultTaskDto getTaskById(int id) {
        Task task = getTaskEntity(id);
        return getTaskDto(task);
    }

    @Cacheable(cacheNames = "tasks", key = "#id")
    private Task getTaskEntity(int id) {
        validateExistingTask(id);
        return taskRepository.findById(id).get();
    }

    private void validateExistingTask(int id) {
        if (!isExistsTask(id)) {
            throw new TaskNotFoundException(id);
        }
    }

    private ResultTaskDto getTaskDto(Task task) {
        int taskId = task.getId();
        int taskProgress = task.getProgress();
        MatchedItem bestMatchItem = task.getBestMatchItem();
        TaskStatus taskStatus = task.getStatus();
        return new ResultTaskDto(taskId, taskStatus, taskProgress, getCompareItem(bestMatchItem));
    }

    private CompareItem getCompareItem(MatchedItem bestMatchItem) {
        return bestMatchItem == null ? null
                : new CompareItem(bestMatchItem.getBestMatchIndex(),
                        bestMatchItem.getBestMatchTypos());
    }

    @CacheEvict(cacheNames = "tasks", allEntries = true)
    @Override
    public void deleteTask(int id) {
        validateExistingTask(id);
        interruptTask(id);
    }

    private void interruptTask(int id) {
        try {
            AbstractTaskProcessor task = processedTasks.get(id);
            if (task != null) {
                task.interruptTask();
            }
        } catch (TaskInterruptedException exception) {
            log.trace(TASK_WAS_INTERRUPTED, id);
        }
    }

    @Override
    public void onTaskFinished(int id) {
        processedTasks.remove(id);
    }

}
