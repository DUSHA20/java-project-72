package hexlet.code;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.time.LocalDateTime;

@Entity
public class LinkCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_id")
    private Long urlId;

    private String url;

    private int statusCode;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private LinkType linkType;

    public enum LinkType {
        INTERNAL,
        EXTERNAL
    }

    // Constructors, getters, and setters
    public LinkCheck() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Добавленные методы для работы с новым полем url_id
    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    // Getters and setters для остальных полей
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getCheckedAt() {
        return createdAt;
    }

    public void setCheckedAt(LocalDateTime createdAtParam) {
        this.createdAt = createdAtParam;
    }
}
