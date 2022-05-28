package mk.bg.threads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mk.bg.PlayerApp;
import mk.bg.networking.playerapphandlers.PlayersPAH;
import mk.bg.utilities.ByteUtils;
import mk.bg.networking.interfaces.PlayerAppHandleableService;

/**
 *
 * @author Marta
 */
public class MulticastListenerThread extends Thread {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            MulticastListenerThread.class.getName());
    private final PlayerAppHandleableService service;

    // public constructors
    public MulticastListenerThread(PlayerAppHandleableService service) {
        this.service = service;
    }

    // public methods
    @Override
    public void run() {
        int playerPort = Integer.parseInt(PlayerApp.PROPERTIES.getProperty(
                PlayerApp.PLAYER_PORT));
        String multicastAdress = PlayerApp.PROPERTIES.getProperty(
                PlayerApp.MULTICAST_ADDRESS);

        try (MulticastSocket player = new MulticastSocket(playerPort)) {
            
            InetAddress groupAdress = InetAddress.getByName(multicastAdress);
            player.joinGroup(groupAdress);
            LOGGER.info("Joined multicast group.");

            while (true) {
                byte[] msgLengthBytes = new byte[4];
                DatagramPacket packet = new DatagramPacket(msgLengthBytes, 
                        msgLengthBytes.length);
                player.receive(packet);
                int length = ByteUtils.byteArrayToInt(msgLengthBytes);

                byte[] msgBytes = new byte[length];
                packet = new DatagramPacket(msgBytes, msgBytes.length);
                player.receive(packet);

                try (ByteArrayInputStream bais = new ByteArrayInputStream(
                        msgBytes);
                        ObjectInputStream ois = new ObjectInputStream(bais)) {
                    PlayersPAH msg = (PlayersPAH) ois.readObject();
                    msg.handleOnPlayerApp(service.getPlayer(), service);
                }
            }
        } catch (SocketException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}



