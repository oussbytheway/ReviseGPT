package com.hybridata.revisegpt.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.hybridata.revisegpt.domain.CourseHistory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseHistoryDTO implements Serializable {

    private Long id;

    private String generatedResponse;

    private String generatedFileUrl;

    private String inputFileUrl;

    private Integer tokenUsage;

    private Integer responseTimeMs;

    private Integer feedbackRating;

    private ZonedDateTime createdAt;

    private SessionDTO session;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneratedResponse() {
        return generatedResponse;
    }

    public void setGeneratedResponse(String generatedResponse) {
        this.generatedResponse = generatedResponse;
    }

    public String getGeneratedFileUrl() {
        return generatedFileUrl;
    }

    public void setGeneratedFileUrl(String generatedFileUrl) {
        this.generatedFileUrl = generatedFileUrl;
    }

    public String getInputFileUrl() {
        return inputFileUrl;
    }

    public void setInputFileUrl(String inputFileUrl) {
        this.inputFileUrl = inputFileUrl;
    }

    public Integer getTokenUsage() {
        return tokenUsage;
    }

    public void setTokenUsage(Integer tokenUsage) {
        this.tokenUsage = tokenUsage;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SessionDTO getSession() {
        return session;
    }

    public void setSession(SessionDTO session) {
        this.session = session;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseHistoryDTO)) {
            return false;
        }

        CourseHistoryDTO courseHistoryDTO = (CourseHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseHistoryDTO{" +
            "id=" + getId() +
            ", generatedResponse='" + getGeneratedResponse() + "'" +
            ", generatedFileUrl='" + getGeneratedFileUrl() + "'" +
            ", inputFileUrl='" + getInputFileUrl() + "'" +
            ", tokenUsage=" + getTokenUsage() +
            ", responseTimeMs=" + getResponseTimeMs() +
            ", feedbackRating=" + getFeedbackRating() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", session=" + getSession() +
            ", user=" + getUser() +
            "}";
    }
}
