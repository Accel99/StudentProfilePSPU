package com.example.dbrequestclass;

public class StudentInfo {
    public long studentId;
    public String firstname;
    public String lastname;
    public String middlename;
    public String group;
    public String faculty;
    public String specialty;
    public String profileName;
    public long planNum;

    public String getFullName() {
        return lastname + " " + firstname + " " + middlename;
    }

    public String getDirection() {
        return specialty + " " + profileName;
    }
}
