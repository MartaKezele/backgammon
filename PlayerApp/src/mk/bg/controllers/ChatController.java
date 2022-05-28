package mk.bg.controllers;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mk.bg.game.Player;
import mk.bg.rmi.ChatClient;

/**
 * FXML Controller class
 *
 * @author Marta
 */
public class ChatController implements Initializable {

    // private members  
    private static final Logger LOGGER = Logger.getLogger(
            ChatController.class.getName());

    private final String TIME_FORMAT = "HH:mm:ss";
    private static final String MESSAGE_FORMAT = "%s (%s): %s";
    private static final int MESSAGE_LENGTH = 78;
    private static final int FONT_SIZE = 15;

    private ResourceBundle bundle;
    private ObservableList<Node> messages;
    private ChatClient chatClient;
    private PlayerAppController playerAppController;

    @FXML
    private TextArea taMessage;
    @FXML
    private Button btnSend;
    @FXML
    private VBox vbChat;
    @FXML
    private ScrollPane spContainer;

    // public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
    }

    public void postMessage(String message, String name, Color color) {
        Platform.runLater(() -> addMessage(message, name, color));
    }

    // proteced methods
    protected void setPlayerAppController(PlayerAppController controller) {
        this.playerAppController = controller;
    }

    protected void initChat(Player opponentPlayer) {
        chatClient = new ChatClient(this, playerAppController.getPlayer());
        messages = FXCollections.observableArrayList();
        Bindings.bindContentBidirectional(messages, vbChat.getChildren());
        taMessage.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.length() >= MESSAGE_LENGTH) {
                        ((StringProperty) observable).setValue(oldValue);
                    }
                }
        );
    }

    protected void resetChatTab() {
        vbChat.getChildren().clear();
        taMessage.clear();
    }

    // private methods
    @FXML
    private void handleSendBtn(KeyEvent event) {
        if (taMessage.getText().isEmpty()) {
            btnSend.setDisable(true);
        } else {
            btnSend.setDisable(false);
        }
        if (!btnSend.isDisable() && event.getCode() == KeyCode.ENTER) {
            send();
        }
    }

    @FXML
    private void send() {
        if (taMessage.getText().trim().length() > 0) {
            chatClient.sendMessage(taMessage.getText().trim());
            addMessage(taMessage.getText().trim(), "you", Color.BLACK);
            taMessage.clear();
        }
    }

    private void addMessage(String message, String name, Color color) {
        Label label = new Label();
        label.setFont(new Font(FONT_SIZE));
        label.setTextFill(color);
        label.setText(String.format(MESSAGE_FORMAT,
                LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT)),
                name,
                message));
        messages.add(label);
        moveScrollPane();
    }

    private void moveScrollPane() {
        spContainer.applyCss();
        spContainer.layout();
        spContainer.setVvalue(1D);
    }

}
