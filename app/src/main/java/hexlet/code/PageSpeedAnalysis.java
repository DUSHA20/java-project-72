package hexlet.code;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PageSpeedAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_id")
    private Long urlId;

    @Column(name = "load_time")
    private Long loadTime;

    @Column(name = "content_length")
    private Integer contentLength;

    @Column(name = "request_count")
    private Integer requestCount;

    @Column(name = "is_cdn_used")
    private Boolean isCdnUsed;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // Constructors, getters, and setters
    public PageSpeedAnalysis() {
    }

    public PageSpeedAnalysis(Long urlId, Long loadTime, Integer contentLength, Integer requestCount, Boolean isCdnUsed) {
        this.urlId = urlId;
        this.loadTime = loadTime;
        this.contentLength = contentLength;
        this.requestCount = requestCount;
        this.isCdnUsed = isCdnUsed;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public Long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(Long loadTime) {
        this.loadTime = loadTime;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Boolean getCdnUsed() {
        return isCdnUsed;
    }

    public void setCdnUsed(Boolean cdnUsed) {
        isCdnUsed = cdnUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
