package mk.bg.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import mk.bg.game.Player;
import mk.bg.networking.serverapphandlers.ChallengeRequestSAH;

/**
 * FXML Controller class
 *
 * @author Marta
 */
public class PlayersController implements Initializable {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            PlayersController.class.getName());
    private static final String CHALLENGE_SENT = "challenge_sent";
    private static final String YOU_HAVE_CHALLENGED = "you_have_challenged";
    private static final String TO_A_GAME = "to_a_game";
    private static final String PLEASE_WAIT_FOR_THEIR_RESPONSE 
            = "please_wait_for_their_response";
    private static final String NO_PLAYER_SELECTED="no_player_selected";
    private static final String SELECT_A_PLAYER_TO_CHALLENGE
            ="select_a_player_to_challenge";
    private ResourceBundle bundle;
    private PlayerAppController playerAppController;
    private ObservableList<Player> players;
    private Optional<Player> selectedPlayer = Optional.empty();

    @FXML
    private Button btnChallengePlayer;
    @FXML
    private ListView<Player> lvPlayers;

    // setters
    protected void setPlayerAppController(PlayerAppController controller) {
        this.playerAppController = controller;
    }

    // public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        initPlayers();
    }

    // protected methods
    protected void implementAvailability(boolean available) {
        btnChallengePlayer.setDisable(!available);
    }

    protected void showPlayers(Set<Player> players) {
        this.players.clear();
        this.players.addAll(players);
    }

    // private methods
    @FXML
    private void challengePlayer(ActionEvent event) {
        if (selectedPlayer.isPresent()) {
            playerAppController.sendMessageToServer(new ChallengeRequestSAH(
                    selectedPlayer.get()));
            LOGGER.info("Sent challenge to player " + selectedPlayer.get() + ".");
            playerAppController.handleAvailability(false);
            // show challenge sent alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(CHALLENGE_SENT));
            alert.setHeaderText(bundle.getString(YOU_HAVE_CHALLENGED)
                    + " " + selectedPlayer.get()
                    + " " + bundle.getString(TO_A_GAME));
            alert.setContentText(bundle.getString(PLEASE_WAIT_FOR_THEIR_RESPONSE));
            alert.show();
        } else {
            // inform no player is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(NO_PLAYER_SELECTED));
            alert.setHeaderText(bundle.getString(SELECT_A_PLAYER_TO_CHALLENGE));
            alert.show();
        }
    }

    @FXML
    private void selectPlayer(MouseEvent event) {
        if (lvPlayers.getSelectionModel().getSelectedItem() != null) {
            selectedPlayer = Optional.of(lvPlayers.getSelectionModel()
                    .getSelectedItem());
        }
    }

    private void initPlayers() {
        players = FXCollections.observableArrayList();
        lvPlayers.setItems(players);
    }

}
