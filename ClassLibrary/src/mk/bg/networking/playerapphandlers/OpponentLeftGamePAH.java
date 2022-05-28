package mk.bg.networking.playerapphandlers;

import java.util.logging.Logger;
import javafx.application.Platform;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;

/**
 *
 * @author Marta
 */
public class OpponentLeftGamePAH extends PlayerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            OpponentLeftGamePAH.class.getName());

    // public constructors
    public OpponentLeftGamePAH() {
    }

    // pubblic methods
    @Override
    public void handleOnPlayerApp(Player player, 
            PlayerAppHandleableService service) {
        Platform.runLater(() -> service.showOpponentLeft());
        LOGGER.info("Received opponent left game message.");
    }
}
