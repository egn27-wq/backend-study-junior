package com.gdgku.enrollment;

import jakarta.validation.constraints.NotBlank;

public class EnrollRequest {

    @NotBlank
    private String studentName;

    public EnrollRequest() {
    }

    public EnrollRequest(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
