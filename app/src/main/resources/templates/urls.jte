import java.util.List;
import hexlet.code.Url;

public class urls {
    public List<Url> urls;

    public urls(List<Url> urls) {
        this.urls = urls;
    }

    public void render() {
% for (Url url : urls) { %
        <tr>
            <td>${url.id}</td>
            <td>${url.url}</td>
        </tr>
% } %
    }
}