package com.cdq.task;

import java.util.Map;

import com.cdq.task.dtos.InsertTaskDto;
import com.cdq.task.dtos.ResultTaskDto;

public interface TaskService {

    int createTask(InsertTaskDto request);

    Map<Integer, ResultTaskDto> getAllTasks();

    boolean isExistsTask(int id);

    ResultTaskDto getTaskById(int id);

    void deleteTask(int id);
}
