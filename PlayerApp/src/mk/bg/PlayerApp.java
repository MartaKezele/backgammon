package mk.bg;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import mk.bg.utilities.ResourceUtils;

/**
 *
 * @author Marta
 */
public class PlayerApp extends Application {

    // public members
    public static final String SERVER_PORT = "SERVER_PORT";
    public static final String PLAYER_PORT = "PLAYER_PORT";
    public static final String MULTICAST_ADDRESS = "MULTICAST_ADDRESS";
    public static final String LOCALHOST = "LOCALHOST";
    public static final Properties PROPERTIES = new Properties();

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            PlayerApp.class.getName());

    public static final String VIEWS_PACKAGE = "mk.bg.views";
    public static final String BUNDLES_PACKAGE = "mk.bg.bundles";
    public static final String PLAYER_APP_VIEW = "PlayerApp.fxml";
    public static final String LANG_BUNDLE = "LangBundle";
    public static final String BACKGAMMON = "backgammon";
    public static final String UNABLE_TO_START_APP = "unable_to_start_app";
    private static final int SCENE_WIDTH = 700;
    private static final int SCENE_HEIGHT = 450;

    private static final String PROPERTIES_FILE = "playerAppConfig.properties";
    private static Stage primaryStage;
    private static ResourceBundle bundle;

    // getters
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    // load properties on app startup
    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    // main
    public static void main(String[] args) {
        launch(args);
    }

    // public methods
    @Override
    public void start(Stage primaryStage) throws IOException {
        PlayerApp.primaryStage = primaryStage;
        loadView(VIEWS_PACKAGE, PLAYER_APP_VIEW, new Locale("en", "EN"));
    }

    // private methods
    private void loadView(String packageName, String viewName, Locale locale)
            throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(ResourceBundle.getBundle(BUNDLES_PACKAGE + "."
                + LANG_BUNDLE, locale));
        bundle = fxmlLoader.getResources();
        Optional<URL> view = ResourceUtils.getResource(packageName, viewName);
        if (view.isPresent()) {
            Parent root = FXMLLoader.load(view.get(), bundle);
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
            primaryStage.setTitle(bundle.getString(BACKGAMMON));
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            new Alert(
                    Alert.AlertType.ERROR,
                    bundle.getString(bundle.getString(UNABLE_TO_START_APP)),
                    ButtonType.OK)
                    .show();
        }
    }

}
