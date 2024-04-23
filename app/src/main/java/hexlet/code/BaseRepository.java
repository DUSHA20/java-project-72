package hexlet.code;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseRepository {
    protected Connection connection;

    // Конструктор, который устанавливает соединение с базой данных
    public BaseRepository(Connection connection) {
        this.connection = connection;
    }

    // Метод для закрытия соединения с базой данных
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
