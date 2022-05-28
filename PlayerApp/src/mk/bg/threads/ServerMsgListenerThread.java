package mk.bg.threads;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleable;
import mk.bg.networking.interfaces.PlayerAppHandleableService;

/**
 *
 * @author Marta
 */
public class ServerMsgListenerThread extends Thread {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            ServerMsgListenerThread.class.getName());

    private final Player player;
    private final PlayerAppHandleableService service;

    // public constructors
    public ServerMsgListenerThread(Player player,
            PlayerAppHandleableService service) {
        this.player = player;
        this.service = service;
    }

    // public methods
    @Override
    public void run() {
        while (!player.getSocket().isClosed()) {
            try {
                PlayerAppHandleable handler
                        = (PlayerAppHandleable) player.getOis().readObject();
                handler.handleOnPlayerApp(player, service);
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
