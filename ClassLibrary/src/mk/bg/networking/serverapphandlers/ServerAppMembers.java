package mk.bg.networking.serverapphandlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public class ServerAppMembers {

    // public members
    private final Set<Player> unpairedPlayers;
    private final Map<Player, Player> pairedPlayers;
    private final LinkedBlockingDeque<Player> connectedPlayers;
    private final LinkedBlockingDeque<Player> disconnectedPlayers;

    // public constructors
    public ServerAppMembers() {
        unpairedPlayers = new HashSet<>();
        pairedPlayers = new HashMap<>();
        connectedPlayers = new LinkedBlockingDeque<>();
        disconnectedPlayers = new LinkedBlockingDeque<>();
    }

    // getters
    public Set<Player> getUnpairedPlayers() {
        return unpairedPlayers;
    }

    public Map<Player, Player> getPairedPlayers() {
        return pairedPlayers;
    }

    public LinkedBlockingDeque<Player> getConnectedPlayers() {
        return connectedPlayers;
    }

    public LinkedBlockingDeque<Player> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    public Optional<Player> getOpponentFromPairedPlayers(Player player) {
        return ServerAppHandler.getOpponentPlayerFromMap(pairedPlayers, player);
    }

}
