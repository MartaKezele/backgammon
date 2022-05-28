package mk.bg.rmi;

import com.sun.jndi.rmi.registry.RegistryContextFactory;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javax.naming.Context;
import javax.naming.NamingException;
import mk.bg.controllers.ChatController;
import mk.bg.game.Player;
import mk.bg.jndi.InitialDirContextCloseable;
import mk.bg.rmi.service.ClientChatService;
import mk.bg.rmi.service.ServerChatService;

/**
 *
 * @author Marta
 */
public final class ChatClient {

    // private members  
    private static final Logger LOGGER = Logger.getLogger(
            ChatClient.class.getName());

    private static final String RMI_CLIENT = "client";
    private static final String RMI_SERVER = "server";
    private static final int REMOTE_PORT = 1099;
    private static final int RANDOM_PORT_HINT = 0;
    // communicate with registry through this url
    private static final String RMI_URL = "rmi://localhost:1099";

    private ClientChatService client;
    private ServerChatService server;
    private Registry registry;

    private final ChatController chatController;
    private final Player player;

    public ChatClient(ChatController chatController, Player player) {
        this.chatController = chatController;
        this.player = player;
        publishClient();
        fetchServer();
    }

    public void publishClient() {
        client = new ClientChatService() {
            @Override
            public void postMessage(Player fromPlayer, String message) 
                    throws RemoteException {
                chatController.postMessage(message, fromPlayer.getNickname(),
                        Color.DARKGREEN);
            }
        };
        try {
            // getting registry, not creating it!
            registry = LocateRegistry.getRegistry(REMOTE_PORT);
            ClientChatService stub = (ClientChatService) 
                    UnicastRemoteObject.exportObject(client, RANDOM_PORT_HINT);
            registry.rebind(RMI_CLIENT + player.getId(), stub);
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void fetchServer() {
        // lookup through JNDI, no need for an reference to a registry
        final Hashtable<String, String> properties = new Hashtable<>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY,
                RegistryContextFactory.class.getName());
        properties.put(Context.PROVIDER_URL, RMI_URL);

        try (InitialDirContextCloseable context
                = new InitialDirContextCloseable(properties)) {
            server = (ServerChatService) context.lookup(RMI_SERVER);
        } catch (NamingException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String message) {
        try {
            server.forwardChatMessage(player, message);
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
