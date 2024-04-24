package hexlet.code;

import io.javalin.Javalin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.resolve.ResourceCodeResolver;

public class App {

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static Javalin getApp() {

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));

        // Получаем значения переменных окружения
        String hostname = System.getenv("HOST");
        String dbPort = "5432"; // Жестко закодированный порт PostgreSQL
        String database = System.getenv("DATABASE");
        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");

        // Формируем строку подключения
        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?password=%s&user=%s",
                hostname,
                dbPort,
                database,
                password,
                username);

        // Конфигурируем HikariCP для использования базы данных
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        DataSource dataSource = new HikariDataSource(config);

        // Создаем экземпляр UrlRepository и инициализируем базу данных
        UrlRepository urlRepository = null;
        try {
            Connection connection = dataSource.getConnection();
            urlRepository = new UrlRepository(connection);
            urlRepository.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            // В случае исключения выходим из приложения
            System.exit(1);
        }

        // Создаем TemplateEngine
        TemplateEngine templateEngine = createTemplateEngine();

        // Подключаем TemplateEngine к Javalin
        Javalin app = Javalin.create(configure -> {
                    configure.fileRenderer(new JavalinJte(templateEngine));
                })
                .get("/", ctx -> ctx.result("Hello World"))
                .start(port); // Используем значение порта

        return app;
    }

    public static void main(String[] args) {
        getApp();
    }
}
