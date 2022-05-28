package mk.bg.threads;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mk.bg.ServerApp;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.ServerAppHandleable;

/**
 *
 * @author Marta
 */
public class PlayerMsgListenerThread extends Thread {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            PlayerMsgListenerThread.class.getName());
    private final Player player;

    // public constructors
    public PlayerMsgListenerThread(Player player) {
        this.player = player;
    }

    // public methods
    @Override
    public void run() {
        while (!player.getSocket().isClosed()) {
            try {
                ServerAppHandleable msgHandler
                        = (ServerAppHandleable) player.getOis().readObject();
                msgHandler.handleOnServerApp(player, ServerApp.getMembers());
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
