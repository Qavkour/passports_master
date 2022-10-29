package passports_master;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class pm_Application extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(pm_Application.class.getResource("/fxml/main-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 910);
        stage.setTitle("Passports_Master");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {launch();}
}