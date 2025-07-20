package com.hybridata.revisegpt.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.hybridata.revisegpt.domain.KnowledgeBase} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KnowledgeBaseDTO implements Serializable {

    private Long id;

    private String fileName;

    private String title;

    private String description;

    private Boolean embed;

    private String vectorId;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private String fileUrl;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEmbed() {
        return embed;
    }

    public void setEmbed(Boolean embed) {
        this.embed = embed;
    }

    public String getVectorId() {
        return vectorId;
    }

    public void setVectorId(String vectorId) {
        this.vectorId = vectorId;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
        if (!(o instanceof KnowledgeBaseDTO)) {
            return false;
        }

        KnowledgeBaseDTO knowledgeBaseDTO = (KnowledgeBaseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, knowledgeBaseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KnowledgeBaseDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", embed='" + getEmbed() + "'" +
            ", vectorId='" + getVectorId() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
