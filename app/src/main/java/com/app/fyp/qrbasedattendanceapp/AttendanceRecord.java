package com.app.fyp.qrbasedattendanceapp;

public class AttendanceRecord {
    private String studentName;
    private String rollNo;
    private String timestamp;

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    private String attendance;

    // Default constructor (required by Firebase)
    public AttendanceRecord() {
    }

    // Constructor with parameters
    public AttendanceRecord(String studentName, String rollNo, String timestamp, String attendance) {
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.timestamp = timestamp;
        this.attendance = attendance;
    }

    // Getter and Setter methods
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
