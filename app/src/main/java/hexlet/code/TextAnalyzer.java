package hexlet.code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.SQLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import hexlet.code.TFIDFCheck;

public class TextAnalyzer {
    public static void calculateAndSaveTFIDFForUrl(String url, UrlRepository urlRepository) {
        try {
            String text = extractTextFromUrl(url);
            Map<String, Double> tfidfMap = calculateTFIDF(text);
            urlRepository.addTFIDFCheck(url, tfidfMap); // Сохраняем результаты TF-IDF в базу данных
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String extractTextFromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return document.text();
    }

    public static Map<String, Double> calculateTFIDF(String text) {
        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        Map<String, Double> tfMap = calculateTF(words);
        Map<String, Double> idfMap = calculateIDF(words);
        Map<String, Double> tfidfMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : tfMap.entrySet()) {
            String word = entry.getKey();
            double tfidf = entry.getValue() * idfMap.getOrDefault(word, 0.0);
            tfidfMap.put(word, tfidf);
        }

        return tfidfMap;
    }

    public static Map<String, Double> calculateTF(String[] words) {
        Map<String, Integer> wordCountMap = new HashMap<>();
        Map<String, Double> tfMap = new HashMap<>();

        for (String word : words) {
            wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
        }

        int totalCount = words.length;
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            String word = entry.getKey();
            double tf = (double) entry.getValue() / totalCount;
            tfMap.put(word, tf);
        }

        return tfMap;
    }

    public static Map<String, Double> calculateIDF(String[] words) {
        Map<String, Integer> documentCountMap = new HashMap<>();
        Map<String, Double> idfMap = new HashMap<>();
        int totalDocuments = 1;

        for (String word : words) {
            documentCountMap.put(word, 1);
        }

        for (Map.Entry<String, Integer> entry : documentCountMap.entrySet()) {
            String word = entry.getKey();
            double idf = Math.log((double) totalDocuments / entry.getValue());
            idfMap.put(word, idf);
        }

        return idfMap;
    }
}
