@import hexlet.code.Url
@import java.util.List
@import hexlet.code.App
@import hexlet.code.UrlRepository

// Определение шаблона
@text
public String generateUrlsPage(List<Url> urls) {
    return """
        <!DOCTYPE html>
        <html lang="ru">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Список URL</title>
            <link rel="stylesheet" href="styles.css"> <!-- Ссылка на файл стилей -->
        </head>
        <body>
            <h1>Список URL</h1>

            <ul id="urls-list">
                <!-- Список URL будет динамически добавлен с помощью JavaScript -->
                @for(Url url : urls)
                <li>
                    <p>ID: ${url.getId()}</p>
                    <p>Name: ${url.getName()}</p>
                    <p>Created At: ${url.getCreatedAt()}</p>
                </li>
                @endfor
            </ul>

            <script>
                // JavaScript для динамического управления списком URL
            </script>

        </body>
        </html>
    """;
}