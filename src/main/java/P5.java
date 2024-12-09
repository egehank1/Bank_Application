import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * stage.show()
 * zeigt das Fenster
 */
public class P5 extends Application {
    /**
     * The start() method is the main entry point for JavaFX applications, taking a Stage object (the window) as a parameter.
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(P5.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Bank");
        stage.setScene(scene);
        stage.show();
    }
    /**
     *launch(args)führt
     * zum Start von
     * start(Stage stage)
     * @param args
     */
    public static void main(String[] args) {
        // Java Runtime wird initialisiert, start() wird aufgerufen.
        launch();
    }
}