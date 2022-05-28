package mk.bg.threads;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import mk.bg.controllers.GameController;
import mk.bg.game.Game;

/**
 *
 * @author Marta
 */
public class ReplayThread extends Thread {

    // private members    
    private static final Logger LOGGER = Logger.getLogger(
            ReplayThread.class.getName());
    private int timeoutInSecs;
    private final int intervalDurationInSecs;
    private final GameController controller;
    private final List<Game> gameStates;
    private final ResourceBundle bundle;

    // public constructors
    public ReplayThread(int timeoutInSecs,
            int intervalDurationInSecs,
            GameController controller,
            List<Game> gameStates,
            ResourceBundle bundle) {
        // timeout actally goes to zero, so we decrement it
        this.timeoutInSecs = --timeoutInSecs;
        this.intervalDurationInSecs = intervalDurationInSecs;
        this.controller = controller;
        this.gameStates = gameStates;
        this.bundle = bundle;
    }

    // public methods
    @Override
    public void run() {
        while (timeoutInSecs >= 0) {
            while (!controller.isReplaying()) {
                // replaying is on pause
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            Platform.runLater(() -> controller.showReplayGameState(
                    gameStates.get(GameController.gameStateCounter++)));
            if (timeoutInSecs == gameStates.size() - 5) {
                Platform.runLater(() -> controller.resetRolledLabels());
            }
            if (timeoutInSecs == 0) {
                Platform.runLater(() -> controller.showMessage(
                        bundle.getString(GameController.GAME_REPLAYED)));
                Platform.runLater(() -> controller.enableReplay());
            }
            try {
                Thread.sleep(intervalDurationInSecs * 1000);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            timeoutInSecs--;
        }
    }

}
