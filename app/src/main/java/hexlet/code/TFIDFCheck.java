package hexlet.code;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TFIDFCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long url_id;

    private String word;

    private Double tfidf;

    private LocalDateTime createdAt;

    // Constructors, getters, and setters
    public TFIDFCheck() {
    }

    public TFIDFCheck(Long id, Long url_id, String word, Double tfidf, LocalDateTime createdAt) {
        this.id = id;
        this.url_id = url_id;
        this.word = word;
        this.tfidf = tfidf;
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
        return url_id;
    }

    public void setUrlId(Long url_id) {
        this.url_id = url_id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getTfidf() {
        return tfidf;
    }

    public void setTfidf(Double tfidf) {
        this.tfidf = tfidf;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
