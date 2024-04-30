// urls_list.jte

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
        }

        h1 {
            color: navy;
            text-align: center;
        }

        ul#urls-list {
            list-style-type: none;
            padding: 0;
        }

        ul#urls-list li {
            border-bottom: 1px solid lightgray;
            padding: 10px;
        }

        ul#urls-list li p {
            margin: 0;
        }
    </style>
</head>
<body>
    <h1>Список URL</h1>

    <ul id="urls-list">
        <!-- Список URL будет динамически добавлен с помощью JavaScript -->
        #foreach($url in $urls)
        <li>
            <p>ID: $url.getId()</p>
            <p>Name: $url.getName()</p>
            <p>Created At: $url.getCreatedAt()</p>
        </li>
        #end
    </ul>

    <script>
        // JavaScript для динамического управления списком URL
    </script>

</body>
</html>