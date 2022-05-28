package mk.bg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import mk.bg.game.Player;
import mk.bg.networking.playerapphandlers.PlayerPAH;
import mk.bg.networking.serverapphandlers.ServerAppMembers;
import mk.bg.rmi.ChatServer;
import mk.bg.threads.MulticastSenderThread;
import mk.bg.threads.PlayerMsgListenerThread;

/**
 *
 * @author Marta
 */
public class ServerApp {

    // public members
    public static final String SERVER_PORT = "SERVER_PORT";
    public static final String PLAYER_PORT = "PLAYER_PORT";
    public static final String MULTICAST_ADDRESS = "MULTICAST_ADDRESS";
    public static final String LOCALHOST = "LOCALHOST";
    public static Properties PROPERTIES = new Properties();

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            ServerApp.class.getName());
    private static final String PROPERTIES_FILE = "serverAppConfig.properties";
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private static final ServerAppMembers members = new ServerAppMembers();

    // getters
    public static ServerAppMembers getMembers() {
        return members;
    }

    // load properties on app startup
    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    // main
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(members);
        try (ServerSocket serverSocket = new ServerSocket(
                Integer.parseInt(PROPERTIES.getProperty(SERVER_PORT)))) {

            LOGGER.info("Server listening on port "
                    + serverSocket.getLocalPort() + ".");

            // start multicast sender thread
            MulticastSenderThread mst = new MulticastSenderThread();
            mst.setDaemon(true);
            mst.start();
            while (true) {
                Socket playerSocket = serverSocket.accept();
                Player player = new Player(ID_GENERATOR.incrementAndGet(),
                        playerSocket);
                LOGGER.info("Player connected, id: " + player.getId() + ".");

                // start player message listener thread
                PlayerMsgListenerThread pmlt
                        = new PlayerMsgListenerThread(player);
                pmlt.setDaemon(true);
                pmlt.start();

                // send player info msg so player has their id
                player.getOos().writeObject(new PlayerPAH(player));
                LOGGER.info("Sent player info message to player with id: "
                        + player.getId() + ".");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
