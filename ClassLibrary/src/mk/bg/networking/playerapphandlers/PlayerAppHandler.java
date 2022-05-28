package mk.bg.networking.playerapphandlers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Logger;
import mk.bg.networking.interfaces.PlayerAppHandleable;

/**
 *
 * @author Marta
 */
public abstract class PlayerAppHandler
        implements Externalizable, PlayerAppHandleable {

    // protected members
    protected Externalizable message;

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            PlayerAppHandler.class.getName());
    private static final long serialVersionUID = 1L;

    // public constructors
    public PlayerAppHandler() {
    }

    public PlayerAppHandler(Externalizable message) {
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

}

