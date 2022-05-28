package mk.bg.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public interface ClientChatService extends Remote {

    public void postMessage(Player fromPlayer, String message) 
            throws RemoteException;

}
