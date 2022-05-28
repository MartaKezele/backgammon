package mk.bg.controllers;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import mk.bg.PlayerApp;
import mk.bg.game.Game;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;
import mk.bg.networking.messages.ChallengeResponseMsg;
import mk.bg.networking.messages.PlayersMsg;
import mk.bg.networking.serverapphandlers.ChallengeResponseSAH;
import mk.bg.networking.serverapphandlers.DisconnectSAH;
import mk.bg.networking.serverapphandlers.PlayerSAH;
import mk.bg.networking.serverapphandlers.PlayersSAH;
import mk.bg.networking.serverapphandlers.ServerAppHandler;
import mk.bg.tasks.TimeoutTask;
import mk.bg.threads.MulticastListenerThread;
import mk.bg.threads.ServerMsgListenerThread;

/**
 * FXML Controller class
 *
 * @author Marta
 */
public class PlayerAppController
        implements Initializable, PlayerAppHandleableService {

    // protected members
    protected Player player;
    protected boolean playerAvailable;

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            PlayerAppController.class.getName());
    private static final String OPPONENT_LEFT = "opponent_left";
    private static final String WE_ARE_SORRY_TO_INFORM_YOU_THAT
            = "we_are_sorry_to_inform_you_that";
    private static final String LEFT_THE_GAME = "left_the_game";
    private static final String YOU_WILL_BE_REDIRECTED_TO
            = "you_will_be_redirected_to";
    private static final String PLAYERS_TAB = "players_tab";
    private static final String GAME_TAB = "game_tab";
    private static final String SHORTLY = "shortly";
    private static final String CHALLENGE_REQUEST = "challenge_request";
    private static final String HAS_CHALLENGED_YOU_TO_A_GAME
            = "has_challenged_you_to_a_game";
    private static final String DO_YOU_ACCEPT = "do_you_accept";
    private static final String CHALLENGE_RESPONSE = "challenge_response";
    private static final String IS_CURRENTLY_NOT_AVAILABLE
            = "is_currently_not_available";
    private static final String YOU_MAY_CHALLENGE_SOMEONE_ELSE
            = "you_may_challenge_someone_else";
    private static final String HAS_ACCEPTED_YOUR_CHALLENGE
            = "has_accepted_your_challenge";
    private static final String HAS_DECLINED_YOUR_CHALLENGE 
            = "has_declined_your_challenge";
    private ResourceBundle bundle;

    @FXML
    private ProfileController profileController;
    @FXML
    private PlayersController playersController;
    @FXML
    private GameController gameController;
    @FXML
    private ChatController chatController;
    @FXML
    private Tab tabProfile;
    @FXML
    private Tab tabPlayers;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabGame;
    @FXML
    private Tab tabChat;

    // getters
    public Tab getTabProfile() {
        return tabProfile;
    }

    public Tab getTabPlayers() {
        return tabPlayers;
    }

    public Tab getTabChat() {
        return tabChat;
    }

    // public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        profileController.setPlayerAppController(this);
        playersController.setPlayerAppController(this);
        gameController.setPlayerAppController(this);
        chatController.setPlayerAppController(this);

        initPlayer();
        playerAvailable = false;
        initThreads();
        sendMessageToServer(new PlayersSAH(new PlayersMsg()));

        PlayerApp.getPrimaryStage().setOnCloseRequest(event -> closeApp());
    }

    public void sendMessageToServer(ServerAppHandler handler) {
        try {
            player.getOos().reset();
            player.getOos().writeObject(handler);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    // player app handleable service
    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isPlayerAvailable() {
        return playerAvailable;
    }

    @Override
    public void showOpponentLeft() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString(OPPONENT_LEFT));
        alert.setHeaderText(bundle.getString(WE_ARE_SORRY_TO_INFORM_YOU_THAT)
                + " " + gameController.getGame().getBlackPlayer()
                + " " + bundle.getString(LEFT_THE_GAME));
        alert.setContentText(bundle.getString(YOU_WILL_BE_REDIRECTED_TO)
                + " " + bundle.getString(PLAYERS_TAB)
                + " " + bundle.getString(SHORTLY));
        if (alert.showAndWait().get() == ButtonType.OK) {
            sendMessageToServer(new PlayerSAH(player));
            handleAvailability(true);
            resetChat();
            gameController.resetGameTab();
            selectTab(tabPlayers);
        }
    }

    @Override
    public void showChallengeRequest(Player opponentPlayer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString(CHALLENGE_REQUEST));
        alert.setHeaderText(opponentPlayer.getNickname()
                + " " + bundle.getString(HAS_CHALLENGED_YOU_TO_A_GAME));
        alert.setContentText(bundle.getString(DO_YOU_ACCEPT));

        handleAvailability(false);
        gameController.initGame(opponentPlayer);
        chatController.initChat(opponentPlayer);

        GridPane grid = new GridPane();
        Label lblTimeout = new Label();
        grid.add(lblTimeout, 0, 0);
        alert.getDialogPane().setContent(grid);

        Task<Void> timeoutTask = new TimeoutTask(15, 1);
        lblTimeout.textProperty().bind(timeoutTask.messageProperty());

        timeoutTask.messageProperty().addListener(
                (ObservableValue<? extends String> observable,
                        String oldValue,
                        String newValue) -> {
                    if (newValue.equals(String.valueOf(0))) {
                        alert.close();
                    }
                });

        Thread timeoutThread = new Thread(timeoutTask);
        timeoutThread.setDaemon(true);
        timeoutThread.start();

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                timeoutThread.interrupt();
                timeoutThread.join();
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            sendMessageToServer(new ChallengeResponseSAH(
                    new ChallengeResponseMsg(opponentPlayer, true)));
            tabChat.setDisable(false);
            selectTab(tabGame);
        } else {
            try {
                timeoutThread.interrupt();
                timeoutThread.join();
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            sendMessageToServer(new ChallengeResponseSAH(
                    new ChallengeResponseMsg(opponentPlayer, false)));
            handleAvailability(true);
            resetChat();
            gameController.resetGameTab();
        }
    }

    @Override
    public void showPlayerUnavailable(Player opponentPlayer) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString(CHALLENGE_RESPONSE));
        alert.setHeaderText(opponentPlayer + " "
                + bundle.getString(IS_CURRENTLY_NOT_AVAILABLE));
        alert.setContentText(bundle.getString(YOU_MAY_CHALLENGE_SOMEONE_ELSE));
        alert.show();
        handleAvailability(true);
    }

    @Override
    public void showChallengeResponse(Player opponentPlayer,
            boolean requestAccepted) {
        if (requestAccepted) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(CHALLENGE_RESPONSE));
            alert.setHeaderText(opponentPlayer + " "
                    + bundle.getString(HAS_ACCEPTED_YOUR_CHALLENGE));
            alert.setContentText(bundle.getString(YOU_WILL_BE_REDIRECTED_TO)
                    + " " + bundle.getString(GAME_TAB)
                    + " " + bundle.getString(SHORTLY));
            chatController.initChat(opponentPlayer);
            gameController.initGame(opponentPlayer);
            if (alert.showAndWait().get() == ButtonType.OK) {
                tabChat.setDisable(false);
                selectTab(tabGame);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(CHALLENGE_RESPONSE));
            alert.setHeaderText(opponentPlayer + " "
                    + bundle.getString(HAS_DECLINED_YOUR_CHALLENGE));
            alert.setContentText(bundle.getString(YOU_MAY_CHALLENGE_SOMEONE_ELSE));
            alert.show();
            handleAvailability(true);
        }
    }

    @Override
    public void showPlayers(Set<Player> players) {
        playersController.showPlayers(players);
    }

    @Override
    public void showGameState(Game updatedGame) {
        gameController.showGameState(updatedGame);
    }

    // protected methods
    protected void handleAvailability(boolean available) {
        playerAvailable = available;
        profileController.implementAvailability(available);
        playersController.implementAvailability(available);
    }

    protected void selectTab(Tab tab) {
        tabPane.getSelectionModel().select(tab);
    }

    protected void resetChat() {
        chatController.resetChatTab();
        tabChat.setDisable(true);
    }

    // private methods
    private void initPlayer() {
        try {
            Socket socket = new Socket(PlayerApp.PROPERTIES.getProperty(
                    PlayerApp.LOCALHOST),
                    Integer.parseInt(PlayerApp.PROPERTIES.getProperty(
                            PlayerApp.SERVER_PORT)));
            player = new Player(socket);
            LOGGER.info("Connected to server.");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void initThreads() {
        ServerMsgListenerThread smlt = new ServerMsgListenerThread(player, this);
        smlt.setDaemon(true);
        smlt.start();

        MulticastListenerThread mlt = new MulticastListenerThread(this);
        mlt.setDaemon(true);
        mlt.start();
    }

    private void closeApp() {
        sendMessageToServer(new DisconnectSAH());
        player.disconnect();
        LOGGER.info("Disconnected from server.");
    }

}
