package com.hybridata.revisegpt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CourseHistory.
 */
@Entity
@Table(name = "course_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "generated_response")
    private String generatedResponse;

    @Column(name = "generated_file_url")
    private String generatedFileUrl;

    @Column(name = "input_file_url")
    private String inputFileUrl;

    @Column(name = "token_usage")
    private Integer tokenUsage;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "feedback_rating")
    private Integer feedbackRating;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Session session;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneratedResponse() {
        return this.generatedResponse;
    }

    public CourseHistory generatedResponse(String generatedResponse) {
        this.setGeneratedResponse(generatedResponse);
        return this;
    }

    public void setGeneratedResponse(String generatedResponse) {
        this.generatedResponse = generatedResponse;
    }

    public String getGeneratedFileUrl() {
        return this.generatedFileUrl;
    }

    public CourseHistory generatedFileUrl(String generatedFileUrl) {
        this.setGeneratedFileUrl(generatedFileUrl);
        return this;
    }

    public void setGeneratedFileUrl(String generatedFileUrl) {
        this.generatedFileUrl = generatedFileUrl;
    }

    public String getInputFileUrl() {
        return this.inputFileUrl;
    }

    public CourseHistory inputFileUrl(String inputFileUrl) {
        this.setInputFileUrl(inputFileUrl);
        return this;
    }

    public void setInputFileUrl(String inputFileUrl) {
        this.inputFileUrl = inputFileUrl;
    }

    public Integer getTokenUsage() {
        return this.tokenUsage;
    }

    public CourseHistory tokenUsage(Integer tokenUsage) {
        this.setTokenUsage(tokenUsage);
        return this;
    }

    public void setTokenUsage(Integer tokenUsage) {
        this.tokenUsage = tokenUsage;
    }

    public Integer getResponseTimeMs() {
        return this.responseTimeMs;
    }

    public CourseHistory responseTimeMs(Integer responseTimeMs) {
        this.setResponseTimeMs(responseTimeMs);
        return this;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Integer getFeedbackRating() {
        return this.feedbackRating;
    }

    public CourseHistory feedbackRating(Integer feedbackRating) {
        this.setFeedbackRating(feedbackRating);
        return this;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public CourseHistory createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public CourseHistory session(Session session) {
        this.setSession(session);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CourseHistory user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseHistory)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseHistory) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseHistory{" +
            "id=" + getId() +
            ", generatedResponse='" + getGeneratedResponse() + "'" +
            ", generatedFileUrl='" + getGeneratedFileUrl() + "'" +
            ", inputFileUrl='" + getInputFileUrl() + "'" +
            ", tokenUsage=" + getTokenUsage() +
            ", responseTimeMs=" + getResponseTimeMs() +
            ", feedbackRating=" + getFeedbackRating() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
