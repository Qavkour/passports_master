package passports_master;

import java.sql.*;

public class DB {
    private String HOST = "";
    private String PORT = "";
    private String DB_NAME = "";
    private String LOG = "";
    private String PASS = "";
    private Connection conn;
    protected String _info;

    ///////// Принимаем настройки от классов наследников или от класса Settings /////////
    public DB(String mass_conn_settings[]) {
        HOST = mass_conn_settings[0];
        PORT = mass_conn_settings[1];
        DB_NAME = mass_conn_settings[2];
        LOG = mass_conn_settings[3];
        PASS = mass_conn_settings[4];
    }

    ///////////// Устанавливаем подключение к бд /////////////
    protected Connection getConn() {
        String str = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(str, LOG, PASS);
        } catch (SQLException | ClassNotFoundException ignored) {
        }
        return conn;
    }

    ///////// Если удалось подключиться к бд за 10 мс, то возвращаем true /////////
    public boolean isConnection() {
        try (Connection conn = getConn()) {
            conn.isValid(10);
            return true;
        } catch (SQLException | NullPointerException e) {
            return false;
        }

    }

}

