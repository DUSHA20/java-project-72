@import hexlet.code.Url
@import java.util.List
@import hexlet.code.App
@import hexlet.code.UrlRepository

List<Url> urls = urlRepository.getAllUrls();

@if(!urls.isEmpty())
<div>
    <h3 style="color: navy; text-align: center;">Сайты</h3>
    <table style="width: 100%; border-collapse: collapse; border: 1px solid #dddddd; text-align: left;">
        <thead>
            <tr style="background-color: darkgray; color: white;">
                <th style="border: 1px solid #dddddd; padding: 8px;">ID</th>
                <th style="border: 1px solid #dddddd; padding: 8px;">Имя</th>
                <th style="border: 1px solid #dddddd; padding: 8px;">Дата создания</th>
            </tr>
        </thead>
        <tbody>
            @for(int i = 0; i < urls.size(); i++)
            <tr style="border: 1px solid #dddddd;">
                <td style="border: 1px solid #dddddd; padding: 8px;">${urls.get(i).getId()}</td>
                <td style="border: 1px solid #dddddd; padding: 8px;">${urls.get(i).getName()}</td>
                <td style="border: 1px solid #dddddd; padding: 8px;">${urls.get(i).getCreatedAt().toString()}</td>
            </tr>
            @end
        </tbody>
    </table>
</div>
@else
<p>Список сайтов пуст</p>
@end