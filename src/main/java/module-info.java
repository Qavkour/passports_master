module passports_master{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens passports_master to javafx.fxml;
    exports passports_master;
}