package com.cdq.task;

import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import com.cdq.task.dtos.InsertTaskDto;
import com.cdq.task.dtos.ResultTaskDto;
import com.cdq.task.models.TaskStatus;

@ActiveProfiles("test")
@SpringBootTest(classes = TaskApplication.class)
@DisplayName("SimpleTaskServiceTest")
@Tag("service")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SimpleTaskServiceIt {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = LocalPostgreSQLContainer.getInstance();

    @Autowired
    private TaskService taskService;

    @Test
    public void testInsert() throws InterruptedException {
        InsertTaskDto dto = new InsertTaskDto();
        dto.setPattern("ABC");
        dto.setInput("AHCABC");
        int taskId = taskService.createTask(dto);
        Thread.sleep(2000);
        ResultTaskDto result = taskService.getTaskById(taskId);
        assertEquals(result.getStatus(), TaskStatus.DONE);
        assertEquals(result.getProgress(), 100);
        assertEquals(result.getBestMatchItem().getIndex(), 3);
        assertEquals(result.getBestMatchItem().getTypos(), 0);
    }

    @Test
    public void testDelete(){
        InsertTaskDto dto = new InsertTaskDto();
        dto.setPattern("ABC");
        dto.setInput("AHCAREEGSDFGRTGBC");
        int taskId = taskService.createTask(dto);
        taskService.deleteTask(taskId);
        ResultTaskDto result = taskService.getTaskById(taskId);
        System.out.println(result);
        assertEquals(result.getStatus(), TaskStatus.DELETED);
    }

}
