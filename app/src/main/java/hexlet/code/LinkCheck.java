package hexlet.code;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LinkCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Новое поле для связи с таблицей urls
    @Column(name = "url_id")
    private Long urlId;

    // Столбец для хранения URL-адреса
    private String url;

    private int statusCode;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime checkedAt;

    @Enumerated(EnumType.STRING)
    private LinkType linkType;

    public enum LinkType {
        INTERNAL,
        EXTERNAL
    }

    // Constructors, getters, and setters
    public LinkCheck() {
    }

    public LinkCheck(String url, int statusCode, LocalDateTime checkedAt, LinkType linkType) {
        this.url = url;
        this.statusCode = statusCode;
        this.checkedAt = checkedAt;
        this.linkType = linkType;
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
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}
