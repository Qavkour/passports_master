package passports_master;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;


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

    /////////////// Список ///////////////
    private String[] classes = {"Опора", "Фундамент", "Анкер", "Деталь", "ЖП", "Лестница", "Оголовок", "Столик",
            "Груз ЖБ", "Рама Опорная", "Люлька", "Знак", "КБП", "Кронштейн", "Консоль", "Оттяжка",
            "Подвес", "Стойка", "Хомут", "Узел Крепления", "Фиксатор", "ГРПЗ", "Заземлитель",
            "Изолятор", "ОПН", "Привод", "Разъединитель", "Провод"};
    private String[] Import_origin = {"Оригинал", "Копия", "Не важно"};
    private String[] Export_and_Change_origin = {"Оригинал", "Копия"};

    @FXML
    void initialize() {

        ////////////// Добавляем списки //////////////
        import_class.getItems().addAll(classes);
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


        String[] mass_conn_settings = ConnectionSettings(); // Получаем параметры для подключения
        settings_host.setText(mass_conn_settings[0]);       // к удаленной бд с паспортами из локальной бд
        settings_port.setText(mass_conn_settings[1]);       // и выводим их
        settings_db_name.setText(mass_conn_settings[2]);
        settings_login.setText(mass_conn_settings[3]);
        settings_password.setText(mass_conn_settings[4]);

        btn_save_settings.setDisable(true);
    }

    private void cl_import() { // Считывает информация с экрана со вкладки Импорт

        String _class = import_class.getValue();  // Паспорта делятся на классы (Опора, фундамент и тп)
        String _type = import_type.getText();     // Классы делятся на типы (Фундамент может быть как ТСА-4,5-5, так и ТСА-5,0-5)
        String _number = import_number.getText(); // У каждого паспорта есть свой номер
        String _amount = import_amount.getText(); // У каждого паспорта есть количество изделий, на которое распространяется паспорт
        String _old_date = String.valueOf(import_old_date.getValue()); // Пользователь вводит дату самого старого из нужных паспортов
        String _young_date = String.valueOf(import_young_date.getValue()); // Пользователь вводит дату самого нового из нужных паспортов
        String _origin = import_origin.getValue(); // Ищем оригиналы, копии, или же неважно
        String _save_path = import_save_path.getText(); // Пользователь указывает конечную директорию, куда надо сохранить паспорта
        String[] mass_conn_settings = ConnectionSettings(); // Получаем параметры для подключения к удаленной бд с паспортами из локальной бд

        Import Import = new Import(_class, _type, _number, _amount, _old_date,
                _young_date, _origin, _save_path, mass_conn_settings);
        _info = Import.Import_GetInfo(); // Получаем информацию об ошибках или же об успехе из класса Import

        SetImportInfo(_info); // Выводим информацию на экран
    }

    private void cl_export() { // Считывает информация с экрана со вкладки Экспорт

        String _address = export_address.getText(); // Получаем адрес паспорта, которого нужно экспортировать в бд
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

        if (_info.contains("Добавлена")) { // Если экспорт завершился успехом, для удобства очищаем частоменяемые поля
            export_address.setText("");
            export_type.clear();
            export_number.clear();
            export_origin.setValue("");
            export_amount.clear();
        }
    }

    private void cl_change() { // Считывает информация с экрана со вкладки Изменить
        String _class = change_class.getValue();
        String _id = change_id.getText(); // В бд у каждого паспорта есть свой уникальный идентификатор в переделах таблицы
        String _type = change_type.getText();
        String _number = change_number.getText();
        String _date = String.valueOf(change_date.getValue());
        String _origin = change_origin.getValue();
        String _amount = change_amount.getText();
        String[] mass_conn_settings = ConnectionSettings();

        Change change = new Change(_id, _class, _type, _number, _date, _origin, _amount, mass_conn_settings);
        _info = change.Change_getInfo();
        SetChangeInfo(_info);
    }

    private void cl_delete() { // Считывает информация с экрана со вкладки Удалить
        String _class = delete_class.getValue();
        String _id = delete_id.getText();
        String[] mass_conn_settings = ConnectionSettings();

        Delete delete = new Delete(_class, _id, mass_conn_settings);
        _info = delete.Delete_GetInfo();
        SetDeleteInfo(_info);

    }

    private void cl_settings() { // Считывает информация с экрана со вкладки Настройки(параметры для подключения к бд)
        String _host = settings_host.getText(); // Хост
        String _port = settings_port.getText(); // Порт
        String _db_name = settings_db_name.getText(); // Имя бд
        String _login = settings_login.getText(); // Логин
        String _password = settings_password.getText(); // Пароль

        Settings settings = new Settings(_host, _port, _db_name, _login, _password);
        _info = settings.Settings_getInfo();
        SetSettingsInfo(_info);

        if (_info.contains("Подключено")) // Если по новым данным удалось подключиться к бд, изменяем настройки на актуальные
            settings_change();
    }

    @FXML/////////////// Блокирует поля ввода количества и дат, если поле "номер" заполнено ///////////////
    public void number_auto_disable() {                                 // см верхние комментарии в классе Import
        if (!import_number.getText().equals("")) {
            import_amount.setDisable(true);
            import_old_date.setDisable(true);
            import_young_date.setDisable(true);
        } else {
            import_amount.setDisable(false);
            import_old_date.setDisable(false);
            import_young_date.setDisable(false);
        }
    }

    @FXML
    public void settings_change() {
        String[] mass_conn_settings = ConnectionSettings();
        btn_save_settings.setDisable(
                settings_host.getText().equals(mass_conn_settings[0]) &&
                        settings_port.getText().equals(mass_conn_settings[1]) &&
                        settings_db_name.getText().equals(mass_conn_settings[2]) &&
                        settings_login.getText().equals(mass_conn_settings[3]) &&
                        settings_password.getText().equals(mass_conn_settings[4])
        );
    }

    /////////////// Обращаемся к сериализированному массиву для получения настроек подключения к бд с паспортами ///////////////
    private String[] ConnectionSettings() {
        String[] mass_conn_settings = new String[5];
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/META-INF/db_settings.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            mass_conn_settings = (String []) ois.readObject();
        } catch(IOException | ClassNotFoundException ignored){}

        return mass_conn_settings; // Возвращаем массив с настройками
    }

    /////////////////// Выводим информация на экран ///////////////////
    private void SetExportInfo(String _info) {
        export_info.setText(_info);
    }

    private void SetImportInfo(String _info) {
        import_info.setText(_info);
    }

    private void SetSettingsInfo(String _info) {
        settings_info.setText(_info);
    }

    private void SetDeleteInfo(String _info) {
        delete_info.setText(_info);
    }

    private void SetChangeInfo(String _info) {
        change_info.setText(_info);
    }

}

