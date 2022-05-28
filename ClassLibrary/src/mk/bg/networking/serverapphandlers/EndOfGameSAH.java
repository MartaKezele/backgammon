package mk.bg.networking.serverapphandlers;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public class EndOfGameSAH extends ServerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EndOfGameSAH.class.getName());

    // public constructors
    public EndOfGameSAH() {
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, 
            ServerAppMembers serverAppMembers) {
        Optional<Map.Entry<Player, Player>> optEntry = getPlayerEntryFromMap(
                serverAppMembers.getPairedPlayers(), player);
        if (optEntry.isPresent()) {
            Map.Entry<Player, Player> entry = optEntry.get();
            Player opponentPlayer = entry.getKey().equals(player) ? 
                    entry.getValue() : entry.getKey();
            serverAppMembers.getPairedPlayers().entrySet().remove(entry);
            LOGGER.info("Received end of game message from player " 
                    + player.getId());
        }
    }
}
