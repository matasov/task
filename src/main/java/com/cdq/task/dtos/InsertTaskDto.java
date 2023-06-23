package com.cdq.task.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
public class InsertTaskDto {
    @Size(min = 1, message = "Pattern must not be empty")
    private String pattern;
    @Size(min = 1, message = "Input must not be empty")
    private String input;
}
