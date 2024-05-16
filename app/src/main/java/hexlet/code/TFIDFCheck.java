package hexlet.code;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
public class TFIDFCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long url_id;

    private String word;

    private BigDecimal tfidf;

    private LocalDateTime createdAt;

    public TFIDFCheck() {
    }

    public TFIDFCheck(Long id, Long url_id, String word, BigDecimal tfidf, LocalDateTime createdAt) {
        this.id = id;
        this.url_id = url_id;
        this.word = word;
        this.tfidf = tfidf;
        this.createdAt = createdAt;
    }

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

    public BigDecimal getTfidf() {
        return tfidf;
    }

    public void setTfidf(BigDecimal tfidf) {
        this.tfidf = tfidf;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
