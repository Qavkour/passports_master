package passports_master;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Delete extends DB{

    private String _class;
    private int _id;
    private String _info;

    public Delete(String c_class, String c_id, String[] mass_conn_settings){
        super(mass_conn_settings);

        if(DeleteCheckErr(c_class, c_id)){
            _class = c_class.toLowerCase();
            _id = Integer.parseInt(c_id);
            DeleteMethod(_class, _id);
        } else _info = "Не все поля заполнены корректно";
    }

    private void DeleteMethod(String _class, int _id){
        String sql = "DELETE FROM `" + _class + "` WHERE `id` = " + _id + " LIMIT 1";

        try(Connection conn = getConn(); Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
            _info = "Удалена запись " + _class + "(id: " + _id + ")";
        } catch (SQLException e) {
            _info = "Не удалось удалить запись";
        }
    }

    private boolean DeleteCheckErr(String c_class, String c_id){
        boolean NoErr = true;

        if(c_class == null) NoErr = false;
        try{ if(c_id.equals("") || Integer.parseInt(c_id) < 1) NoErr = false;}
        catch (Exception e){NoErr = false;}

        return NoErr;
    }

    public String Delete_GetInfo(){return _info;} //////// Передача информации в контроллер ////////
}
