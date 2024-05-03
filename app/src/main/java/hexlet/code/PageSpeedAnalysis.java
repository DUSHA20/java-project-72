package hexlet.code;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PageSpeedAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long urlId;

    @Column(columnDefinition = "TEXT")
    private String analysisResult;

    private LocalDateTime createdAt;

    // Constructors, getters, and setters
    public PageSpeedAnalysis() {
    }

    public PageSpeedAnalysis(Long id, Long urlId, String analysisResult, LocalDateTime createdAt) {
        this.id = id;
        this.urlId = urlId;
        this.analysisResult = analysisResult;
        this.createdAt = createdAt;
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

    public String getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
