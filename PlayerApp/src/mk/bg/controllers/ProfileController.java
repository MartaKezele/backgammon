package mk.bg.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import mk.bg.networking.serverapphandlers.PlayerSAH;

/**
 * FXML Controller class
 *
 * @author Marta
 */
public class ProfileController implements Initializable {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            ProfileController.class.getName());
    private ResourceBundle bundle;
    private PlayerAppController playerAppController;

    @FXML
    private TextField tfNickname;
    @FXML
    private Button btnConfirm;

    // setters
    protected void setPlayerAppController(PlayerAppController controller) {
        this.playerAppController = controller;
    }

    // public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
    }

    // protected methods
    protected void implementAvailability(boolean available) {
        btnConfirm.setDisable(!available);
    }

    // private methods
    @FXML
    private void handleConfirmBtn() {
        if (anyRequiredFieldEmpty()
                || (!anyRequiredFieldEmpty()
                && !playerAppController.playerAvailable
                && !playerAppController.player.getNickname().isEmpty())) {
            btnConfirm.setDisable(true);
        } else {
            btnConfirm.setDisable(false);
        }
    }

    @FXML
    private void confirm() {
        String nickname = tfNickname.getText().trim();
        if (playerAppController.player.getNickname().isEmpty()
                || !nickname.equals(playerAppController.player.getNickname())) {
            playerAppController.player.setNickname(nickname);
            playerAppController.sendMessageToServer(
                    new PlayerSAH(playerAppController.player));
            LOGGER.info("Sent player info message.");
            playerAppController.handleAvailability(true);
            playerAppController.selectTab(playerAppController.getTabPlayers());
        }
    }

    private boolean anyRequiredFieldEmpty() {
        return tfNickname.getText().trim().isEmpty();
    }

}
