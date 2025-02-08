package com.workintech.spring17challenge.dto;

import com.workintech.spring17challenge.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseResponse {
    private Course course;
    private Integer status;
    private String message;

}
