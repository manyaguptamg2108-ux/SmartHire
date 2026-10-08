package model;

import model.enums.InterviewStatus;
import model.enums.InterviewType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Interview model class representing scheduled candidate interview rounds.
 */
public class Interview {

    private int id;
    private int applicationId;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private InterviewType interviewType;
    private String meetingLink;
    private String location;
    private String notes;
    private InterviewStatus status;
    private LocalDateTime createdAt;

    public Interview() {
        this.status = InterviewStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
    }

    public Interview(int id, int applicationId, LocalDate interviewDate, LocalTime interviewTime,
                     InterviewType interviewType, String meetingLink, String location,
                     String notes, InterviewStatus status) {
        this.id = id;
        this.applicationId = applicationId;
        setInterviewDate(interviewDate);
        setInterviewTime(interviewTime);
        setInterviewType(interviewType);
        this.meetingLink = meetingLink;
        this.location = location;
        this.notes = notes;
        setStatus(status != null ? status : InterviewStatus.SCHEDULED);
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = Objects.requireNonNull(interviewDate, "Interview date is required.");
    }

    public LocalTime getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(LocalTime interviewTime) {
        this.interviewTime = Objects.requireNonNull(interviewTime, "Interview time is required.");
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(InterviewType interviewType) {
        this.interviewType = Objects.requireNonNull(interviewType, "Interview type is required.");
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = Objects.requireNonNull(status, "Interview status is required.");
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Interview{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", interviewDate=" + interviewDate +
                ", interviewTime=" + interviewTime +
                ", interviewType=" + interviewType +
                ", status=" + status +
                '}';
    }
}
