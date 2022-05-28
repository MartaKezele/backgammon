package mk.bg.rmi;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.serverapphandlers.ServerAppMembers;
import mk.bg.rmi.service.ClientChatService;
import mk.bg.rmi.service.ServerChatService;

/**
 *
 * @author Marta
 */
public final class ChatServer {

    // private members  
    private static final Logger LOGGER = Logger.getLogger(
            ChatServer.class.getName());

    private static final String RMI_CLIENT = "client";
    private static final String RMI_SERVER = "server";
    // rmi server runs on this port, and client connects to this port
    // but when interprocess communication starts 
    // (handshake between server and client)
    // every exported object will receive a random port
    private static final int REMOTE_PORT = 1099;
    // When it comes to a handshake with the client
    // the object needs a different port to communicate on 
    // because 1099 needs to be free for other inquirys.
    // RANDOM_PORT_HINT variable hints that random port 
    // needs to be given to the object
    private static final int RANDOM_PORT_HINT = 0;

    private final ServerAppMembers members;
    private ServerChatService chatServer;
    private Registry registry;

    public ChatServer(ServerAppMembers members) {
        this.members = members;
        publishServer();
    }

    public void publishServer() {
        // create object of implementation
        chatServer = new ServerChatService() {
            @Override
            public void forwardChatMessage(Player fromPlayer, String message)
                    throws RemoteException {
                try {
                    ClientChatService fromClient
                            = (ClientChatService) registry.lookup(RMI_CLIENT
                                    + fromPlayer.getId());
                    Optional<Player> optToClient
                            = members.getOpponentFromPairedPlayers(fromPlayer);
                    if (optToClient.isPresent()) {
                        ClientChatService toClient
                                = (ClientChatService) registry.lookup(RMI_CLIENT
                                        + optToClient.get().getId());
                        toClient.postMessage(fromPlayer, message);
                    }
                } catch (NotBoundException | AccessException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        };
        // publish server
        try {
            // create RMI registry
            registry = LocateRegistry.createRegistry(REMOTE_PORT);
            // create stub which communicates with port given by the system
            ServerChatService stub
                    = (ServerChatService) UnicastRemoteObject.exportObject(
                            chatServer, RANDOM_PORT_HINT);
            // publish object under some name in registry
            registry.rebind(RMI_SERVER, stub);
            LOGGER.info("Published RMI server.");
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
