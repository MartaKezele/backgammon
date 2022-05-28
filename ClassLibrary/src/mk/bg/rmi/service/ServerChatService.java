package mk.bg.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public interface ServerChatService extends Remote {

    public void forwardChatMessage(Player fromPlayer, String message) 
            throws RemoteException;

}
