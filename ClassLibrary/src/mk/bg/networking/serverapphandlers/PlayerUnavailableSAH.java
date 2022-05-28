package mk.bg.networking.serverapphandlers;

import java.util.Optional;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.playerapphandlers.PlayerUnavailablePAH;

/**
 *
 * @author Marta
 */
public class PlayerUnavailableSAH extends ServerAppHandler {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            PlayerUnavailableSAH.class.getName());
    private static final long serialVersionUID = 1L;

    // public constructors
    public PlayerUnavailableSAH() {
    }

    public PlayerUnavailableSAH(Player message) {
        super(message);
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        Player p = (Player) super.message;
        Optional<Player> opponentPlayer = getPlayerFromCollection(
                members.getUnpairedPlayers(), p);
        if (opponentPlayer.isPresent()) {
            sendMessageToPlayer(opponentPlayer.get(),
                    new PlayerUnavailablePAH(player));
            LOGGER.info("Forwarded player not available message from player "
                    + player.getId() + " to player " 
                    + opponentPlayer.get().getId() + ".");
        }
    }

}
