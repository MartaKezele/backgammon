package mk.bg.networking.messages;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Logger;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public class ChallengeResponseMsg implements Externalizable {

    //private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            ChallengeResponseMsg.class.getName());

    private Player opponentPlayer;
    private boolean requestAccepted;

    // public constructors
    public ChallengeResponseMsg() {
    }

    public ChallengeResponseMsg(Player opponentPlayer, boolean requestAccepted) {
        this.opponentPlayer = opponentPlayer;
        this.requestAccepted = requestAccepted;
    }

    // getters
    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public boolean isRequestAccepted() {
        return requestAccepted;
    }

    // public methods
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(opponentPlayer);
        out.writeBoolean(requestAccepted);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        opponentPlayer = (Player) in.readObject();
        requestAccepted = in.readBoolean();
    }
}


