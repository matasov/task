package com.cdq.task.processors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.cdq.task.TaskListener;
import com.cdq.task.TaskRepository;
import com.cdq.task.exceptions.TaskInterruptedException;
import com.cdq.task.exceptions.WrongLengthsException;
import com.cdq.task.models.CompareItem;
import com.cdq.task.models.MatchedItem;
import com.cdq.task.models.Task;
import com.cdq.task.models.TaskStatus;

public abstract class AbstractTaskProcessor implements Runnable {

    private static final String WRONG_LENGTHS = "Input length must be greater than or equal to pattern length";

    protected final int id;
    protected final String pattern;
    protected final String input;
    protected int patternLength;
    protected final AtomicInteger progress = new AtomicInteger(0);
    protected Optional<CompareItem> bestMatchItem = Optional.empty();
    protected final AtomicInteger isInterruptedTask = new AtomicInteger(0);
    private final TaskRepository taskRepository;
    protected final TaskListener listener;

    public AbstractTaskProcessor(int id, String pattern, String input, TaskRepository taskRepository,
            TaskListener listener) {
        this.id = id;
        this.pattern = pattern;
        this.input = input;
        this.taskRepository = taskRepository;
        this.listener = listener;
    }

    public int getId() {
        return id;
    }

    public Optional<CompareItem> getBestMatchItem() {
        return bestMatchItem;
    }

    protected void validateLengths() {
        if (input.length() < pattern.length()) {
            throw new WrongLengthsException(WRONG_LENGTHS);
        }
    }

    protected CompareItem getCompareItem(int inputIndex, int patternLength) {
        checkInterruption();
        delayOperation();
        String inputSubstring = input.substring(inputIndex, inputIndex + patternLength);
        int amountTypoSymbols = (int) IntStream.range(0, patternLength)
                .filter(charIndex -> inputSubstring.charAt(charIndex) != pattern.charAt(charIndex))
                .count();
        progress.incrementAndGet();
        saveItemStatus();
        return new CompareItem(inputIndex, amountTypoSymbols);
    }

    public int getProgress() {
        BigDecimal progressDecimal = BigDecimal.valueOf(progress.get());
        BigDecimal lengthDecimal = BigDecimal.valueOf(input.length() - patternLength + 1);
        BigDecimal percentage = progressDecimal.divide(lengthDecimal, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return percentage.intValue();
    }

    public void interruptTask() {
        isInterruptedTask.incrementAndGet();
        taskFinished();
        save();
    }

    private void checkInterruption() {
        if (isInterrupted()) {
            throw new TaskInterruptedException();
        }
    }

    private boolean isInterrupted() {
        return isInterruptedTask.get() > 0;
    }

    private void delayOperation() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public TaskStatus getStatus() {
        int progress = getProgress();
        TaskStatus result;
        if (isInterrupted()) {
            result = TaskStatus.DELETED;
        } else if (progress == 0) {
            result = TaskStatus.IDLE;
        } else if (progress < 100) {
            result = TaskStatus.RUNNING;
        } else if (progress >= 100 && bestMatchItem.isPresent()) {
            result = TaskStatus.DONE;
        } else {
            result = TaskStatus.ERROR;
        }
        return result;
    }

    public void saveItemStatus() {
        if (!isInterrupted()) {
            save();
        }
    }

    public synchronized void save() {
        Task task = createTaskEntity();
        taskRepository.save(task);
    }

    private Task createTaskEntity() {
        Task task = new Task();
        task.setId(this.id);
        task.setProgress(getProgress());
        task.setTemplate(this.pattern);
        task.setInput(this.input);
        task.setStatus(getStatus());
        task.setBestMatchItem(getMatchedItemEntity());
        return task;
    }

    private MatchedItem getMatchedItemEntity() {
        if (!bestMatchItem.isPresent()) {
            return null;
        } else {
            MatchedItem matchedItem = new MatchedItem();
            matchedItem.setBestMatchIndex(bestMatchItem.get().getIndex());
            matchedItem.setBestMatchTypos(bestMatchItem.get().getTypos());
            return matchedItem;
        }
    }

    protected void taskFinished() {
        listener.onTaskFinished(id);
    }

}
