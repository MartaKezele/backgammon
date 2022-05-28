package mk.bg.game;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Marta
 */
public class Field implements Externalizable {

    // public members
    public static final String FIELD_ID = "field%d";

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Field.class.getName());

    private Pane container;
    private int number;
    private List<Chip> chips;

    // public constructors
    public Field() {
    }

    public Field(Pane container, int number) {
        container.setId(String.format(FIELD_ID, number));
        this.container = container;
        this.number = number;
        this.chips = new ArrayList();
    }

    public Field(int number, List<Chip> chips) {
        this.number = number;
        this.chips = chips;
    }

    // getters
    public Pane getContainer() {
        return container;
    }

    public int getNumber() {
        return number;
    }

    public List<Chip> getChips() {
        return chips;
    }

    // setters
    public void addChip(Chip chip) {
        container.getChildren().add(chip.getCircle());
        chips.add(chip);
    }

    public void removeChip(Chip chip) {
        container.getChildren().remove(chip.getCircle());
        chips.remove(chip);
    }

    public void setChips(List<Chip> chips) {
        if (!chips.isEmpty()) {
            List<Circle> circles = chips
                    .stream()
                    .map(c -> c.getCircle())
                    .collect(Collectors.toList());
            container.getChildren().addAll(circles);
        }
        this.chips.clear();
        this.chips.addAll(chips);
    }

    // public methods
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.container);
        hash = 59 * hash + this.number;
        hash = 59 * hash + Objects.hashCode(this.chips);
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
        final Field other = (Field) obj;
        if (this.number != other.number) {
            return false;
        }
        if (!Objects.equals(this.container, other.container)) {
            return false;
        }
        return Objects.equals(this.chips, other.chips);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(number);
        out.writeObject(chips);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        number = in.readInt();
        chips = (List<Chip>) in.readObject();
    }

    public Optional<Chip> getLastChip() {
        if (!chips.isEmpty()) {
            if (getNumber() < Board.NUMBER_OF_FIRST_FIELD_IN_UPPER_BOARD_HALF) {
                return Optional.of(chips.get(0));
            } else {
                return Optional.of(chips.get(chips.size() - 1));
            }
        }

        return Optional.empty();
    }

    public boolean isAvailableForWhitePlayer() {
        if (chips.isEmpty()) {
            return true;
        }

        if (chips.get(0).getColor() != Color.WHITE) {
            if (chips.size() >= 2) {
                return false;
            }
        }

        return true;
    }

    public Optional<Chip> containsOneBlackChip() {
        List<Chip> opponentChips = chips
                .stream()
                .filter(chip -> chip.getColor() == (Color.BLACK))
                .collect(Collectors.toList());

        if (opponentChips.size() == 1) {
            return Optional.of(opponentChips.get(0));
        }

        return Optional.empty();
    }

}
