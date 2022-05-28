package mk.bg.networking.playerapphandlers;

import java.util.logging.Logger;
import javafx.application.Platform;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;
import mk.bg.networking.messages.PlayersMsg;


/**
 *
 * @author Marta
 */
public class PlayersPAH extends PlayerAppHandler {
    
    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            PlayersPAH.class.getName());

    // public constructors
    public PlayersPAH() {
    }

    public PlayersPAH(PlayersMsg message) {
        super(message);
    }

    // pubblic methods
    @Override
    public void handleOnPlayerApp(Player player, 
            PlayerAppHandleableService service) {
        PlayersMsg playersMsg = (PlayersMsg) (super.message);
        playersMsg.getPlayers().remove(service.getPlayer());
        Platform.runLater(() -> service.showPlayers(playersMsg.getPlayers()));
        LOGGER.info("Received players message.");
    }
}

