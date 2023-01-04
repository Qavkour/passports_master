package passports_master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Change extends DB {

    private int _id;
    private String _class;
    private String _type;
    private String _number;
    private String _date;
    private int _origin;
    private int _amount;
    private String _code;
    private String _info;

    public Change(String c_id, String c_class, String c_type, String c_number,
                  String c_date, String c_origin, String c_amount, String[] mass_conn_settings) {
        super(mass_conn_settings);
        if (ChangeCheckErr(c_id, c_class, c_amount)) { // Проверяем наличие некорректно заполненных полей
            _id = Integer.parseInt(c_id);
            _class = c_class.toLowerCase().replaceAll(" ", "");// Для стандартизации хранимых данных
            _type = "\"" + c_type.toUpperCase().replaceAll("-", "_") + "\"";  // Для стандартизации хранимых данных
            _number = "\"" + c_number + "\"";
            _date = "\"" + c_date + "\"";
            if (c_origin != null) _origin = c_origin.equals("Оригинал") ? 1 : 0;
            else _origin = -1;
            if (!c_amount.equals("")) _amount = Integer.parseInt(c_amount);
            else _amount = -1;
            ChangeMethod(_id, _class, _type, _number, _date, _origin, _amount);
        } else _info = "Не все поля заполнены корректно";

    }

    private void ChangeMethod(int _id, String _class, String _type, String _number, String _date,
                              int _origin, int _amount) {
        String sql = "UPDATE `" + _class + "` SET ";                         ///////////
        if (!_type.equals("\"\"")) sql += "`type` = " + _type + ", ";         //    Жутчайший костыль
        if (!_number.equals("\"\"")) sql += "`number` = " + _number + ", ";  //
        if (_date.equals("\"\"")) sql += "`date` = " + _date + ", ";         //    Я открыт к любым правкам
        if (_origin != -1) sql += "`origin` = " + _origin + ", ";             //
        if (_amount != -1) sql += "`amount` = " + _amount + ", ";             ///////////
        sql = sql.substring(0, sql.length() - 2); // Убираем последнюю запятую
        sql += " WHERE `id` = " + _id;

        try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
            _info = "Запись успешно отредактирована";
        } catch (SQLException e) {
            _info = "Не удалось изменить запись";
        } catch (NullPointerException e) {
            _info = "Отсутствует подключение к бд";
        }
    }

    private boolean ChangeCheckErr(String c_id, String c_class, String _amount) {
        boolean NoErr = true;

        if (c_class == null) NoErr = false;

        try {
            if (c_id.equals("") || Integer.parseInt(c_id) < 1) NoErr = false;
        } catch (Exception e) {
            NoErr = false;
        }

        try {
            if (!_amount.equals("") && Integer.parseInt(_amount) < 1) {
                NoErr = false;
            }
        } catch (Exception e) {
            NoErr = false;
        }

        return NoErr; // Если нет некорректно заполненных полей - возвращаем true
    }

    public String Change_getInfo() {
        return _info;
    }
}
