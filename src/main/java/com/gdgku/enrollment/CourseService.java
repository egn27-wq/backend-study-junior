package com.gdgku.enrollment;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class CourseService {

    private final Map<Long, Course> courses = new HashMap<>();
    private long nextCourseId = 1L;

    public CourseResponse createCourse(String name, int capacity) {
        Course course = new Course(nextCourseId++, name, capacity);
        courses.put(course.getId(), course);
        return new CourseResponse(course);
    }

    public CourseResponse getCourse(Long courseId) {
        Course course = findCourseOrThrow(courseId);
        return new CourseResponse(course);
    }

    public CourseResponse enroll(Long courseId, String studentName) {
        Course course = findCourseOrThrow(courseId);

        if (course.getEnrolledCount() >= course.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "정원이 초과되었습니다.");
        }

        if (course.getEnrolledStudents().contains(studentName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신청한 강의입니다.");
        }

        course.getEnrolledStudents().add(studentName.trim());
        return new CourseResponse(course);
    }

    private Course findCourseOrThrow(Long courseId) {
        Course course = courses.get(courseId);
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다.");
        }
        return course;
    }
}