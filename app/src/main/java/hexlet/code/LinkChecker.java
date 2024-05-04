package hexlet.code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.sql.SQLException;

public class LinkChecker {
    private static HttpClient httpClient;
    private static String baseUrl;
    private static UrlRepository urlRepository;
    private static Set<String> allLinks;

    public LinkChecker(String baseUrl, UrlRepository urlRepository) {
        this.httpClient = HttpClients.createDefault();
        this.baseUrl = baseUrl;
        this.urlRepository = urlRepository;
        this.allLinks = new HashSet<>();
    }

    public static void checkLinks(String url) throws SQLException {
        try {
            // Проверяем доступность всех ссылок
            Set<String> internalLinks = new HashSet<>();
            internalLinks.add(url);
            getAllInternalLinks(url, internalLinks);
            for (String internalLink : internalLinks) {
                int statusCode = getStatusCode(internalLink);
                LinkCheck.LinkType linkType = isInternalLink(internalLink) ? LinkCheck.LinkType.INTERNAL : LinkCheck.LinkType.EXTERNAL;
                urlRepository.saveUrlStatus(internalLink, statusCode, linkType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Проверяем доступность внешних ссылок
            for (String externalLink : allLinks) {
                try {
                    String[] parts = externalLink.split("\\|");
                    String link = parts[0];
                    int statusCode = Integer.parseInt(parts[1]);
                    LinkCheck.LinkType linkType = isInternalLink(link) ? LinkCheck.LinkType.INTERNAL : LinkCheck.LinkType.EXTERNAL;
                    urlRepository.saveUrlStatus(link, statusCode, linkType);
                } catch (SQLException e) {
                    e.printStackTrace(); // Обработка SQLException здесь
                }
            }
        }
    }

    private static void getAllInternalLinks(String url, Set<String> internalLinks) throws IOException {
        Document doc = Jsoup.connect(url).get();
        for (Element link : doc.select("a[href]")) {
            String href = link.attr("abs:href");
            if (isInternalLink(href) && !internalLinks.contains(href)) {
                internalLinks.add(href);
                getAllInternalLinks(href, internalLinks);
            } else if (isExternalLink(href)) {
                int statusCode = getStatusCode(href);
                allLinks.add(href + "|" + statusCode);
            }
        }
    }

    private static boolean isExternalLink(String url) {
        try {
            URI baseUri = new URI(baseUrl);
            URI linkUri = new URI(url);
            return !baseUri.getHost().equalsIgnoreCase(linkUri.getHost());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return true; // В случае ошибки считаем ссылку внешней
        }
    }

    private static boolean isInternalLink(String url) {
        return !isExternalLink(url);
    }

    private static int getStatusCode(String url) throws IOException {
        // Проверяем доступность страницы по URL
        URL u = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        int statusCode = connection.getResponseCode();
        connection.disconnect();
        return statusCode;
    }

}
