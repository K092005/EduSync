import bridge.JavaBridge;
import db.DBConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;
import java.nio.file.*;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("EduSync — Student Collaboration Platform");
        stage.setWidth(1200);
        stage.setHeight(750);
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // Load UI html
        WebView  webView = new WebView();
        WebEngine engine  = webView.getEngine();

        // Disable right-click context menu
        webView.setContextMenuEnabled(false);

        // Load the HTML file from the ui/ directory
        URL htmlUrl = getClass().getResource("/ui/index.html");
        if (htmlUrl == null) {
            // fallback: load from filesystem
            Path htmlPath = Paths.get("ui/index.html");
            if (!Files.exists(htmlPath)) {
                System.err.println("ERROR: ui/index.html not found!");
                Platform.exit();
                return;
            }
            engine.load(htmlPath.toUri().toString());
        } else {
            engine.load(htmlUrl.toExternalForm());
        }

        // Wire Java bridge once page finishes loading
        JavaBridge bridge = new JavaBridge(engine, stage);
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("java", bridge);
                System.out.println("[App] JavaBridge attached to window.java");
                // Tell JS that bridge is ready
                engine.executeScript("if(typeof onJavaBridgeReady==='function') onJavaBridgeReady();");
            }
            if (newState == Worker.State.FAILED) {
                System.err.println("[App] WebEngine failed to load page.");
            }
        });

        // Print JS console.log to Java console (useful for debugging)
        engine.setOnAlert(e -> System.out.println("[JS] " + e.getData()));

        Scene scene = new Scene(webView);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        DBConnection.close();
        System.out.println("[App] Shutting down.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
