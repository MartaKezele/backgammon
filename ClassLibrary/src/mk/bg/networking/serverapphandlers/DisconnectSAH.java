package mk.bg.networking.serverapphandlers;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.playerapphandlers.OpponentLeftGamePAH;

/**
 *
 * @author Marta
 */
public class DisconnectSAH extends ServerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            DisconnectSAH.class.getName());

    // public constructors
    public DisconnectSAH() {
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        player.disconnect();
        members.getUnpairedPlayers().remove(player);
        Optional<Map.Entry<Player, Player>> optEntry 
                = getPlayerEntryFromMap(members.getPairedPlayers(), player);
        if (optEntry.isPresent()) {
            Map.Entry<Player, Player> entry = optEntry.get();
            Player opponentPlayer = entry.getKey().equals(player) ? 
                    entry.getValue() : entry.getKey();
            sendMessageToPlayer(opponentPlayer, new OpponentLeftGamePAH());
            members.getPairedPlayers().entrySet().remove(entry);
        }
        members.getDisconnectedPlayers().add(player);
        LOGGER.info("Player disconnected: " + player.getId() + ".");
    }

}
