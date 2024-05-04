package hexlet.code;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LinkChecker {

    public static void checkAndSaveAllInternalLinks(String baseUrl, UrlRepository urlRepository) {
        try {
            Document doc = Jsoup.connect(baseUrl).get();

            for (Element link : doc.select("a[href], button[href]")) {
                String href = link.absUrl("href");
                // Проверяем, является ли ссылка внутренней
                if (isInternalLink(href, baseUrl)) {
                    int statusCode = getStatusCode(href);
                    urlRepository.saveUrlStatus(href, statusCode);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для проверки, является ли ссылка внутренней
    private static boolean isInternalLink(String url, String baseUrl) {
        return url.startsWith(baseUrl);
    }

    // Метод для получения статуса ссылки
    private static int getStatusCode(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
