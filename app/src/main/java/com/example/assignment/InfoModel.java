package com.example.assignment;

public class InfoModel {
    private String name;
    private String location;
    private String department;
    private String profileUrl;
    private String scheduled;
    private boolean in_office;

    public InfoModel(String name, String location, String department, String profileUrl, String scheduled, boolean in_office) {
        this.name = name;
        this.location = location;
        this.department = department;
        this.profileUrl = profileUrl;
        this.scheduled = scheduled;
        this.in_office = in_office;
    }

    public String getScheduled() {
        return scheduled;
    }

    public Boolean getInOffice() {
        return in_office;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDepartment() {
        return department;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
