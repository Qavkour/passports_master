package passports_master;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class Export extends DB {

    private String _address;
    private String _class;
    private String _type;
    private String _number;
    private String _date;
    private int _origin;
    private int _amount;
    private String _code;
    private String _info;

    public Export(String c_address, String c_class, String c_type, String c_number,
                  String c_date, String c_origin, String c_amount, String[] mass_conn_settings){
        super(mass_conn_settings); // Передаем настройки бд в класс DB для установки подключения

        if(ExportCheckErr(c_address, c_class, c_type, c_number, c_date, c_origin, c_amount)) {

            _address = c_address;
            _class = c_class.toLowerCase().replaceAll(" ", ""); //Приводим класс к нижнему регистру и убираем пробелы
            _type = c_type.toUpperCase().replaceAll("-", "_");  //Приводим тип к верхнему регистру
            _number = c_number;
            _date = c_date;
            _origin = c_origin.equals("Оригинал") ? 1 : 0;
            _amount = Integer.parseInt(c_amount);

            try {
                _code = encodeImage(_address); // Присваиваем переменной _code код файла
            } catch (IOException e) {
                _info = "Не удалось закодировать файл";
            }
            ExportMethod(_class, _type, _number, _date, _origin, _amount, _code);
        }
        else
            _info = "Не все поля заполнены корректно"; // Если класс ExportCheckErr() фиксирует ошибку и возвращает false
    }                                                 //                                выводим содеражание проблемы

    private void ExportMethod(String _class, String _type, String _number, String _date,
                               int _origin, int _amount, String _code){

        String sql = "INSERT INTO `" + _class + "` (`type` , `number`, `date`, `original`, `amount`, `code`) VALUES(?, ?, ?, ?, ?, ?)";

        try(Connection conn = getConn();  PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, _type);
            ps.setString(2, _number);
            ps.setString(3, _date);
            ps.setInt(4, _origin);
            ps.setInt(5, _amount);
            ps.setString(6, _code);
            ps.executeUpdate();
            _info = "Добавлена запись: " + _class + "(" + _type + ")";
        } catch (SQLException e) {
            _info = "Ошибка отправки файла в БД";
        }
    }

    ////////////// Кодирует изображение //////////////
    private String encodeImage(String _address) throws IOException {
        FileInputStream imageStream = new FileInputStream(_address);
        byte[] data = imageStream.readAllBytes();
        String imageString = Base64.getEncoder().encodeToString(data);
        imageStream.close();

        return imageString;
    }

    ////////////// Проверяет наличие некорректно заполненных полей //////////////
    private boolean ExportCheckErr(String _address, String _class, String _type, String _number, String _date, String _origin, String _amount){
        boolean NoErr = true;

        if(_address.equals("")){NoErr = false;}
        if(_class == null){NoErr = false;}
        if(_type.equals("")){NoErr = false;}
        if(_number.equals("")){NoErr = false;}
        if(_date == null){NoErr = false;}
        if(_origin == null){NoErr = false;}
        try {if (_amount.equals("") || Integer.parseInt(_amount) < 1) {NoErr = false;}
        } catch (Exception e){NoErr = false;}

        return NoErr; /////////// Если нет пустых полей - возвращаем true ///////////
    }

    public String Export_GetInfo(){return _info;} //////// Передача информации в контроллер ////////
}
