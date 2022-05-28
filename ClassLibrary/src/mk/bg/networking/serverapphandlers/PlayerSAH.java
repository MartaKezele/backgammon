package mk.bg.networking.serverapphandlers;

import java.util.logging.Logger;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public class PlayerSAH extends ServerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            PlayerSAH.class.getName());

    // public constructors
    public PlayerSAH() {
    }

    public PlayerSAH(Player message) {
        super(message);
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        Player p = (Player) (super.message);
        setPlayerInfo(player, p);
        if (!members.getUnpairedPlayers().contains(player)) {
            members.getUnpairedPlayers().add(player);
        }
        members.getConnectedPlayers().add(player);
        LOGGER.info("Received player info message, player id: "
                + player.getId() + ".");
    }

    // private methods
    private void setPlayerInfo(Player oldPlayer, Player newPlayer) {
        oldPlayer.setNickname(newPlayer.getNickname());
    }

}