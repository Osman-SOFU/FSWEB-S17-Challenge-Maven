package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.dto.CourseResponse;
import com.workintech.spring17challenge.entity.Course;
import com.workintech.spring17challenge.exceptions.ApiErrorResponse;
import com.workintech.spring17challenge.exceptions.ApiException;
import com.workintech.spring17challenge.model.HighCourseGpa;
import com.workintech.spring17challenge.model.LowCourseGpa;
import com.workintech.spring17challenge.model.MediumCourseGpa;
import com.workintech.spring17challenge.validation.CourseValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/courses")
public class CourseController {

    private List<Course> courses = new ArrayList<>();
    private final LowCourseGpa lowCourseGpa = new LowCourseGpa();
    private final MediumCourseGpa mediumCourseGpa = new MediumCourseGpa();
    private final HighCourseGpa highCourseGpa = new HighCourseGpa();

    @GetMapping
    public List<Course> getAllCourses() {
        return courses;
    }

    @GetMapping("/{name}")
    public ResponseEntity<Course> getCourseByName(@PathVariable String name) {
        CourseValidation.checkCourseExistence(courses, name, true); // **Validation çağrıldı**

        return courses.stream()
                .filter(course -> course.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addCourse(@RequestBody Course course) {
        try {
            // Eğer aynı isimde bir kurs varsa, önce kaldır
            courses.removeIf(existingCourse -> existingCourse.getName().equalsIgnoreCase(course.getName()));

            // Validasyon Kontrolleri
            CourseValidation.isValid(course, courses);

            course.setId(courses.size() + 1);
            courses.add(course);

            // **Total GPA Hesaplama**
            double totalGpa = calculateTotalGpa(course);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CourseResponse(course, 201, "Kurs başarıyla eklendi. TotalGPA: " + totalGpa));
        } catch (ApiException e) {
            log.error("HATA: " + e.getMessage());
            return ResponseEntity.status(e.getHttpStatus())
                    .body(new ApiErrorResponse(e.getHttpStatus().value(), e.getMessage(), System.currentTimeMillis()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Integer id, @RequestBody Course updatedCourse) {
        Optional<Course> existingCourse = courses.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst();

        if (existingCourse.isPresent()) {
            Course course = existingCourse.get();
            course.setName(updatedCourse.getName());
            course.setCredit(updatedCourse.getCredit());
            course.setGrade(updatedCourse.getGrade());

            // **Total GPA Hesaplama**
            double totalGpa = calculateTotalGpa(course);

            return ResponseEntity.ok(new CourseResponse(course, 200, "Kurs başarıyla güncellendi. TotalGPA: " + totalGpa));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CourseResponse(null, 404, "Güncellenecek kurs bulunamadı."));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourseById(@PathVariable Integer id) {
        Optional<Course> courseToRemove = courses.stream()
                .filter(course -> course.getId().equals(id))
                .findFirst();

        if (courseToRemove.isPresent()) {
            Course deletedCourse = courseToRemove.get();
            courses.remove(deletedCourse);

            return ResponseEntity.ok().body(new CourseResponse(deletedCourse, 200, "Kurs başarıyla silindi."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponse(404, "Kurs bulunamadı: " + id, System.currentTimeMillis()));
        }
    }

    private double calculateTotalGpa(Course course) {
        if (course.getCredit() <= 2) {
            return course.getGrade().getCoefficient() * course.getCredit() * lowCourseGpa.getGpa();
        } else if (course.getCredit() == 3) {
            return course.getGrade().getCoefficient() * course.getCredit() * mediumCourseGpa.getGpa();
        } else {
            return course.getGrade().getCoefficient() * course.getCredit() * highCourseGpa.getGpa();
        }
    }

}
