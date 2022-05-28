package mk.bg.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author Marta
 */
public class TimeoutTask extends Task<Void> {

    // private members    
    private static final Logger LOGGER = Logger.getLogger(
            TimeoutTask.class.getName());
    private int timeoutInSecs;
    private final int intervalDurationInSecs;

    // public constructors
    public TimeoutTask(int timeoutInSecs, int intervalDurationInSecs) {
        // timeout actally goes to zero, so we decrement it
        this.timeoutInSecs = --timeoutInSecs;
        this.intervalDurationInSecs = intervalDurationInSecs;
    }

    // public methods
    @Override
    protected Void call() throws Exception {
        while (timeoutInSecs >= 0) {
            updateMessage(String.valueOf(timeoutInSecs));
            Thread.sleep(intervalDurationInSecs * 1000);
            timeoutInSecs--;
        }
        return null;
    }

}
