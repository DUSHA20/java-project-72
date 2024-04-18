package hexlet.code;

import io.javalin.Javalin;

public class App {

    public static Javalin getApp() {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));

        Javalin app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.result("Hello World"))
                .start(port); // Используем значение порта
        return app;
    }

    public static void main(String[] args) {
        getApp();
    }
}
