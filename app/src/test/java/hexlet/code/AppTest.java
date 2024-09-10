package hexlet.code;

import io.javalin.Javalin;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {

    private static Javalin app;
    private static UrlRepository urlRepository;
    private static Connection connection;
    private static OkHttpClient client;

    @BeforeAll
    public static void setup() throws SQLException {
        // Start in-memory H2 database
        String jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        connection = DriverManager.getConnection(jdbcUrl, "sa", "");

        urlRepository = new UrlRepository(connection);
        urlRepository.initializeDatabase();

        // Start the Javalin app
        app = App.getApp();

        client = new OkHttpClient();
    }

    @AfterAll
    public static void teardown() {
        if (app != null) {
            app.stop();
        }
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddUrl() throws IOException {
        String testUrl = "http://example.com";

        Request request = new Request.Builder()
                .url("http://localhost:8080/urls")
                .post(RequestBody.create(
                        MediaType.parse("application/x-www-form-urlencoded"),
                        "url=" + testUrl))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code());

            // Verify URL is added
            List<Url> urls = urlRepository.getAllUrls();
            assertTrue(urls.stream().anyMatch(url -> url.getName().equals(testUrl)));
        }
    }

    @Test
    public void testGetAllUrls() throws IOException {
        String testUrl = "http://example.com";
        urlRepository.addUrl(testUrl);

        Request request = new Request.Builder()
                .url("http://localhost:8080/urls")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code()); // 200 OK
            String responseBody = response.body().string();

            assertTrue(responseBody.contains(testUrl));
        }
    }

    @Test
    public void testGetUrlById() throws IOException {
        // Add URL to repository
        String testUrl = "http://example.com";
        urlRepository.addUrl(testUrl);
        Url url = urlRepository.getLastInsertedUrl();

        Request request = new Request.Builder()
                .url("http://localhost:8080/urls/" + url.getId())
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code()); // 200 OK
            String responseBody = response.body().string();

            assertTrue(responseBody.contains(testUrl));
        }
    }
}

