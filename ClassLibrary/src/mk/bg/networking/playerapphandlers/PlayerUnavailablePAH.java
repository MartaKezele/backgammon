package mk.bg.networking.playerapphandlers;

import java.util.logging.Logger;
import javafx.application.Platform;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;

/**
 *
 * @author Marta
 */
public class PlayerUnavailablePAH extends PlayerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            PlayerUnavailablePAH.class.getName());

    // public constructors
    public PlayerUnavailablePAH() {
    }

    public PlayerUnavailablePAH(Player message) {
        super(message);
    }

    // public methods
    @Override
    public void handleOnPlayerApp(Player player, 
            PlayerAppHandleableService service) {
        Player opponentPlayer = (Player) super.message;
        Platform.runLater(() -> service.showPlayerUnavailable(opponentPlayer));
        LOGGER.info("Received player not available message from player " 
                + opponentPlayer);
    }

}
