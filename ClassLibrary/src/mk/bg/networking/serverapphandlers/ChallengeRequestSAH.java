package mk.bg.networking.serverapphandlers;

import java.util.Optional;
import java.util.logging.Logger;
import mk.bg.networking.playerapphandlers.ChallengeRequestPAH;
import mk.bg.game.Player;

/**
 *
 * @author Marta
 */
public class ChallengeRequestSAH extends ServerAppHandler {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            ChallengeRequestSAH.class.getName());
    private static final long serialVersionUID = 1L;

    // public constructors
    public ChallengeRequestSAH() {
    }

    public ChallengeRequestSAH(Player message) {
        super(message);
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        Player p = (Player) (super.message);
        Optional<Player> opponentPlayer = super.getPlayerFromCollection(
                members.getUnpairedPlayers(), p);

        if (opponentPlayer.isPresent()) {
            sendMessageToPlayer(opponentPlayer.get(), 
                    new ChallengeRequestPAH(player));
            LOGGER.info("Challenge forwarded from player " + player.getId() 
                    + " to player " + opponentPlayer.get().getId() + ".");

        }
    }
}