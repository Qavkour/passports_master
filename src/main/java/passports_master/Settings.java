package passports_master;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class Settings{
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
                    FileOutputStream fis = new FileOutputStream("src/main/resources/META-INF/db_settings.bin");
                    ObjectOutputStream oos = new ObjectOutputStream(fis);
                    oos.writeObject(mass_conn_settings);
                    oos.close();
                _info = "Подключено и сохранено";
            } catch (IOException e) {
                _info = "Не удалось сохранить настройки";
            }
        }

        else
            _info = "Подключение отсутствует, настройки не сохранены";
    }

    public String Settings_getInfo(){return _info;}//////// Передача информации в контроллер ////////
}
