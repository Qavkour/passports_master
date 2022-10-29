/**
 * Метод ImportMethod перегружен 4 раза , что соответствует 4 комбинация ввода параметров:
 *  1) Введен только номер, оригинал/копия - неважно
 *  2) Введен номер и указан оригинал/копия
 *  3) Номер не введен, оригинал/копия - неважно, 2 даты и кол-во
 *  4) Номер не введен, указан оригинал/копия, 2 даты и кол-во
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

        if(ImportCheckErr(c_class, c_type, c_number, c_amount, c_oldDate, c_youngDate, c_savePath)){

            _class = c_class.toLowerCase().replaceAll(" ", "");
            _type = c_type.toUpperCase().replaceAll("-", "_");
            _number = c_number;
            if(c_amount != "") _amount = Integer.parseInt(c_amount);
            _oldDate = c_oldDate;
            _youngDate = c_youngDate;
            _savePath = c_savePath.charAt(c_savePath.length() - 1) == '/' ? c_savePath : c_savePath + "/";
            if(c_origin != null) _origin = c_origin.equals("Оригинал") ? 1 : 0;

            /////// Определяем комбнацию ввода и передаем данные в соответствующий 1 из 4 метод ///////
            if(!_number.equals("") && c_origin.equals("Не важно")) ImportMethod(_class, _type, _number);
            else if(!_number.equals("") && !c_origin.equals("Не важно")) ImportMethod(_class, _type, _number, _origin);
            else if(_number.equals("") && c_origin.equals("Не важно")) ImportMethod(_class, _type, _amount, _oldDate, _youngDate);
            else if(_number.equals("") && !c_origin.equals("Не важно")) ImportMethod(_class, _type, _amount, _oldDate, _youngDate, _origin);
        }
        else
            _info = "Не все поля заполнены корректно";

    }
    ////////////////// Комбинация 1: Введен только номер, оригинал/копия - неважно //////////////////
    private void ImportMethod(String _class, String _type, String _number){
        String sql = "SELECT `code`, `id` FROM " + _class + " WHERE `type` LIKE ? AND `number` = ?";
        try(Connection conn = getConn()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, _type);
            ps.setString(2, _number);
            ResultSet res = ps.executeQuery();
            res.next();
            decodeImage(res.getString("code"), _savePath + _type + "___" + String.valueOf(res.getInt("id")));
            res.close();
            ps.close();
        } catch (SQLException e) {
            _info = "Паспорт не найден";
        } catch(NullPointerException e){
            _info = "Отсутствует подключение к базе данных";
        } catch (IOException e) {
            _info = "Неверно указана директория";
        }

    }

    ////////////////// Комбинация 2: Введен номер и указан оригинал/копия //////////////////
    private void ImportMethod(String _class, String _type, String _number, int _origin){
        String sql = "SELECT `code`, `id` FROM " + _class + " WHERE `type` = ? AND `number` = ? AND `original` = ?";
        try(Connection conn = getConn()) {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setString(1, _type);
            ps.setString(2, _number);
            ps.setString(3, String.valueOf(_origin));
            ResultSet res = ps.executeQuery();
            res.next();
            decodeImage(res.getString("code"), _savePath + _type + "___" + String.valueOf(res.getInt("id")));
            res.close();
            ps.close();
        } catch (SQLException e) {
            _info = "Паспорт не найден";
        } catch(NullPointerException e){
            _info = "Отсутствует подключение к базе данных";
        } catch (IOException e) {
            _info = "Неверно указана директория";
        }

    }

    ////////////////// Комбинация 3: Номер не введен, оригинал/копия - неважно, 2 даты и кол-во //////////////////
    private void ImportMethod(String _class, String _type, int _amount, String _oldDate, String _youngDate){
        int i = 0;
        String sql = "SELECT `amount`, `code`, `id` FROM " + _class + " WHERE `type` LIKE ? AND DATE(date) > ? " +
                "AND DATE (date) < ?";
        try(Connection conn = getConn()) {
            PreparedStatement ps = conn.prepareStatement(sql);
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
            ps.close();
        } catch (SQLException e) {
            _info = "Паспортов не найдено: " + String.valueOf(_amount - i);
        } catch (NullPointerException e) {
            _info = "Отсутствует подключение к базе данных";
        } catch (IOException e) {
            _info = "Неверно указана директория";
        }
    }

    ////////////////// Комбинация 4: Номер не введен, указан оригинал/копия, 2 даты и кол-во //////////////////
    private void ImportMethod(String _class, String _type, int amount, String _oldDate, String _youngDate, int _origin) {
        int i = 0;
        String sql = "SELECT `amount`, `code`, `id` FROM " + _class + " WHERE `type` LIKE ? AND DATE(date) > ? " +
                "AND DATE (date) < ? AND original = ?";
        try(Connection conn = getConn()) {
            PreparedStatement ps = getConn().prepareStatement(sql);
            ps.setString(1, _type + '%');
            ps.setString(2, _oldDate);
            ps.setString(3, _youngDate);
            ps.setString(4, String.valueOf(_origin));
            ResultSet res = ps.executeQuery();
            res.next();
            while(i < _amount) { // Ищем, пока кол-во найденных не будет превосходить кол-во заданных
                decodeImage(res.getString("code"), _savePath + _type + "___" + String.valueOf(res.getInt("id")));
                i += res.getInt("amount"); // Считаем, сколько оборудования уже нашлось
                res.next();
            }
            res.close();
            ps.close();
        } catch (SQLException e) {
            _info = "Паспортов не найдено: " + String.valueOf(_amount - i);
        } catch (NullPointerException e) {
            _info = "Отсутствует подключение к базе данных";
        } catch (IOException e) {
            _info = "Неверно указана директория";
        }
    }

    ////////////// Декодирует изображение //////////////
    private void decodeImage(String imageString, String savePath) throws IOException {
        byte data[] = Base64.getDecoder().decode(imageString);
        FileOutputStream fos = new FileOutputStream(savePath);
        fos.write(data);
        fos.close();
    }



    ////////////// Проверяет наличие некорректно заполненных полей //////////////
    private boolean ImportCheckErr(String _class, String _type, String _number, String _amount, String _oldDate,
                                   String _youngDate, String _savePath){
        boolean NoErr = true;
        if(_class == null){NoErr = false;}
        if(_type == ""){NoErr = false;}
        if(_number == "" && (_amount == "" || Integer.parseInt(_amount) < 1)){NoErr = false;}
        if(_number == "" && _oldDate == null){NoErr = false;}
        if(_number == "" && _youngDate == null){NoErr = false;}
        if(_savePath == ""){NoErr = false;}

        return NoErr; /////////// Если нет пустых полей - возвращаем true ///////////
    }

    ////////////// Передает информацию  в контроллер //////////////
    public String Import_GetInfo(){return _info;}
}
