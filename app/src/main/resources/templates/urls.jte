<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Список URL</title>
    <style>
        body {
            background-color: white;
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
        }

        .header {
            background-color: darkgray;
            color: white;
            padding: 10px;
            text-align: center;
        }

        .content {
            max-width: 800px;
            margin: 50px auto; /* Центрируем список */
        }

        h1 {
            color: navy;
            text-align: center;
        }

        .url-item {
            border-bottom: 1px solid lightgray;
            padding: 10px;
        }

        .url-item p {
            margin: 0;
        }
    </style>
</head>
<body>
    <div class="header">
        <a href="/" style="color: white; text-decoration: none;">Главная</a>
    </div>

    <div class="content">
        <h1>Сайты</h1>

        <!-- Список URL будет динамически добавлен с помощью JTE -->
        <?jte foreach (Url url in $urls) { ?>
        <div class="url-item">
            <p>ID: <?jte url.getId() ?></p>
            <p>Name: <?jte url.getName() ?></p>
            <p>Created At: <?jte url.getCreatedAt() ?></p>
        </div>
        <?jte } ?>
    </div>

    <script>
        // JavaScript для динамического управления списком URL
    </script>

</body>
</html>