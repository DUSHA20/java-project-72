package hexlet.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PageSpeedAnalyzer {

    private static final String CDN_HEADER = "X-CDN";

    public static void analyzePage(String url, UrlRepository urlRepository) {
        try {
            // Устанавливаем соединение с URL-адресом
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Засекаем время начала загрузки страницы
            long startTime = System.currentTimeMillis();

            // Получаем ответ от сервера
            int responseCode = connection.getResponseCode();

            // Засекаем время окончания загрузки страницы
            long endTime = System.currentTimeMillis();

            // Размер страницы
            int contentLength = getPageContentLength(url);

            // Количество запросов к серверу
            int requestCount = connection.getHeaderFieldInt("X-Total-Requests", 0);

            // Время загрузки страницы
            long loadTime = endTime - startTime;

            // Проверяем наличие заголовка CDN
            String cdnUsage = connection.getHeaderField(CDN_HEADER);
            boolean isCDNUsed = (cdnUsage != null && !cdnUsage.isEmpty());

            // Добавляем данные в базу данных
            urlRepository.addPageAnalysis(url, loadTime, contentLength, requestCount, isCDNUsed);

        } catch (IOException e) {
            // Обрабатываем ошибку
            e.printStackTrace();
        }
    }

    public static int getPageContentLength(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Читаем содержимое страницы
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();

            // Возвращаем размер содержимого страницы
            return content.toString().getBytes().length;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // или какой-то другой код ошибки
        }
    }
}
