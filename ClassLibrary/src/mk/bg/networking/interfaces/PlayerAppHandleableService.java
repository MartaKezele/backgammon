package mk.bg.networking.interfaces;

import java.util.Set;
import mk.bg.game.Game;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public interface PlayerAppHandleableService {

    public boolean isPlayerAvailable();

    public Player getPlayer();

    public void showOpponentLeft();

    public void showChallengeRequest(Player opponentPlayer);

    public void showPlayerUnavailable(Player opponentPlayer);

    public void showChallengeResponse(Player opponentPlayer, 
            boolean requestAccepted);

    public void showPlayers(Set<Player> players);

    public void showGameState(Game updatedGame);

}
