<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Список URL</title>
</head>
<body style="background-color: white; margin: 0;">

<!-- Верхняя шапка страницы -->
<div style="background-color: #4682B4; padding: 20px 10px; text-align: left; width: 100%; margin-top: -10px;">
    <a href="/" style="color: white; text-decoration: none;">На главную</a>
</div>

<!-- Таблица со списком URL -->
<table style="border-collapse: collapse; margin: 20px auto; width: 80%;">
    <thead>
    <tr style="background-color: #4682B4;">
        <th style="padding: 8px;">ID</th>
        <th style="padding: 8px;">Name</th>
        <th style="padding: 8px;">Created At</th>
    </tr>
    </thead>
    <tbody>
    <!-- JTE синтаксис для итерации по списку URLs -->
    @for (Url url : urls) {
    <tr style="border: 1px solid black;">
        <td style="padding: 8px;">@url.id</td>
        <td style="padding: 8px;">@url.name</td>
        <td style="padding: 8px;">@url.createdAt</td>
    </tr>
    }
    </tbody>
</table>

</body>
</html>