package com.hybridata.revisegpt.service.dto;

import com.hybridata.revisegpt.domain.enumeration.ServiceType;
import com.hybridata.revisegpt.domain.enumeration.SessionStatus;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.hybridata.revisegpt.domain.Session} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SessionDTO implements Serializable {

    private Long id;

    private ServiceType type;

    private SessionStatus sessionStatus;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private ZonedDateTime endedAt;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ZonedDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(ZonedDateTime endedAt) {
        this.endedAt = endedAt;
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
        if (!(o instanceof SessionDTO)) {
            return false;
        }

        SessionDTO sessionDTO = (SessionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, sessionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SessionDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", sessionStatus='" + getSessionStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", endedAt='" + getEndedAt() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
