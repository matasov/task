package com.cdq.task.models;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import com.cdq.task.EmptyTaskListener;
import com.cdq.task.LocalPostgreSQLContainer;
import com.cdq.task.TaskApplication;
import com.cdq.task.TaskRepository;
import com.cdq.task.exceptions.WrongLengthsException;
import com.cdq.task.processors.AbstractTaskProcessor;
import com.cdq.task.processors.StreamBasedTaskProcessor;

@ActiveProfiles("test")
@SpringBootTest(classes = TaskApplication.class)
@DisplayName("TaskTest")
@Tag("task")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TaskTest {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = LocalPostgreSQLContainer.getInstance();

    @Autowired
    private TaskRepository taskRepository;

    private final static EmptyTaskListener listener = new EmptyTaskListener();

    @Test
    void testRun() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(100, "DAM", "TADAM", taskRepository, listener);
        task.run();
        Assertions.assertTrue(task.getBestMatchItem().isPresent());
        Assertions.assertEquals(2, task.getBestMatchItem().get().getIndex());
        Assertions.assertEquals(0, task.getBestMatchItem().get().getTypos());
    }

    @Test
    void testDelete() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(1, "ABC", "ABCABC", taskRepository, listener);
        new Thread(task).start();
        task.interruptTask();
        Assertions.assertEquals(task.getStatus(), TaskStatus.DELETED);
    }

    @Test
    void testWrongLengths() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(2, "ABC", "AB", taskRepository, listener);
        assertThrows(WrongLengthsException.class, task::run);
    }

    @Test
    void testMatchWithoutTypos() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(3, "BCD", "ABCD", taskRepository, listener);
        task.run();
        Assertions.assertTrue(task.getBestMatchItem().isPresent());
        Assertions.assertEquals(1, task.getBestMatchItem().get().getIndex());
        Assertions.assertEquals(0, task.getBestMatchItem().get().getTypos());
    }

    @Test
    void testMatchWithOneTypo() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(4, "BWD", "ABCD", taskRepository, listener);
        task.run();
        Assertions.assertTrue(task.getBestMatchItem().isPresent());
        Assertions.assertEquals(1, task.getBestMatchItem().get().getIndex());
        Assertions.assertEquals(1, task.getBestMatchItem().get().getTypos());
    }

    @Test
    void testMatchWithBestSubstring() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(5, "CFG", "ABCDEFG", taskRepository, listener);
        task.run();
        Assertions.assertTrue(task.getBestMatchItem().isPresent());
        Assertions.assertEquals(4, task.getBestMatchItem().get().getIndex());
        Assertions.assertEquals(1, task.getBestMatchItem().get().getTypos());
    }

    @Test
    void testMultipleExactMatches() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(4, "ABC", "ABCABC", taskRepository, listener);
        task.run();
        Assertions.assertTrue(task.getBestMatchItem().isPresent());
        Assertions.assertEquals(0, task.getBestMatchItem().get().getIndex());
        Assertions.assertEquals(0, task.getBestMatchItem().get().getTypos());
    }

    @Test
    void testMultipleMatchesFirstBetter() {
        AbstractTaskProcessor task = new StreamBasedTaskProcessor(5, "TDD", "ABCDEFG", taskRepository, listener);
        task.run();
        Assertions.assertTrue(task.getBestMatchItem().isPresent());
        Assertions.assertEquals(1, task.getBestMatchItem().get().getIndex());
        Assertions.assertEquals(2, task.getBestMatchItem().get().getTypos());
    }

}
