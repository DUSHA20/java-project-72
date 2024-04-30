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
import java.time.format.DateTimeFormatter;

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

    public static void addUrlHandler(Context ctx, UrlRepository urlRepository) {
        String url = ctx.formParam("url");
        try {
            URL parsedUrl = new URI(url).toURL();
            String domainWithPort = parsedUrl.getProtocol() + "://" + parsedUrl.getHost() +
                    (parsedUrl.getPort() != -1 ? ":" + parsedUrl.getPort() : "");

            if (urlRepository.exists(domainWithPort)) {
                ctx.sessionAttribute("error", "Страница уже существует");
            } else {
                urlRepository.addUrl(domainWithPort);
                ctx.sessionAttribute("success", "Страница успешно добавлена");
            }
        } catch (Exception e) {
            ctx.sessionAttribute("error", "Некорректный URL");
        }
        ctx.redirect("/");
    }

    public static void getAllUrlsHandler(Context ctx, UrlRepository urlRepository) {
        List<Url> urls = urlRepository.getAllUrls();

        // Создаем объект контекста для jte и передаем список URL
        Map<String, Object> model = Map.of("urls", urls);

        // Рендерим шаблон и передаем его на клиентскую сторону
        ctx.render("urls.jte", model);
    }

    public static void getUrlByIdHandler(Context ctx, UrlRepository urlRepository) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Url url = urlRepository.getUrlById(id);
        ctx.render("url.html", Map.of("url", url));
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
                .get("/urls/{id}", ctx -> getUrlByIdHandler(ctx, urlRepository))
                .start(port);

        return app;
    }

    public static void main(String[] args) {
        getApp();
    }
}
