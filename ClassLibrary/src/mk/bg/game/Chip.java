package mk.bg.game;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Marta
 */
public class Chip implements Externalizable {

    // public members
    public static final double CIRCLE_RADIUS = 9.5;
    public static final String CIRCLE_ID = "circle%d";

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Chip.class.getName());

    private Circle circle;
    private int number;
    private Color color;

    // public constructors
    public Chip() {
    }

    public Chip(Circle circle, int number, Color color) {
        circle.setId(String.format(CIRCLE_ID, number));
        this.circle = circle;
        this.number = number;
        this.color = color;
    }

    public Chip(int number, Color color) {
        this.number = number;
        this.color = color;
    }

    // getters
    public Circle getCircle() {
        return circle;
    }

    public int getNumber() {
        return number;
    }

    public Color getColor() {
        return color;
    }

    // setters
    public void setColor(Color color) {
        this.color = color;
    }

    // public methods
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.circle);
        hash = 29 * hash + this.number;
        hash = 29 * hash + Objects.hashCode(this.color);
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
        final Chip other = (Chip) obj;
        if (this.number != other.number) {
            return false;
        }
        if (!Objects.equals(this.circle, other.circle)) {
            return false;
        }
        return Objects.equals(this.color, other.color);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(number);
        out.writeUTF(color.toString());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        number = in.readInt();
        if (in.readUTF().equals(Color.WHITE.toString())) {
            color = Color.WHITE;
        } else {
            color = Color.BLACK;
        }
    }
}
