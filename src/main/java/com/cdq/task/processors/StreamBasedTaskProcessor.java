package com.cdq.task.processors;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;

import com.cdq.task.TaskListener;
import com.cdq.task.TaskRepository;
import com.cdq.task.models.CompareItem;

public class StreamBasedTaskProcessor extends AbstractTaskProcessor {

    public StreamBasedTaskProcessor(int id, String pattern, String input, TaskRepository taskRepository,
            TaskListener listener) {
        super(id, pattern, input, taskRepository, listener);
    }

    public void run() {
        validateLengths();
        patternLength = pattern.length();
        bestMatchItem = Optional.of(IntStream.range(0, input.length() - patternLength + 1)
                .parallel()
                .mapToObj(index -> getCompareItem(index, patternLength))
                .min(Comparator.comparingInt(CompareItem::getTypos))
                .orElseGet(() -> new CompareItem(0, patternLength)));
        save();
        taskFinished();
    }

}
