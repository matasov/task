package com.cdq.task;

import com.cdq.task.dtos.ResultTaskDto;
import com.cdq.task.models.CompareItem;
import com.cdq.task.models.TaskStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.key;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class TaskControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TaskService taskService;

        @Test
        public void getAllTasks() throws Exception {
                Map<Integer, ResultTaskDto> tasks = Collections.singletonMap(1,
                                new ResultTaskDto(1, TaskStatus.RUNNING, 50, null));
                when(taskService.getAllTasks()).thenReturn(tasks);

                mockMvc.perform(get("/tasks"))
                                .andExpect(status().isOk())
                                .andExpect(content().json(
                                                "{\"1\":{\"id\":1,\"status\":\"RUNNING\",\"progress\":50,\"bestMatchItem\":null}}"))
                                .andDo(document("tasks/get-all-tasks",
                                                responseFields(
                                                                fieldWithPath("1.id").description("Task id"),
                                                                fieldWithPath("1.status")
                                                                                .description("Status of the task"),
                                                                fieldWithPath("1.progress")
                                                                                .description("Progress of the task"),
                                                                fieldWithPath("1.bestMatchItem")
                                                                                .description("The best match item"))));
        }

        @Test
        public void getTask() throws Exception {
                ResultTaskDto task = new ResultTaskDto(1, TaskStatus.DONE, 100, new CompareItem(0, 0));
                when(taskService.getTaskById(eq(1))).thenReturn(task);

                mockMvc.perform(get("/tasks/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(content().json(
                                                "{\"id\":1,\"status\":\"DONE\",\"progress\":100,\"bestMatchItem\":{\"index\":0,\"typos\":0}}"))
                                .andDo(document("tasks/get-task", pathParameters(
                                                parameterWithName("id").description("Task id")),
                                                responseFields(
                                                                fieldWithPath("id").description("Task id"),
                                                                fieldWithPath("status")
                                                                                .description("Status of the task"),
                                                                fieldWithPath("progress")
                                                                                .description("Progress of the task"),
                                                                fieldWithPath("bestMatchItem")
                                                                                .description("The best match item"),
                                                                fieldWithPath("bestMatchItem.index").description(
                                                                                "Index of the best match item"),
                                                                fieldWithPath("bestMatchItem.typos").description(
                                                                                "Typos of the best match item"))));
        }

        @Test
        public void deleteTask() throws Exception {
                mockMvc.perform(delete("/tasks/{id}", 1))
                                .andExpect(status().isNoContent())
                                .andDo(document("tasks/delete-task", pathParameters(
                                                parameterWithName("id").description("Task id"))));
        }

        @Test
        public void createTask() throws Exception {
                when(taskService.createTask(any())).thenReturn(1);
                mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pattern\":\"ABC\", \"input\":\"ABCD\"}"))
                        .andExpect(status().isCreated())
                        .andExpect(content().json("{\"id\":1}"))
                        .andDo(document("tasks/create-task",
                        requestFields(
                                fieldWithPath("pattern").description("Pattern").attributes(key("mandatory").value("YES")),
                                fieldWithPath("input").description("Input").attributes(key("mandatory").value("YES"))),
                        responseFields(
                                fieldWithPath("id").description("The task id").attributes(key("mandatory").value("YES")))));
        }

}
