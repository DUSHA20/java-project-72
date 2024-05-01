import java.util.List;
import hexlet.code.Url;

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Список URL</title>
</head>
<body style="background-color: white;"> <!-- Белый фон страницы -->
    <div style="background-color: darkgray; padding: 10px; text-align: center;"> <!-- Верхняя шапка страницы -->
        <a href="/" style="color: white; text-decoration: none;">На главную</a>
    </div>
    <table style="border-collapse: collapse; margin: 20px auto; width: 80%;"> <!-- Таблица URL -->
        <tr style="background-color: lightgray;"><th style="padding: 8px;">ID</th><th style="padding: 8px;">Name</th><th style="padding: 8px;">Created At</th></tr>
        @for (Url url : urls)
            <tr style="border: 1px solid black;">
                <td style="padding: 8px;">@url.getId()</td>
                <td style="padding: 8px;">@url.getName()</td>
                <td style="padding: 8px;">@url.getCreatedAt()</td>
            </tr>
        @endfor
    </table>
</body>
</html>