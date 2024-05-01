@import hexlet.code.UrlPage
@import hexlet.code.Url
@import java.util.List

@param List<UrlPage> urlPages

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
            @for (UrlPage page : urlPages)
                <tr>
                    <td>@page.getUrl().getId()</td>
                    <td>@page.getUrl().getName()</td>
                    <td>@page.getUrl().getCreatedAt()</td>
                </tr>
            @endfor
        </tbody>
    </table>
</body>
</html>