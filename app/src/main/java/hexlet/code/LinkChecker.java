package hexlet.code;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.net.MalformedURLException;

public class LinkChecker {

    public static void checkAndSaveNonWorkingExternalLinks(String baseUrl, UrlRepository urlRepository) {
        try {
            Document doc = Jsoup.connect(baseUrl).get();

            for (Element link : doc.select("a[href]")) {
                String href = link.absUrl("href");
                int statusCode = getStatusCode(href);
                if (statusCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    urlRepository.saveUrlStatus(href, statusCode);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getStatusCode(String url) throws IOException {
        try {
            URL u = new URL(url);
            String protocol = u.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                // Пропускаем URL с неподдерживаемым протоколом
                return -1;
            }

            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            int statusCode = connection.getResponseCode();
            connection.disconnect();
            return statusCode;
        } catch (MalformedURLException e) {
            // Обработка недопустимого URL
            e.printStackTrace();
            return -1;
        }
    }
}
