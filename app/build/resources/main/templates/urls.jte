import java.util.List;
import hexlet.code.Url;

public class urls {
    public List<Url> urls;

    public urls(List<Url> urls) {
        this.urls = urls;
    }

    public void render() {
        // Вставляем HTML начало таблицы
        #html(
            "<table>",
            "<thead>",
            "<tr>",
            "<th>ID</th>",
            "<th>URL</th>",
            "<th>Created At</th>", // Добавляем заголовок для даты создания
            "</tr>",
            "</thead>",
            "<tbody>"
        )

        // Для каждого URL в списке, создаем строку таблицы
        #for(Url url : urls) {
            #html(
                "<tr>",
                "<td>${url.id}</td>",
                "<td><a href='${url.name}'>${url.name}</a></td>",
                "<td>${url.createdAt}</td>", // Вставляем дату создания
                "</tr>"
            )
        }

        // Вставляем HTML конец таблицы
        #html("</tbody>", "</table>")
    }
}