package hexlet.code;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PageSpeedAnalyzer {

    private static final String API_KEY = "AIzaSyATMIvPccvzI5MxsejIq8cPS41hmx8-MLs";
    private static final String API_ENDPOINT = "https://www.googleapis.com/pagespeedonline/v5/runPagespeed";

    public static void analyzePageSpeed(String url, UrlRepository urlRepository) {
        try {
            String queryString = String.format("url=%s&key=%s", URLEncoder.encode(url, "UTF-8"), API_KEY);
            String apiUrl = String.format("%s?%s", API_ENDPOINT, queryString);

            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Разбор JSON-ответа
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Извлечение значений метрик Lighthouse
                JSONObject lighthouse = jsonResponse.getJSONObject("lighthouseResult");
                JSONObject audits = lighthouse.getJSONObject("audits");
                Map<String, String> lighthouseMetrics = new HashMap<>();
                lighthouseMetrics.put("First Contentful Paint", audits.getJSONObject("first-contentful-paint").getString("displayValue"));
                lighthouseMetrics.put("Speed Index", audits.getJSONObject("speed-index").getString("displayValue"));
                lighthouseMetrics.put("Time To Interactive", audits.getJSONObject("interactive").getString("displayValue"));
                lighthouseMetrics.put("First Meaningful Paint", audits.getJSONObject("first-meaningful-paint").getString("displayValue"));
                lighthouseMetrics.put("First CPU Idle", audits.getJSONObject("first-cpu-idle").getString("displayValue"));
                lighthouseMetrics.put("Estimated Input Latency", audits.getJSONObject("estimated-input-latency").getString("displayValue"));

                JSONObject lighthouseMetricsJson = new JSONObject(lighthouseMetrics);
                String lighthouseMetricsString = lighthouseMetricsJson.toString();

                // Вывод всей строки с данными о скорости загрузки страницы в консоль
                System.out.println("Page speed analysis data: " + lighthouseMetricsString);

                urlRepository.addPageSpeedAnalysis(url, lighthouseMetricsString);

            } else {
                System.err.println("Failed to fetch API data. Response code: " + responseCode);
            }
        } catch (IOException e) {
            System.err.println("Error during PageSpeed analysis: " + e.getMessage());
        }
    }
}
