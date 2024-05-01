<!DOCTYPE html>
<html>
<head>
    <title>Example</title>
</head>
<body>
    <h1>URLs Data</h1>
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Created Date</th>
            </tr>
        </thead>
        <tbody>
            @for (int i = 0; i < ids.size(); i++)
                <tr>
                    <td>@ids.get(i)</td>
                    <td>@names.get(i)</td>
                    <td>@createdDates.get(i)</td>
                </tr>
            @endfor
        </tbody>
    </table>
</body>
</html>