package com.cdq.task.dtos;

import com.cdq.task.models.CompareItem;
import com.cdq.task.models.TaskStatus;

import lombok.Data;

@Data
public class ResultTaskDto {
    private final int id;
    private final TaskStatus status;
    private final int progress;
    private final CompareItem bestMatchItem;
}
