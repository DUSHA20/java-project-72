package hexlet.code;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;

public class UrlChecker {

    public static void checkAndSaveUrlInfo(String url, UrlRepository urlRepository) {
        try {
            // Получаем содержимое веб-страницы с помощью Jsoup
            Document document = Jsoup.connect(url).get();

            // Извлекаем заголовок страницы
            String title = document.title();

            // Извлекаем заголовок H1 страницы
            Element h1Element = document.selectFirst("h1");
            String h1 = (h1Element != null) ? h1Element.text() : null;

            // Извлекаем описание страницы из метатега
            Element metaDescription = document.selectFirst("meta[name=description]");
            String description = (metaDescription != null) ? metaDescription.attr("content") : null;

            // Добавляем информацию в базу данных
            urlRepository.addUrlCheck(url, title, h1, description);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
