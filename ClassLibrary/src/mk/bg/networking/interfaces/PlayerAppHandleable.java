package mk.bg.networking.interfaces;

import mk.bg.game.Player;
        
/**
 *
 * @author Marta
 */
public interface PlayerAppHandleable {
    
    public void handleOnPlayerApp(Player player, 
            PlayerAppHandleableService service);
    
}
