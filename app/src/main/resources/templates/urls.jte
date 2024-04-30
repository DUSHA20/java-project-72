@import hexlet.code.App
@import hexlet.code.Url

@import java.util.List

@if(!urls.isEmpty())
<div>
    <h3 style="color: navy; text-align: center;">Добавленные сайты</h3>
    <table style="width: 100%; border-collapse: collapse; border: 1px solid #dddddd; text-align: left;">
        <thead>
            <tr style="background-color: darkgray; color: white;">
                <th style="border: 1px solid #dddddd; padding: 8px;">ID</th>
                <th style="border: 1px solid #dddddd; padding: 8px;">Имя</th>
                <th style="border: 1px solid #dddddd; padding: 8px;">Дата создания</th>
            </tr>
        </thead>
        <tbody>
            @foreach(var url in urls)
            <tr style="border: 1px solid #dddddd;">
                <td style="border: 1px solid #dddddd; padding: 8px;">${url.getId()}</td>
                <td style="border: 1px solid #dddddd; padding: 8px;">${url.getName()}</td>
                <td style="border: 1px solid #dddddd; padding: 8px;">${url.getCreatedAt().toString()}</td>
            </tr>
            @endforeach
        </tbody>
    </table>
</div>
@else
<p>Список сайтов пуст</p>
@endif