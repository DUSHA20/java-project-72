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
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
    public static void addUrlHandler(Context ctx, UrlRepository urlRepository) {
        String url = ctx.formParam("url");
        try {
            URL parsedUrl = new URI(url).toURL();
            String domainWithPort = parsedUrl.getProtocol() + "://" + parsedUrl.getHost() +
                    (parsedUrl.getPort() != -1 ? ":" + parsedUrl.getPort() : "");

            if (!urlRepository.exists(domainWithPort)) {
                // Если URL не существует, добавляем его
                urlRepository.addUrl(url);

                // Выполняем проверку URL и добавляем информацию о проверке в базу данных
                UrlChecker.checkAndSaveUrlInfo(url, urlRepository);

                // Получаем добавленный URL из репозитория
                Url addedUrl = urlRepository.getLastInsertedUrl();
                // Получаем его ID
                long addedUrlId = addedUrl.getId();

                // Вычисляем TF-IDF и добавляем результаты в базу данных
                TextAnalyzer.calculateAndSaveTFIDFForUrl(url, urlRepository);

                PageSpeedAnalyzer.analyzePage(url, urlRepository);

                // Перенаправляем пользователя на страницу с информацией о добавленном URL
                ctx.redirect("/urls/" + addedUrlId);

            } else {
                System.out.println("Сайт уже существует: " + domainWithPort);
            }

        } catch (Exception e) {
            System.out.println("Некорректный URL: " + url);
        }
    }

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
        htmlContent.append("<div style=\"background-color: #4682B4; padding: 10px; text-align: center;\">");
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

    public static void getAllUrlChecksHandler(Context ctx, UrlRepository urlRepository) {
        List<UrlCheck> urlChecks = urlRepository.getAllUrlChecks();
        StringBuilder htmlContent = new StringBuilder();

        // Добавляем начало HTML страницы с встроенными стилями
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"ru\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>Список проверок URL</title>");
        htmlContent.append("</head>");
        htmlContent.append("<body style=\"background-color: white;\">"); // Белый фон страницы

        // Добавляем верхнюю шапку страницы
        htmlContent.append("<div style=\"background-color: #4682B4; padding: 10px; text-align: left;\">");
        htmlContent.append("<a href=\"/\" style=\"color: white; text-decoration: none;\">На главную</a>");
        htmlContent.append("</div>");

        // Добавляем начало таблицы с встроенными стилями
        htmlContent.append("<table style=\"border-collapse: collapse; margin: 20px auto; width: 80%;\">");
        htmlContent.append("<tr style=\"background-color: lightgray;\">");
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

    public static void getAllTFIDFChecksHandler(Context ctx, UrlRepository urlRepository) {
        List<TFIDFCheck> tfidfchecks = urlRepository.getAllTFIDFChecks();
        StringBuilder htmlContent = new StringBuilder();

        // Добавляем начало HTML страницы с встроенными стилями
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"ru\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>Список проверок TF-IDF</title>");
        htmlContent.append("</head>");
        htmlContent.append("<body style=\"background-color: white;\">"); // Белый фон страницы

        // Добавляем верхнюю шапку страницы
        htmlContent.append("<div style=\"background-color: #4682B4; padding: 10px; text-align: left;\">");
        htmlContent.append("<a href=\"/\" style=\"color: white; text-decoration: none;\">На главную</a>");
        htmlContent.append("</div>");

        // Добавляем начало таблицы с встроенными стилями
        htmlContent.append("<table style=\"border-collapse: collapse; margin: 20px auto; width: 80%;\">");
        htmlContent.append("<tr style=\"background-color: lightgray;\">");
        htmlContent.append("<th style=\"padding: 8px;\">ID_Проверки</th>");
        htmlContent.append("<th style=\"padding: 8px;\">ID_Сайта</th>");
        htmlContent.append("<th style=\"padding: 8px;\">Слово</th>");
        htmlContent.append("<th style=\"padding: 8px;\"></th>");
        htmlContent.append("<th style=\"padding: 8px;\">Дата и время проверки</th>");
        htmlContent.append("</tr>");

        // Добавляем каждую проверку TF-IDF в таблицу
        for (TFIDFCheck tfidfcheck : tfidfchecks) {
            htmlContent.append("<tr style=\"border: 1px solid black;\">");
            htmlContent.append("<td style=\"padding: 8px;\">").append(tfidfcheck.getId()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(tfidfcheck.getUrlId()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(tfidfcheck.getWord()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(tfidfcheck.getTfidf()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(tfidfcheck.getCreatedAt()).append("</td>");
            htmlContent.append("</tr>");
        }

        // Закрываем таблицу и HTML страницу
        htmlContent.append("</table>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        // Передаем HTML содержимое на клиентскую сторону
        ctx.html(htmlContent.toString());
    }

    public static void getAllSpeedAnalysisHandler(Context ctx, UrlRepository urlRepository) {
        List<PageSpeedAnalysis> pageSpeedAnalysisList = urlRepository.getAllSpeedAnalysis();
        StringBuilder htmlContent = new StringBuilder();

        // Добавляем начало HTML страницы с встроенными стилями
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"ru\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>Список анализов скорости загрузки страниц</title>");
        htmlContent.append("</head>");
        htmlContent.append("<body style=\"background-color: white;\">"); // Белый фон страницы

        // Добавляем верхнюю шапку страницы
        htmlContent.append("<div style=\"background-color: #4682B4; padding: 10px; text-align: left;\">");
        htmlContent.append("<a href=\"/\" style=\"color: white; text-decoration: none;\">На главную</a>");
        htmlContent.append("</div>");

        // Добавляем начало таблицы с встроенными стилями
        htmlContent.append("<table style=\"border-collapse: collapse; margin: 20px auto; width: 80%;\">");
        htmlContent.append("<tr style=\"background-color: lightgray;\">");
        htmlContent.append("<th style=\"padding: 8px;\">ID_Анализа</th>");
        htmlContent.append("<th style=\"padding: 8px;\">ID_Сайта</th>");
        htmlContent.append("<th style=\"padding: 8px;\">Время загрузки (мс)</th>");
        htmlContent.append("<th style=\"padding: 8px;\">Использование CDN</th>");
        htmlContent.append("<th style=\"padding: 8px;\">Дата и время анализа</th>");
        htmlContent.append("</tr>");

        // Добавляем каждый анализ скорости загрузки страницы в таблицу
        for (PageSpeedAnalysis pageSpeedAnalysis : pageSpeedAnalysisList) {
            htmlContent.append("<tr style=\"border: 1px solid black;\">");
            htmlContent.append("<td style=\"padding: 8px;\">").append(pageSpeedAnalysis.getId()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(pageSpeedAnalysis.getUrlId()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(pageSpeedAnalysis.getLoadTime()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(pageSpeedAnalysis.getCdnUsed()).append("</td>");
            htmlContent.append("<td style=\"padding: 8px;\">").append(pageSpeedAnalysis.getCreatedAt()).append("</td>");
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
                .get("/urls/checks", ctx -> getAllUrlChecksHandler(ctx, urlRepository))
                .get("/urls/tfidfchecks", ctx -> getAllTFIDFChecksHandler(ctx, urlRepository))
                .get("/urls/pagespeedresults", ctx -> getAllSpeedAnalysisHandler(ctx, urlRepository))
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
                    htmlContent.append("<div style=\"background-color: #4682B4; padding: 10px; text-align: center;\">");
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
