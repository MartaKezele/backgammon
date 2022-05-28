package mk.bg.networking.serverapphandlers;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.playerapphandlers.OpponentLeftGamePAH;

/**
 *
 * @author Marta
 */
public class LeaveGameSAH extends ServerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            LeaveGameSAH.class.getName());

    // public constructors
    public LeaveGameSAH() {
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        Optional<Entry<Player, Player>> optEntry = getPlayerEntryFromMap(
                members.getPairedPlayers(), player);
        if (optEntry.isPresent()) {
            Entry<Player, Player> entry = optEntry.get();
            Player opponentPlayer = entry.getKey().equals(player) ?
                    entry.getValue() : entry.getKey();
            sendMessageToPlayer(opponentPlayer, new OpponentLeftGamePAH());
            LOGGER.info("Received leave game message from player " 
                    + player.getId()
                    + " and forwarded to player " 
                    + opponentPlayer.getId() + ".");
            members.getPairedPlayers().entrySet().remove(entry);
            members.getUnpairedPlayers().add(player);
            members.getConnectedPlayers().add(player);
        }
    }
}
