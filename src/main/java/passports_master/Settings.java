package passports_master;

import java.sql.*;


public class Settings {
    private String _info;
    public Settings(String c_host, String c_port, String c_db_name,
                    String c_login, String c_password){

        String mass_conn_settings[] = new String[5];
        mass_conn_settings[0] = c_host;
        mass_conn_settings[1] = c_port;
        mass_conn_settings[2] = c_db_name;
        mass_conn_settings[3] = c_login;
        mass_conn_settings[4] = c_password;

        DB database = new DB(mass_conn_settings);

        if(database.isConnection()){ // Если по текущим настройкам удалось подключиться к бд
            try {                    //                           за 10 мс, то сохраняем их
                Class.forName("org.sqlite.JDBC");
                Connection conn_settings = DriverManager.getConnection("jdbc:" +
                        "sqlite:" + getClass().getResource("/META-INF/db_settings.db"));

                String sql = "UPDATE `settings` SET `host` = ?, `port` = ?, `db_name` = ?, `login` = ?, `password` = ?";
                PreparedStatement ps = conn_settings.prepareStatement(sql);
                ps.setString(1, c_host);
                ps.setString(2, c_port);
                ps.setString(3, c_db_name);
                ps.setString(4, c_login);
                ps.setString(5, c_password);
                ps.executeUpdate();
                conn_settings.close();

                _info = "Подключено и сохранено";
            } catch (ClassNotFoundException | SQLException e) {
                _info = "Не удалось сохранить настройки";
            }
        }

        else
            _info = "Подключение отсутствует, настройки не сохранены";
    }

    public String Settings_getInfo(){return _info;}//////// Передача информации в контроллер ////////
}
