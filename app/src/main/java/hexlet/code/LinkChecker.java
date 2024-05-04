package hexlet.code;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LinkChecker {

    public static void checkAndSaveNonWorkingExternalLinks(String baseUrl, UrlRepository urlRepository) {
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                String contentType = connection.getContentType();
                if (contentType != null && contentType.startsWith("text/html")) {
                    // Если тип контента HTML, ищем все ссылки
                    Document doc = Jsoup.connect(baseUrl).get();
                    for (Element link : doc.select("a[href]")) {
                        String href = link.attr("abs:href");
                        // Проверяем, является ли ссылка внешней
                        if (!isInternalLink(href, baseUrl)) {
                            // Проверяем, работает ли внешняя ссылка
                            int linkStatusCode = getStatusCode(href);
                            if (linkStatusCode != HttpURLConnection.HTTP_OK) {
                                // Сохраняем нерабочую внешнюю ссылку в базу данных с полученным статус-кодом
                                urlRepository.saveUrlStatus(href, linkStatusCode);
                            }
                        }
                    }
                }
            }
            connection.disconnect();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isInternalLink(String url, String baseUrl) {
        try {
            URL base = new URL(baseUrl);
            URL link = new URL(url);
            return base.getHost().equalsIgnoreCase(link.getHost());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int getStatusCode(String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        int statusCode = connection.getResponseCode();
        connection.disconnect();
        return statusCode;
    }
}
