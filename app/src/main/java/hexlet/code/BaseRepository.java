package hexlet.code;

import java.sql.Connection;

public abstract class BaseRepository {
    protected Connection connection;

    // Конструктор, который устанавливает соединение с базой данных
    public BaseRepository(Connection connection) {
        this.connection = connection;
    }
}
