package mk.bg.networking.serverapphandlers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleable;
import mk.bg.networking.interfaces.ServerAppHandleable;

/**
 *
 * @author Marta
 */
public abstract class ServerAppHandler
        implements Externalizable, ServerAppHandleable {

    // protected members
    protected Externalizable message;

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            ServerAppHandler.class.getName());
    private static final long serialVersionUID = 1L;

    // public constructors
    public ServerAppHandler() {
    }

    public ServerAppHandler(Externalizable message) {
        this.message = message;
    }

    // public methods
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(message);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        message = (Externalizable) in.readObject();
    }

    // protected methods
    protected static Optional<Player> getPlayerFromCollection(Set<Player> players,
            Player player) {
        return players
                .stream()
                .filter(p -> p.equals(player))
                .findAny();
    }

    protected static Optional<Entry<Player, Player>> getPlayerEntryFromMap(
            Map<Player, Player> players, Player player) {
        return players.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(player)
                || entry.getValue().equals(player))
                .findAny();
    }

    protected static Optional<Player> getOpponentPlayerFromMap(
            Map<Player, Player> players, Player player) {
        Optional<Entry<Player, Player>> optEntry
                = getPlayerEntryFromMap(players, player);

        if (optEntry.isPresent()) {
            Entry<Player, Player> entry = optEntry.get();
            return entry.getKey().equals(player) ? Optional.of(entry.getValue())
                    : Optional.of(entry.getKey());
        }

        return Optional.empty();
    }

    protected static void sendMessageToPlayer(Player player,
            PlayerAppHandleable handler) {
        try {
            player.getOos().writeObject(handler);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}

