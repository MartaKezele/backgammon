package mk.bg.networking.playerapphandlers;

import java.util.logging.Logger;
import javafx.application.Platform;
import mk.bg.game.Game;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;


/**
 *
 * @author Marta
 */
public class GamePAH extends PlayerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            GamePAH.class.getName());

    // public constructors
    public GamePAH() {
    }

    public GamePAH(Game message) {
        super(message);
    }

    // pubblic methods
    @Override
    public void handleOnPlayerApp(Player player, 
            PlayerAppHandleableService service) {
        Game game = (Game) super.message;
        LOGGER.info("Received game message.");
        Platform.runLater(() -> service.showGameState(game));
    }
}

