package passports_master;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
public class Main_Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn_Export;

    @FXML
    private Button btn_Import;

    @FXML
    private Button btn_save_settings;

    @FXML
    private TextField export_address;

    @FXML
    private ComboBox<String> export_class;

    @FXML
    private DatePicker export_date;

    @FXML
    private TextField export_number;

    @FXML
    private TextField export_amount;

    @FXML
    private TextField export_type;

    @FXML
    private ComboBox<String> import_class;

    @FXML
    private TextField import_save_path;

    @FXML
    private TextField import_amount;

    @FXML
    private TextField import_number;

    @FXML
    private DatePicker import_old_date;

    @FXML
    private TextField import_type;

    @FXML
    private ChoiceBox<String> import_origin;

    @FXML
    private ChoiceBox<String> export_origin;

    @FXML
    private DatePicker import_young_date;

    @FXML
    private Label export_info;

    @FXML
    private Label import_info;

    @FXML
    private TextField settings_db_name;

    @FXML
    private TextField settings_host;

    @FXML
    private TextField settings_login;

    @FXML
    private PasswordField settings_password;

    @FXML
    private TextField settings_port;

    @FXML
    private Label settings_info;

    private String _info;

    /////////////// Список
    private String[] classes = {"Опора", "Фундамент", "Анкер", "Деталь", "ЖП", "Лестница", "Оголовок", "Столик",
            "Груз ЖБ", "Рама Опорная", "Люлька", "Знак", "КБП", "Кронштейн", "Консоль", "Оттяжка",
            "Подвес", "Стойка", "Хомут", "Узел Крепления", "Фиксатор", "ГРПЗ", "Заземлитель",
            "Изолятор", "ОПН", "Привод", "Разъединитель", "Провод"};
    private String[] Import_origin = {"Оригинал", "Копия", "Не важно"};
    private String[] Export_origin = {"Оригинал", "Копия"};

    @FXML
    void initialize() {

        import_class.getItems().addAll(classes); // Добавляем списки
        export_class.getItems().addAll(classes);
        import_origin.getItems().addAll(Import_origin);
        import_origin.setValue(Import_origin[2]);
        export_origin.getItems().addAll(Export_origin);

        btn_Export.setOnAction(event -> cl_export());
        btn_Import.setOnAction(event -> cl_import());
        btn_save_settings.setOnAction(event -> cl_settings());

        String[] mass_conn_settings = ConnectionSettings();
        settings_host.setText(mass_conn_settings[0]);
        settings_port.setText(mass_conn_settings[1]);
        settings_db_name.setText(mass_conn_settings[2]);
        settings_login.setText(mass_conn_settings[3]);
        settings_password.setText(mass_conn_settings[4]);

        btn_save_settings.setDisable(true);
    }

    private void cl_export(){

        String _address = export_address.getText();
        String _class = export_class.getValue();
        String _type = export_type.getText();
        String _number = export_number.getText();
        String _date = String.valueOf(export_date.getValue());
        String _origin = export_origin.getValue();
        String _amount = export_amount.getText();
        String[] mass_conn_settings = ConnectionSettings();

        Export Export = new Export(_address, _class, _type, _number, _date, _origin, _amount, mass_conn_settings);
        _info = Export.Export_GetInfo();
        SetExportInfo(_info);

        if(_info.contains("Добавлена")) {
            export_address.setText("");
            export_type.clear();
            export_number.clear();
            export_origin.setValue("");
            export_amount.clear();
        }
    }

    private void cl_import(){

        String _class = import_class.getValue();
        String _type = import_type.getText();
        String _number = import_number.getText();
        String _amount = import_amount.getText();
        String _old_date = String.valueOf(import_old_date.getValue());
        String _young_date = String.valueOf(import_young_date.getValue());
        String _origin = import_origin.getValue();
        String _save_path = import_save_path.getText();
        String[] mass_conn_settings = ConnectionSettings();

        Import Import = new Import(_class, _type, _number, _amount, _old_date,
                _young_date, _origin, _save_path, mass_conn_settings);
        _info = Import.Import_GetInfo();

        SetImportInfo(_info);
    }

    private void cl_settings(){
        String _host = settings_host.getText();
        String _port = settings_port.getText();
        String _db_name = settings_db_name.getText();
        String _login = settings_login.getText();
        String _password = settings_password.getText();

        Settings settings = new Settings(_host, _port, _db_name, _login, _password);
        _info = settings.Settings_getInfo();
        SetSettingsInfo(_info);

        if(_info.contains("Подключено"))
            settings_change();
    }
    @FXML/////////////// Блокирует поля ввода количества и дат, если поле "номер" заполнено ///////////////
    public void number_auto_disable(){
        if(!import_number.getText().equals("")) {
            import_amount.setDisable(true);
            import_old_date.setDisable(true);
            import_young_date.setDisable(true);
        }
        else {
            import_amount.setDisable(false);
            import_old_date.setDisable(false);
            import_young_date.setDisable(false);
        }
    }

    @FXML
    public void settings_change(){
        String[] mass_conn_settings = ConnectionSettings();
        btn_save_settings.setDisable(
                settings_host.getText().equals(mass_conn_settings[0]) &&
                settings_port.getText().equals(mass_conn_settings[1]) &&
                settings_db_name.getText().equals(mass_conn_settings[2]) &&
                settings_login.getText().equals(mass_conn_settings[3]) &&
                settings_password.getText().equals(mass_conn_settings[4])
                );
    }

    /////////////// Обращается к бд sqlite для получения настроек подключения к бд с паспортами ///////////////
    private String[] ConnectionSettings(){
        String[] mass_conn_settings = new String[5];
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn_settings = DriverManager.getConnection("jdbc:" +
                    "sqlite:" + getClass().getResource("/META-INF/db_settings.db"));
            String sql = "SELECT * FROM `settings`";
            Statement st = conn_settings.createStatement();
            ResultSet rs = st.executeQuery(sql);
            mass_conn_settings[0] = rs.getString("host");
            mass_conn_settings[1] = rs.getString("port");
            mass_conn_settings[2] = rs.getString("db_name");
            mass_conn_settings[3] = rs.getString("login");
            mass_conn_settings[4] = rs.getString("password");
            st.close();
            rs.close();
            conn_settings.close();
        } catch (SQLException | ClassNotFoundException e) {
            _info = "Не найден файл db_settings.db";
        }

        return mass_conn_settings; // Возвращаем массив с настройками
    }

    /////////////////// Выводим информация на экран ///////////////////
    private void SetExportInfo(String _info){export_info.setText(_info);}
    private void SetImportInfo(String _info){import_info.setText(_info);}
    private void SetSettingsInfo(String _info){settings_info.setText(_info);}

}

