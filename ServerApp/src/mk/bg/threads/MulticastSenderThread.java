package mk.bg.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mk.bg.networking.playerapphandlers.PlayersPAH;
import mk.bg.utilities.ByteUtils;
import mk.bg.ServerApp;
import mk.bg.networking.messages.PlayersMsg;

/**
 *
 * @author Marta
 */
public class MulticastSenderThread extends Thread {

    // private methods
    private static final Logger LOGGER = Logger.getLogger(
            MulticastSenderThread.class.getName());

    // public methods
    @Override
    public void run() {
        try (DatagramSocket server = new DatagramSocket()) {
            LOGGER.info("Server multicasting on port: "
                    + server.getLocalPort() + ".\n");

            int playerPort = Integer.parseInt(ServerApp.PROPERTIES
                    .getProperty(ServerApp.PLAYER_PORT));
            String multicastAddress = ServerApp.PROPERTIES
                    .getProperty(ServerApp.MULTICAST_ADDRESS);
            InetAddress groupAdress = InetAddress.getByName(multicastAddress);

            while (true) {
                if (!ServerApp.getMembers().getConnectedPlayers().isEmpty()
                        || !ServerApp.getMembers().getDisconnectedPlayers()
                                .isEmpty()) {
                    ServerApp.getMembers().getConnectedPlayers().clear();
                    ServerApp.getMembers().getDisconnectedPlayers().clear();

                    PlayersPAH msg = new PlayersPAH(new PlayersMsg(
                            ServerApp.getMembers().getUnpairedPlayers()));

                    byte[] msgBytes = ByteUtils.convertObjectToByteArray(msg);

                    // msg byte array length must be sent 
                    // before sending the msg byte array
                    byte[] msgBytesLength = ByteUtils.intToByteArray(
                            msgBytes.length);
                    DatagramPacket packet = new DatagramPacket(
                            msgBytesLength,
                            msgBytesLength.length,
                            groupAdress,
                            playerPort);
                    server.send(packet);
                    // sending the msg byte array
                    packet = new DatagramPacket(
                            msgBytes,
                            msgBytes.length,
                            groupAdress,
                            playerPort);
                    server.send(packet);
                    LOGGER.info("Multicasted available players.");
                }
            }
        } catch (SocketException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
