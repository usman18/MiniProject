package com.uk.miniproject;

/**
 * Created by usman on 25-09-2018.
 */

public class Student {

    private String name;
    private String grNumber;
    private String university;
    private String examName;
    private String examScore;
    private String acceptanceLetterUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrNumber() {
        return grNumber;
    }

    public void setGrNumber(String grNumber) {
        this.grNumber = grNumber;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamScore() {
        return examScore;
    }

    public void setExamScore(String examScore) {
        this.examScore = examScore;
    }

    public String getAcceptanceLetterUrl() {
        return acceptanceLetterUrl;
    }

    public void setAcceptanceLetterUrl(String acceptanceLetterUrl) {
        this.acceptanceLetterUrl = acceptanceLetterUrl;
    }
}
