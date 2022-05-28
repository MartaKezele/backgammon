package mk.bg.networking.serverapphandlers;

import mk.bg.networking.playerapphandlers.PlayersPAH;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.messages.PlayersMsg;

/**
 *
 * @author Marta
 */
public class PlayersSAH extends ServerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            PlayersSAH.class.getName());

    // public constructors
    public PlayersSAH() {
    }

    public PlayersSAH(PlayersMsg message) {
        super(message);
    }

    // pubblic methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        LOGGER.info("Received players message from player with id: "
                + player.getId() + ".");
        sendMessageToPlayer(player, new PlayersPAH(
                new PlayersMsg(members.getUnpairedPlayers())));
        LOGGER.info("Sent players message to player with id: "
                + player.getId() + ".");
    }

}
