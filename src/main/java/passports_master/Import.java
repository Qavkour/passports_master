/**
 * Метод ImportMethod перегружен 2 раза , что соответствует 2 комбинация ввода параметров:
 *  1) Номер введен
 *  2) Номер не введен
 *
 *  P.S. Если введен номер паспорта, то нет смысла указывать даты и кол-во, так как
 *      существует ровно один паспорт с таким номером в пределах указанного типа
 *
 *  P.S.2 Один паспорт(одна бумажка) может являться документом качества на несколько штук оборудования
 **/
package passports_master;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class Import extends DB {

    private String _class;
    private String _type;
    private String _number;
    private int _amount;
    private String _oldDate;
    private String _youngDate;
    private int _origin;
    private String _savePath;
    private String _info;


    public Import(String c_class, String c_type, String c_number, String c_amount, String c_oldDate,
                  String c_youngDate, String c_origin, String c_savePath, String[] mass_conn_settings){
        super(mass_conn_settings); // Передаем настройки бд в класс DB для установки подключения

        if(ImportCheckErr(c_class, c_type, c_number, c_amount, c_oldDate, c_youngDate, c_savePath)){ // Проверяем наличие некорректно заполненных полей
            _class = c_class.toLowerCase().replaceAll(" ", ""); // Для стандартизации хранимых данных
            _type = c_type.toUpperCase().replaceAll("-", "_");  // Для стандартизации хранимых данных
            _number = c_number;
            if(!c_amount.equals(""))_amount = Integer.parseInt(c_amount);
            _oldDate = c_oldDate;
            _youngDate = c_youngDate;
            _savePath = c_savePath.charAt(c_savePath.length() - 1) == '/' ? c_savePath : c_savePath + "/";
            if(!c_origin.equals("Не важно")) _origin = c_origin.equals("Оригинал") ? 1 : 0;
            else _origin = -1;

            /////// Определяем комбинацию ввода(см верхний комментарий) и передаем данные в соответствующий 1 из 2 методов ///////
            if(_number.equals(""))
                ImportMethod(_class, _type, _amount, _oldDate, _youngDate, _origin);
            else
                ImportMethod(_class, _type, _number, _origin);
        }
        else
            _info = "Не все поля заполнены корректно"; // Если класс ExportCheckErr() фиксирует ошибку и возвращает false
    }                                                 //                                выводим содержание проблемы

    ////////////////// Комбинация 1: Номер введен //////////////////
    private void ImportMethod(String _class, String _type, String _number, int _origin){
        String sql = "SELECT `code`, `id` FROM " + _class + " WHERE `type` LIKE ? AND `number` = ?";
        if(_origin != -1) sql += " AND `original` = " + _origin; // определяем, нужно ли искать строго оригиналы(копии), или не важно

        try(Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql);){
            ps.setString(1, _type);
            ps.setString(2, _number);
            ResultSet res = ps.executeQuery();
            res.next();
            decodeImage(res.getString("code"), _savePath + _type + "___" + res.getInt("id"));
            res.close();
        } catch (SQLException e) {
            _info = "Паспорт не найден";
        } catch(NullPointerException e){
            _info = "Отсутствует подключение к базе данных";
        } catch (IOException e) {
            _info = "Неверно указана директория";
        }

    }


    ////////////////// Комбинация 2: Номер не введен //////////////////
    private void ImportMethod(String _class, String _type, int _amount, String _oldDate, String _youngDate, int _origin){
        String sql = "SELECT `amount`, `code`, `id` FROM " + _class + " WHERE `type` LIKE ? AND DATE(date) >= ? " + "AND DATE (date) <= ?";
        if(_origin != -1) sql += " AND `original` = " + _origin; // определяем, нужно ли искать строго оригиналы(копии), или не важно
        int i = 0;

        try(Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, _type + '%');
            ps.setString(2, _oldDate);
            ps.setString(3, _youngDate);
            ResultSet res = ps.executeQuery();
            res.next();
            while(i < _amount) { // Ищем, пока кол-во найденных не будет превосходить кол-во заданных
                decodeImage(res.getString("code"), _savePath + _type + "___" + String.valueOf(res.getInt("id")));
                i+= res.getInt("amount"); // Считаем, сколько оборудования уже нашлось
                res.next();
            }
            res.close();
        } catch (SQLException e) {
            _info = "Паспортов не найдено: " + (_amount - i);
        } catch (NullPointerException e) {
            _info = "Отсутствует подключение к базе данных";
        } catch (IOException e) {
            _info = "Неверно указана директория";
        }
    }

    ////////////// Декодирует изображение //////////////
    private void decodeImage(String imageString, String savePath) throws IOException {
        byte[] data = Base64.getDecoder().decode(imageString);
        FileOutputStream fos = new FileOutputStream(savePath);
        fos.write(data);
        fos.close();
    }



    ////////////// Проверяет наличие некорректно заполненных полей //////////////
    private boolean ImportCheckErr(String _class, String _type, String _number, String _amount, String _oldDate,
                                   String _youngDate, String _savePath){
        boolean NoErr = true;
        if(_class == null){NoErr = false;}
        if(_type.equals("")){NoErr = false;}
        if(_number.equals("") && _oldDate == null){NoErr = false;}
        if(_number.equals("") && _youngDate == null){NoErr = false;}
        if(_savePath.equals("")){NoErr = false;}
        try {
            if (_number.equals("") & (_amount.equals("") || Integer.parseInt(_amount) < 1)) {NoErr = false;}
        } catch (Exception e){NoErr = false;}

        return NoErr; // Если нет некорректно заполненных полей - возвращаем true
    }

    ////////////// Передает информацию  в контроллер //////////////
    public String Import_GetInfo(){return _info;}
}
