package mk.bg.networking.serverapphandlers;

import java.util.Optional;
import java.util.logging.Logger;
import mk.bg.game.Player;
import mk.bg.networking.messages.ChallengeResponseMsg;
import mk.bg.networking.playerapphandlers.ChallengeResponsePAH;

/**
 *
 * @author Marta
 */
public class ChallengeResponseSAH extends ServerAppHandler {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            ChallengeResponseSAH.class.getName());
    private static final long serialVersionUID = 1L;

    // public constructors
    public ChallengeResponseSAH() {
    }

    public ChallengeResponseSAH(ChallengeResponseMsg message) {
        super(message);
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        ChallengeResponseMsg msg = (ChallengeResponseMsg) super.message;
        Optional<Player> optOpponentPlayer = getPlayerFromCollection(
                members.getUnpairedPlayers(), msg.getOpponentPlayer());

        if (optOpponentPlayer.isPresent()) {
            Player opponentPlayer = optOpponentPlayer.get();
            sendMessageToPlayer(opponentPlayer, new ChallengeResponsePAH(
                    new ChallengeResponseMsg(player, msg.isRequestAccepted())));
            if (msg.isRequestAccepted()) {
                members.getUnpairedPlayers().remove(player);
                members.getUnpairedPlayers().remove(opponentPlayer);
                members.getDisconnectedPlayers().add(player);
                members.getDisconnectedPlayers().add(opponentPlayer);
                members.getPairedPlayers().put(player, opponentPlayer);
            }
            LOGGER.info("Challenge response from player " + player.getId()
                    + " forwarded to player " + opponentPlayer.getId() + ".");
        }
    }
}
