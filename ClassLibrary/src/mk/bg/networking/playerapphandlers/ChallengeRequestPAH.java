package mk.bg.networking.playerapphandlers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;
import mk.bg.networking.serverapphandlers.PlayerUnavailableSAH;

/**
 *
 * @author Marta
 */
public class ChallengeRequestPAH extends PlayerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            ChallengeRequestPAH.class.getName());

    // public constructors
    public ChallengeRequestPAH() {
    }

    public ChallengeRequestPAH(Player message) {
        super(message);
    }

    // pubblic methods
    @Override
    public void handleOnPlayerApp(Player player, 
            PlayerAppHandleableService service) {
        Player opponentPlayer = (Player) super.message;
        LOGGER.info("Received challenge from player " + opponentPlayer + ".");
        if (service.isPlayerAvailable()) {
            Platform.runLater(() -> 
                    service.showChallengeRequest(opponentPlayer));
        } else {
            try {
                player.getOos().writeObject(
                        new PlayerUnavailableSAH(opponentPlayer));
                LOGGER.info("Sent player not available message to server, "
                        + "where it will be forwarded to player with id " 
                        + opponentPlayer + ".");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
