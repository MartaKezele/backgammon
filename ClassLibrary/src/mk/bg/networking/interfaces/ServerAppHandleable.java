package mk.bg.networking.interfaces;

import mk.bg.game.Player;
import mk.bg.networking.serverapphandlers.ServerAppMembers;

/**
 *
 * @author Marta
 */
public interface ServerAppHandleable {
    
    public void handleOnServerApp(Player player, ServerAppMembers members);
    
}

