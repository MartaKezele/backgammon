package mk.bg.networking.playerapphandlers;

import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;

/**
 *
 * @author Marta
 */
public class PlayerPAH extends PlayerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(
            PlayerPAH.class.getName());

    // public constructors
    public PlayerPAH() {
    }

    public PlayerPAH(Player message) {
        super(message);
    }

    // public methods
    @Override
    public void handleOnPlayerApp(Player player, 
            PlayerAppHandleableService service) {
        Player p = (Player) (super.message);
        player.setId(p.getId());
        LOGGER.info("Received player info message.");
    }

}