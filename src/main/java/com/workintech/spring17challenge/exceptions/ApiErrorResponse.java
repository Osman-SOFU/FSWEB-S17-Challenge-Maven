package com.workintech.spring17challenge.exceptions;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiErrorResponse {
    private Integer status;
    private String message;
    private Long timestamp;
}
