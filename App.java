import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("App");
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("app.fxml"))));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}