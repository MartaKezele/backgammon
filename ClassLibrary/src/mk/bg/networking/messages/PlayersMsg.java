package mk.bg.networking.messages;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public class PlayersMsg implements Externalizable {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            PlayersMsg.class.getName());
    private static final long serialVersionUID = 1L;

    private Set<Player> players;

    // public constructors
    public PlayersMsg() {
    }

    public PlayersMsg(Set<Player> players) {
        if (players == null) {
            players = new HashSet<>();
        }
        this.players = players;
    }

    // getters
    public Set<Player> getPlayers() {
        return players;
    }

    // public methods
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(players);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        players = (Set<Player>) in.readObject();
    }
}

