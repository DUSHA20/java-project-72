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
import java.util.Map;
import java.util.List;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

public class App {

    private static UrlRepository urlRepository;

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    private static String getJdbcUrlTemplate() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project");
    }


    // Надо обновить этот метод. При нажатии на кнопку проверить, должны добавляться данные еще и в таблицу проверенных URL
    // Надо сделать переход на отельную страницу с показом этого отдельного URL при нажатии на кнопку проверка на главной странице, но только в том случае
    // если эта страница еще не добавлена и введена правильно
    // Еще надо добавить вывод сообщений, что страница существует в базе данных, и, что URL введен не правильно, это лучше сделать прям в методе html
    // н еработает теория выше надо работать прям с методом getUrlByIdHtml
    public static void addUrlHandler(Context ctx, UrlRepository urlRepository) {
        String url = ctx.formParam("url");
        try {
            URL parsedUrl = new URI(url).toURL();
            String domainWithPort = parsedUrl.getProtocol() + "://" + parsedUrl.getHost() +
                    (parsedUrl.getPort() != -1 ? ":" + parsedUrl.getPort() : "");

            if (!urlRepository.exists(domainWithPort)) {
                // Если URL не существует, добавляем его
                urlRepository.addUrl(url);

                // Получаем добавленный URL из репозитория
                Url addedUrl = urlRepository.getLastInsertedUrl();
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

    // здесь еще надо будет добавить параметры из таблицы UrlCheck status проверки и последнее время проверки
    public static void getAllUrlsHandler(Context ctx, UrlRepository urlRepository) {
        List<Url> urls = urlRepository.getAllUrls();
        StringBuilder htmlContent = new StringBuilder();

        // Добавляем начало HTML страницы с встроенными стилями
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"ru\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>Список URL</title>");
        htmlContent.append("</head>");
        htmlContent.append("<body style=\"background-color: white;\">"); // Белый фон страницы

        // Добавляем верхнюю шапку страницы
        htmlContent.append("<div style=\"background-color: darkgray; padding: 10px; text-align: center;\">");
        htmlContent.append("<a href=\"/\" style=\"color: white; text-decoration: none;\">На главную</a>");
        htmlContent.append("</div>");

        // Добавляем начало таблицы с встроенными стилями
        htmlContent.append("<table style=\"border-collapse: collapse; margin: 20px auto; width: 80%;\">");
        htmlContent.append("<tr style=\"background-color: lightgray;\"><th style=\"padding: 8px;\">ID</th><th style=\"padding: 8px;\">Name</th><th style=\"padding: 8px;\">Created At</th></tr>");

        // Добавляем каждый URL в таблицу
        for (Url url : urls) {
            htmlContent.append("<tr style=\"border: 1px solid black;\">");
            htmlContent.append("<td style=\"padding: 8px;\">").append(url.getId()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(url.getName()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(url.getCreatedAt()).append("</td>");
            htmlContent.append("</tr>");
        }

        // Закрываем таблицу и HTML страницу
        htmlContent.append("</table>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        // Передаем HTML содержимое на клиентскую сторону
        ctx.html(htmlContent.toString());
    }

    // тут надо добавить html код страницы отображения
    // пробуем возвращать строку html
    // надо будет переназвать его
    public static void getUrlByIdHandler(Context ctx, UrlRepository urlRepository) {
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
        htmlContent.append("<div style=\"background-color: darkgray; padding: 10px; text-align: center;\">");
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
    }

    public static Javalin getApp() {

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        String jdbcUrlTemplate = getJdbcUrlTemplate();

        DataSource dataSource;

        if (jdbcUrlTemplate.startsWith("jdbc:h2")) {
            // Используем H2 для локальной разработки
            dataSource = new HikariDataSource(new HikariConfig());
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
                .get("/urls", ctx -> getAllUrlsHandler(ctx, urlRepository))
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
                    htmlContent.append("<div style=\"background-color: darkgray; padding: 10px; text-align: center;\">");
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
