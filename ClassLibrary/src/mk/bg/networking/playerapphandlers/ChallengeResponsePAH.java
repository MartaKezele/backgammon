package mk.bg.networking.playerapphandlers;

import java.util.logging.Logger;
import javafx.application.Platform;
import mk.bg.game.Player;
import mk.bg.networking.interfaces.PlayerAppHandleableService;
import mk.bg.networking.messages.ChallengeResponseMsg;

/**
 *
 * @author Marta
 */
public class ChallengeResponsePAH extends PlayerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            ChallengeResponsePAH.class.getName());

    // public constructors
    public ChallengeResponsePAH() {
    }

    public ChallengeResponsePAH(ChallengeResponseMsg message) {
        super(message);
    }

    // pubblic methods
    @Override
    public void handleOnPlayerApp(Player player, PlayerAppHandleableService service) {
        ChallengeResponseMsg msg = (ChallengeResponseMsg) super.message;
        Platform.runLater(() -> service.showChallengeResponse(
                msg.getOpponentPlayer(), msg.isRequestAccepted()));
        LOGGER.info("Received challenge response from player " 
                + msg.getOpponentPlayer() + ".");
    }
}

