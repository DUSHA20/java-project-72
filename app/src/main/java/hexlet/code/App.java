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
//import gg.jte.output.StringOutput;
import java.io.StringWriter;
//import java.util.Properties;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import java.util.HashMap;
import java.util.ArrayList;


public class App {

    private static UrlRepository urlRepository;
    private static MustacheFactory mustacheFactory;

    protected static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    private static String getJdbcUrlTemplate() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL",
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
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

                // Устанавливаем flash-сообщение
                ctx.sessionAttribute("flash", Map.of("success", "Страница успешно добавлена"));

                // отладка
                System.out.println("Flash message: " + ctx.sessionAttribute("flash"));

                // Перенаправляем пользователя на страницу с информацией о добавленном URL
                ctx.redirect("/urls/" + addedUrlId);

            } else {
                // Устанавливаем flash-сообщение
                ctx.sessionAttribute("flash", Map.of("error", "Сайт уже существует"));

                // отладка
                System.out.println("Flash message: " + ctx.sessionAttribute("flash"));

                // Перенаправляем пользователя на страницу со всеми добавленными сайтами
                ctx.redirect("/urls");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("flash", Map.of("error", "Некорректный URL"));
            // Отладка
            System.out.println("Flash message: " + ctx.sessionAttribute("flash"));
            ctx.redirect("/urls");
        }
    }

    protected static MustacheFactory createMustacheFactory() {
        return new DefaultMustacheFactory("templates");
    }

    public static void getAllUrlsHandler(Context ctx, UrlRepository repository) {
        List<Url> urls = repository.getAllUrls();
        if (urls == null) {
            urls = new ArrayList<>(); // Or any other default value
        }

        Map<String, Object> flash = ctx.sessionAttribute("flash");
        if (flash == null) {
            flash = new HashMap<>(); // Default empty map
        }
        ctx.sessionAttribute("flash", null); // Clear flash messages after usage

        // Отладка
        System.out.println("Flash message on URL list page: " + flash);

        Map<String, Object> context = Map.of(
                "urls", urls,
                "flash", flash
        );

        try {
            Mustache mustache = mustacheFactory.compile("allUrls.mustache");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, context).flush();
            ctx.html(writer.toString());
        } catch (Exception e) {
            ctx.status(500).result("Error processing template: " + e.getMessage());
        }
    }

    public static void getAllUrlChecksHandler(Context ctx, UrlRepository repository) {
        List<UrlCheck> urlChecks = repository.getAllUrlChecks();
        if (urlChecks == null) {
            urlChecks = new ArrayList<>(); // Or any other default value
        }

        Map<String, Object> flash = ctx.sessionAttribute("flash");
        if (flash == null) {
            flash = new HashMap<>(); // Default empty map
        }
        ctx.sessionAttribute("flash", null); // Clear flash messages after usage

        // Отладка
        System.out.println("Flash message on URL checks page: " + flash);

        Map<String, Object> context = Map.of(
                "urlChecks", urlChecks,
                "flash", flash
        );

        try {
            Mustache mustache = mustacheFactory.compile("allUrlChecks.mustache");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, context).flush();
            ctx.html(writer.toString());
        } catch (Exception e) {
            ctx.status(500).result("Ошибка при обработке шаблона: " + e.getMessage());
        }
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

        mustacheFactory = createMustacheFactory();

        Javalin app = Javalin.create(configure -> {
            configure.fileRenderer(new JavalinJte(createTemplateEngine()));
            configure.staticFiles.add("/templates");
        })
                .get("/", ctx -> ctx.render("index.mustache"))
                .post("/urls", ctx -> addUrlHandler(ctx, urlRepository))
                .get("/urls", ctx -> getAllUrlsHandler(ctx, urlRepository))
                .get("/urls/checks", ctx -> getAllUrlChecksHandler(ctx, urlRepository))
                .get("/urls/{id}", ctx -> {
                    long id = Long.parseLong(ctx.pathParam("id"));
                    Url url = urlRepository.getUrlById(id);

                    // Извлечение флеш-сообщений
                    Map<String, Object> flash = ctx.sessionAttribute("flash");
                    if (flash == null) {
                        flash = new HashMap<>(); // Создаем пустую карту, если флеш-сообщений нет
                    }
                    ctx.sessionAttribute("flash", null); // Очистка флеш-сообщений после использования

                    if (url != null) {
                        Map<String, Object> model = Map.of(
                                "id", url.getId(),
                                "name", url.getName(),
                                "createdAt", url.getCreatedAt(),
                                "flash", flash // Добавляем флеш-сообщения в модель
                        );

                        Mustache mustache = mustacheFactory.compile("urlDetails.mustache");
                        StringWriter writer = new StringWriter();
                        mustache.execute(writer, model).flush();
                        ctx.html(writer.toString());
                    } else {
                        ctx.status(404).result("URL not found");
                    }
                })
                .start(port);

        return app;
    }

    public static void main(String[] args) {
        getApp();
    }
}
