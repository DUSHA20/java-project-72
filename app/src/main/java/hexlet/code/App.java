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
import java.net.URI;
import java.net.URL;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import gg.jte.output.StringOutput;

public class App {

    private static UrlRepository urlRepository;

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    private static String getJdbcUrlTemplate() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
    }

    public static void addUrlHandler(Context ctx, UrlRepository repository) {
        String url = ctx.formParam("url");
        try {
            URL parsedUrl = new URI(url).toURL();
            String domainWithPort = parsedUrl.getProtocol() + "://" + parsedUrl.getHost()
                   + (parsedUrl.getPort() != -1 ? ":" + parsedUrl.getPort() : "");

            if (!repository.exists(domainWithPort)) {
                // Если URL не существует, добавляем его
                repository.addUrl(url);

                // Выполняем проверку URL и добавляем информацию о проверке в базу данных
                UrlChecker.checkAndSaveUrlInfo(url, repository);

                // Получаем добавленный URL из репозитория
                Url addedUrl = repository.getLastInsertedUrl();
                // Получаем его ID
                long addedUrlId = addedUrl.getId();

                // Перенаправляем пользователя на страницу с информацией о добавленном URL
                ctx.redirect("/urls/" + addedUrlId);

            } else {
                System.out.println("Сайт уже существует: " + domainWithPort);
            }

        } catch (Exception e) {
            System.out.println("Некорректный URL: " + url);
        }
    }

    public static void getMessageHandler(Context ctx) {
        // Простое строковое сообщение
        String message = "Hello, World!";

        // Получаем экземпляр TemplateEngine
        TemplateEngine templateEngine = createTemplateEngine();

        // Создаем StringOutput для вывода
        StringOutput output = new StringOutput();

        // Рендеринг страницы с использованием шаблона allUrls.jte и передачи строки в шаблон
        templateEngine.render("allUrls.jte", Map.of("message", message), output);

        // Передача отрендеренного HTML в контекст
        ctx.html(output.toString());
    }

    public static void getAllUrlChecksHandler(Context ctx, UrlRepository repository) {
        List<UrlCheck> urlChecks = repository.getAllUrlChecks();
        StringBuilder htmlContent = new StringBuilder();

        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"ru\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>Список проверок URL</title>");
        htmlContent.append("</head>");
        htmlContent.append("<body style=\"background-color: white;\">");

        // Добавляем верхнюю шапку страницы
        htmlContent.append("<div style=\"background-color: #4682B4; padding: 20px 10px; "
                + "text-align: left; width: 100%; margin-top: -10px;\">");
        htmlContent.append("<a href=\"/\" style=\"color: white; text-decoration: none;\">На главную</a>");
        htmlContent.append("</div>");

        // Добавляем начало таблицы с встроенными стилями
        htmlContent.append("<table style=\"border-collapse: collapse; margin: 20px auto; width: 80%;\">");
        htmlContent.append("<tr style=\"background-color: #4682B4;\">");
        htmlContent.append("<th style=\"padding: 8px;\">ID</th>");
        htmlContent.append("<th style=\"padding: 8px;\">URL_id</th>");
        htmlContent.append("<th style=\"padding: 8px;\">Title</th>");
        htmlContent.append("<th style=\"padding: 8px;\">H1</th>");
        htmlContent.append("<th style=\"padding: 8px;\">Description</th>");
        htmlContent.append("<th style=\"padding: 8px;\">Created At</th>");
        htmlContent.append("</tr>");

        // Добавляем каждую проверку URL в таблицу
        for (UrlCheck urlCheck : urlChecks) {
            htmlContent.append("<tr style=\"border: 1px solid black;\">");
            htmlContent.append("<td style=\"padding: 8px;\">").append(urlCheck.getId()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(urlCheck.getUrl()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(urlCheck.getTitle()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(urlCheck.getH1()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(urlCheck.getDescription()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(urlCheck.getCreatedAt()).append("</td>");
            htmlContent.append("</tr>");
        }

        // Закрываем таблицу и HTML страницу
        htmlContent.append("</table>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        // Передаем HTML содержимое на клиентскую сторону
        ctx.html(htmlContent.toString());
    }

    public static Javalin getApp() {

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        String jdbcUrlTemplate = getJdbcUrlTemplate();

        DataSource dataSource;

        if (jdbcUrlTemplate.startsWith("jdbc:h2")) {
            try {
                Class.forName("org.h2.Driver");
                System.out.println("H2 Driver Loaded Successfully!");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }

            // Конфигурация для H2
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrlTemplate);
            config.setDriverClassName("org.h2.Driver");
            config.setUsername("sa");
            config.setPassword("");
            dataSource = new HikariDataSource(config);
        } else {
            // Используем PostgreSQL для продакшена
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrlTemplate);
            config.setUsername(System.getenv("JDBC_DATABASE_USER"));
            config.setPassword(System.getenv("JDBC_DATABASE_PASSWORD"));
            dataSource = new HikariDataSource(config);
        }

        try {
            Connection connection = dataSource.getConnection();
            urlRepository = new UrlRepository(connection);
            urlRepository.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Javalin app = Javalin.create(configure -> {
            configure.fileRenderer(new JavalinJte(createTemplateEngine()));
            configure.staticFiles.add("/templates");
        })
                .get("/", ctx -> ctx.render("index.html"))
                .post("/urls", ctx -> addUrlHandler(ctx, urlRepository))
                .get("/urls", ctx -> getMessageHandler(ctx))
                .get("/urls/checks", ctx -> getAllUrlChecksHandler(ctx, urlRepository))
                .get("/urls/{id}", ctx -> {
                    long id = Long.parseLong(ctx.pathParam("id"));
                    Url url = urlRepository.getUrlById(id);

                    // Создаем HTML-контент с помощью StringBuilder
                    StringBuilder htmlContent = new StringBuilder();

                    // Добавляем начало HTML страницы
                    htmlContent.append("<!DOCTYPE html>");
                    htmlContent.append("<html lang=\"ru\">");
                    htmlContent.append("<head>");
                    htmlContent.append("<meta charset=\"UTF-8\">");
                    htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                    htmlContent.append("<title>Детали URL</title>");
                    htmlContent.append("</head>");
                    htmlContent.append("<body style=\"background-color: white;\">"); // Белый фон страницы

                    // Добавляем верхнюю шапку страницы
                    htmlContent.append("<div style=\"background-color: #4682B4; padding: 20px 10px; "
                            + "text-align: left; width: 100%; margin-top: -10px;\">");
                    htmlContent.append("<a href=\"/\" style=\"color: white; text-decoration: none;\">На главную</a>");
                    htmlContent.append("</div>");

                    // Добавляем информацию о URL
                    if (url != null) {
                        htmlContent.append("<div style=\"margin: 20px auto; width: 80%;\">");
                        htmlContent.append("<h2>ID: ").append(url.getId()).append("</h2>");
                        htmlContent.append("<p>Name: ").append(url.getName()).append("</p>");
                        htmlContent.append("<p>Created At: ").append(url.getCreatedAt()).append("</p>");
                        htmlContent.append("</div>");
                    } else {
                        htmlContent.append("<p>URL not found</p>");
                    }

                    // Закрываем HTML страницу
                    htmlContent.append("</body>");
                    htmlContent.append("</html>");

                    // Передаем HTML содержимое на клиентскую сторону
                    ctx.html(htmlContent.toString());
                })
                .start(port);

        return app;
    }

    public static void main(String[] args) {
        getApp();
    }
}
