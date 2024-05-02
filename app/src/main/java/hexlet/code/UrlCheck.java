package hexlet.code;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UrlCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String h1;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long url_id;

    private LocalDateTime createdAt;

    // Constructors, getters, and setters
    public UrlCheck() {
    }

    public UrlCheck(Long id, String title, String h1, String description, Long url_id, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url_id = url_id;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUrl() {
        return url_id;
    }

    public void setUrl(Long url_id) {
        this.url_id = url_id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
