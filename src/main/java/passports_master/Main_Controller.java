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
    private Button btn_Delete;

    @FXML
    private Button btn_Export;

    @FXML
    private Button btn_Change;

    @FXML
    private Button btn_Import;

    @FXML
    private Button btn_save_settings;

    @FXML
    private TextField change_id;

    @FXML
    private TextField change_amount;

    @FXML
    private ComboBox<String> change_class;

    @FXML
    private DatePicker change_date;

    @FXML
    private TextField change_number;

    @FXML
    private ChoiceBox<String> change_origin;

    @FXML
    private TextField change_type;

    @FXML
    private ComboBox<String> delete_class;

    @FXML
    private TextField delete_id;

    @FXML
    private Label delete_info;

    @FXML
    private TextField export_address;

    @FXML
    private TextField export_amount;

    @FXML
    private ComboBox<String> export_class;

    @FXML
    private DatePicker export_date;

    @FXML
    private Label export_info;

    @FXML
    private Label change_info;

    @FXML
    private TextField export_number;

    @FXML
    private ChoiceBox<String> export_origin;

    @FXML
    private TextField export_type;

    @FXML
    private TextField import_amount;

    @FXML
    private ComboBox<String> import_class;

    @FXML
    private Label import_info;

    @FXML
    private TextField import_number;

    @FXML
    private DatePicker import_old_date;

    @FXML
    private ChoiceBox<String> import_origin;

    @FXML
    private TextField import_save_path;

    @FXML
    private TextField import_type;

    @FXML
    private DatePicker import_young_date;

    @FXML
    private TextField settings_db_name;

    @FXML
    private TextField settings_host;

    @FXML
    private Label settings_info;

    @FXML
    private TextField settings_login;

    @FXML
    private PasswordField settings_password;

    @FXML
    private TextField settings_port;

    private String _info;

    /////////////// Список
    private String[] classes = {"Опора", "Фундамент", "Анкер", "Деталь", "ЖП", "Лестница", "Оголовок", "Столик",
            "Груз ЖБ", "Рама Опорная", "Люлька", "Знак", "КБП", "Кронштейн", "Консоль", "Оттяжка",
            "Подвес", "Стойка", "Хомут", "Узел Крепления", "Фиксатор", "ГРПЗ", "Заземлитель",
            "Изолятор", "ОПН", "Привод", "Разъединитель", "Провод"};
    private String[] Import_origin = {"Оригинал", "Копия", "Не важно"};
    private String[] Export_and_Change_origin = {"Оригинал", "Копия"};

    @FXML
    void initialize() {

        import_class.getItems().addAll(classes); // Добавляем списки
        export_class.getItems().addAll(classes);
        change_class.getItems().addAll(classes);
        delete_class.getItems().addAll(classes);
        import_origin.getItems().addAll(Import_origin);
        import_origin.setValue(Import_origin[2]);
        export_origin.getItems().addAll(Export_and_Change_origin);
        change_origin.getItems().addAll(Export_and_Change_origin);

        btn_Export.setOnAction(event -> cl_export());
        btn_Import.setOnAction(event -> cl_import());
        btn_Change.setOnAction(event -> cl_change());
        btn_Delete.setOnAction(event -> cl_delete());
        btn_save_settings.setOnAction(event -> cl_settings());



        String[] mass_conn_settings = ConnectionSettings();
        settings_host.setText(mass_conn_settings[0]);
        settings_port.setText(mass_conn_settings[1]);
        settings_db_name.setText(mass_conn_settings[2]);
        settings_login.setText(mass_conn_settings[3]);
        settings_password.setText(mass_conn_settings[4]);

        btn_save_settings.setDisable(true);
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

    private void cl_change(){
        String _class = change_class.getValue();
        String _id = change_id.getText();
        String _type = change_type.getText();
        String _number = change_number.getText();
        String _date = String.valueOf(change_date.getValue());
        String _origin = change_origin.getValue();
        String _amount = change_amount.getText();
        String[] mass_conn_settings = ConnectionSettings();

        Change change = new Change(_id,  _class, _type, _number, _date, _origin, _amount, mass_conn_settings);
        _info = change.Change_getInfo();
        SetChangeInfo(_info);
    }

    private void cl_delete() {
        String _class = delete_class.getValue();
        String _id = delete_id.getText();
        String[] mass_conn_settings = ConnectionSettings();

        Delete delete = new Delete(_class, _id, mass_conn_settings);
        _info = delete.Delete_GetInfo();
        SetDeleteInfo(_info);

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
    private void SetDeleteInfo(String _info){delete_info.setText(_info);}
    private void SetChangeInfo(String _info){change_info.setText(_info);}

}

