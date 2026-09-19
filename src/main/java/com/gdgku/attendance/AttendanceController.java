package com.gdgku.attendance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * [문제: 계층 분리(Layering) 부재]
 * API 두 개에서 지각 판정을 하는데 로직이 다르다. 
 * 로직을 서비스에 분리해서 서비스에서 요청하도록 하자.
 * 테스트 돌리면 고쳤는지 알 수 있음.
 */
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private static final LocalTime LATE_CUTOFF = LocalTime.of(9, 10);
    private static final LocalTime ABSENT_CUTOFF = LocalTime.of(9, 30);

    private final List<Attendance> attendances = new ArrayList<>();
    private long nextId = 1L;

    AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public static class Attendance {
        private Long id;
        private String studentName;
        private LocalTime checkInTime;
        private String status;

        public Attendance() {
        }

        public Attendance(Long id, String studentName, LocalTime checkInTime, String status) {
            this.id = id;
            this.studentName = studentName;
            this.checkInTime = checkInTime;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public LocalTime getCheckInTime() {
            return checkInTime;
        }

        public void setCheckInTime(LocalTime checkInTime) {
            this.checkInTime = checkInTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @PostMapping("/check-in")
    public Attendance checkIn(@RequestBody Attendance request) {
        String status = attendanceService.determineStatus(request.getCheckInTime());


        Attendance attendance = new Attendance(nextId++, request.getStudentName(), request.getCheckInTime(), status);
        attendances.add(attendance);
        return attendance;
    }

    @GetMapping
    public List<Attendance> getAttendances() {
        return attendances;
    }

    @GetMapping("/{id}")
    public Attendance getAttendance(@PathVariable Long id) {
        for (Attendance attendance : attendances) {
            if (attendance.getId().equals(id)) {
                return attendance;
            }
        }
        return null;
    }

    @GetMapping("/late-count")
    public long countLate() {
        long count = 0;
        for (Attendance attendance : attendances) {
            if ("LATE".equals(attendance.getStatus())) {
                count++;
            }
        }
        return count;
    }

    // 관리자가 잘못 입력된 출석 시각을 정정하는 API.
    // 지각 판정 로직을 checkIn()과 별개로 다시 구현하다가 경계값 조건(<= vs <)이 미묘하게 달라졌다.
    @PutMapping("/{id}")
    public Attendance updateCheckInTime(@PathVariable Long id, @RequestBody Attendance request) {
        Attendance attendance = getAttendance(id);
        if (attendance == null) {
            return null;
        }

        attendance.setCheckInTime(request.getCheckInTime());

        attendance.setCheckInTime((request.getCheckInTime()));
        attendance.setStatus((attendanceService.determineStatus((request.getCheckInTime()))));

        return attendance;
    }
}
