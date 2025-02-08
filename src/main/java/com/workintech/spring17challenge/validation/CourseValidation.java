package com.workintech.spring17challenge.validation;

import com.workintech.spring17challenge.entity.Course;
import com.workintech.spring17challenge.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;

@Slf4j
public class CourseValidation {

    // Kurs adının tekrar edilmediğini ve kredi değerinin uygun olduğunu kontrol eder
    public static void isValid(Course course, List<Course> courses) {
        if (courses.stream().anyMatch(c -> c.getName().equalsIgnoreCase(course.getName()))) {
            log.error("HATA: Aynı isimde kurs eklenemez: " + course.getName());
            throw new ApiException("Kurs ismi zaten var: " + course.getName(), HttpStatus.BAD_REQUEST);
        }

        if (course.getCredit() < 0 || course.getCredit() > 4) {
            log.error("HATA: Kredi değeri 0-4 arasında olmalı: " + course.getCredit());
            throw new ApiException("Kredi değeri hiçbir şekilde 0'dan küçük yada 4'ten büyük olamaz.", HttpStatus.BAD_REQUEST);
        }
    }

    // Belirtilen isme sahip bir kurs olup olmadığını kontrol eder
    public static void checkCourseExistence(List<Course> courses, String name, boolean shouldExist) {
        boolean exists = courses.stream().anyMatch(course -> course.getName().equalsIgnoreCase(name));

        if (shouldExist && !exists) {
            throw new ApiException("Kurs bulunamadı: " + name, HttpStatus.NOT_FOUND);
        } else if (!shouldExist && exists) {
            throw new ApiException("Bu isimde bir kurs zaten mevcut: " + name, HttpStatus.BAD_REQUEST);
        }
    }
}
