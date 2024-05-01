package hexlet.code;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.Timestamp;


public class UrlRepository extends BaseRepository {

    // Конструктор, который устанавливает соединение с базой данных
    public UrlRepository(Connection connection) {
        super(connection);
    }

    public void initializeDatabase() {
        try (Statement statement = connection.createStatement()) {
            String createUrlsTableSql = "CREATE TABLE IF NOT EXISTS urls (" +
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "created_at TIMESTAMP NOT NULL" +
                    ")";
            statement.executeUpdate(createUrlsTableSql);

            String createUrlChecksTableSql = "CREATE TABLE IF NOT EXISTS url_checks (" +
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                    "url VARCHAR(255)," +
                    "title VARCHAR(255)," +
                    "h1 VARCHAR(255)," +
                    "description TEXT," +
                    "created_at TIMESTAMP NOT NULL," +
                    "FOREIGN KEY (url_id) REFERENCES urls(id)" +
                    ")";
            statement.executeUpdate(createUrlChecksTableSql);

            System.out.println("Tables 'urls' and 'url_checks' created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для проверки существования URL в базе данных
    public boolean exists(String url) {
        String sql = "SELECT COUNT(*) FROM urls WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, url);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для добавления URL в базу данных
    public void addUrl(String url) {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, NOW())";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, url);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Url getLastInsertedUrl() {
        Url url = null;
        String sql = "SELECT * FROM urls ORDER BY id DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                url = new Url();
                url.setId(resultSet.getLong("id"));
                url.setName(resultSet.getString("name"));
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                LocalDateTime createdAt = timestamp.toLocalDateTime();
                url.setCreatedAt(createdAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // Метод для получения списка всех URL из базы данных
    public List<Url> getAllUrls() {
        List<Url> urls = new ArrayList<>();
        String sql = "SELECT * FROM urls"; // Получаем все данные из таблицы urls
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Url url = new Url();
                url.setId(resultSet.getLong("id"));
                url.setName(resultSet.getString("name"));

                // Получаем дату и время в формате Timestamp
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                // Преобразуем Timestamp в LocalDateTime
                LocalDateTime createdAt = timestamp.toLocalDateTime();

                url.setCreatedAt(createdAt);
                urls.add(url);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return urls;
    }

    // Метод для получения URL по идентификатору из базы данных
    public Url getUrlById(long id) {
        Url url = null;
        String sql = "SELECT id, name, created_at FROM urls WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long urlId = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    url = new Url(urlId, name, createdAt); // Используем конструктор с параметрами
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // Метод для добавления информации о проверке URL в базу данных
    public void addUrlCheck(String url, String title, String h1, String description) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO url_checks (url, title, h1, description, created_at) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, url);
            statement.setString(2, title);
            statement.setString(3, h1);
            statement.setString(4, description);
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения информации о проверках конкретного URL из базы данных
    public List<UrlCheck> getUrlChecksByUrl(String url) {
        List<UrlCheck> urlChecks = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM url_checks WHERE url = ?");
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UrlCheck urlCheck = new UrlCheck();
                urlCheck.setId(resultSet.getLong("id"));
                urlCheck.setTitle(resultSet.getString("title"));
                urlCheck.setH1(resultSet.getString("h1"));
                urlCheck.setDescription(resultSet.getString("description"));
                urlCheck.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                urlChecks.add(urlCheck);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return urlChecks;
    }

    public List<UrlCheck> getAllUrlChecks() {
        List<UrlCheck> urlChecks = new ArrayList<>();
        String sql = "SELECT * FROM url_checks"; // Получаем все данные из таблицы url_checks
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                UrlCheck urlCheck = new UrlCheck();
                urlCheck.setId(resultSet.getLong("id"));
                urlCheck.setUrl(resultSet.getString("url"));
                urlCheck.setTitle(resultSet.getString("title"));
                urlCheck.setH1(resultSet.getString("h1"));
                urlCheck.setDescription(resultSet.getString("description"));

                // Получаем дату и время в формате Timestamp
                Timestamp timestamp = resultSet.getTimestamp("created_at");
                // Преобразуем Timestamp в LocalDateTime
                LocalDateTime createdAt = timestamp.toLocalDateTime();
                urlCheck.setCreatedAt(createdAt);

                urlChecks.add(urlCheck);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return urlChecks;
    }
}
