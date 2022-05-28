package mk.bg.game;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;
import javafx.scene.control.Label;

/**
 *
 * @author Marta
 */
public class Dice implements Externalizable {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Dice.class.getName());

    private Label lblDice;
    private int value;
    private Boolean disabled;
    private Boolean visible;

    // public constructors
    public Dice() {
    }

    public Dice(Label lblDice) {
        this.lblDice = lblDice;
        value = 0;
        disabled = false;
        visible = false;
    }

    public Dice(int value, Boolean disabled, Boolean visible) {
        this.value = value;
        this.disabled = disabled;
        this.visible = visible;
    }

    // getters
    public int getValue() {
        return value;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public Boolean getVisible() {
        return visible;
    }

    // setters
    public void setValue(Integer value) {
        this.value = value;
        lblDice.setText(value.toString());
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
        lblDice.setDisable(disabled);
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
        lblDice.setVisible(visible);
    }

    // public methods
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.lblDice);
        hash = 47 * hash + this.value;
        hash = 47 * hash + Objects.hashCode(this.disabled);
        hash = 47 * hash + Objects.hashCode(this.visible);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Dice other = (Dice) obj;
        if (this.value != other.value) {
            return false;
        }
        if (!Objects.equals(this.lblDice, other.lblDice)) {
            return false;
        }
        if (!Objects.equals(this.disabled, other.disabled)) {
            return false;
        }
        return Objects.equals(this.visible, other.visible);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(value);
        out.writeBoolean(disabled);
        out.writeBoolean(visible);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        value = in.readInt();
        disabled = in.readBoolean();
        visible = in.readBoolean();
    }

    public int roll() {
        Random random = new Random();
        Integer number = random.nextInt(6) + 1;
        setValue(number);
        setVisible(true);
        setDisabled(false);
        return value;
    }

    public Boolean isVisibleAndEnabled() {
        return visible && !disabled;
    }

    public Boolean isVisibleAndDisabled() {
        return visible && disabled;
    }

}
