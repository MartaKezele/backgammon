package mk.bg.game;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 *
 * @author Marta
 */
public class Board implements Externalizable {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Board.class.getName());

    public static final int NUMBER_OF_LAST_FIELD_IN_WHITE_HOME_BOARD = 5;
    public static final int NUMBER_OF_LAST_FIELD_IN_BLACK_HOME_BOARD = 18;
    public static final int NUMBER_OF_FIRST_FIELD_IN_UPPER_BOARD_HALF = 12;

    private List<Field> fields;
    private Field whiteCenterField;
    private Field blackCenterField;
    private Field whiteEndField;
    private Field blackEndField;

    // public constructors
    public Board() {
    }

    public Board(List<Field> fields,
            Field whiteCenterField,
            Field blackCenterField,
            Field whiteEndField,
            Field blackEndField) {
        this.fields = fields;
        this.whiteCenterField = whiteCenterField;
        this.blackCenterField = blackCenterField;
        this.whiteEndField = whiteEndField;
        this.blackEndField = blackEndField;
    }

    // getters
    public List<Field> getFields() {
        return fields;
    }

    public Field getWhiteCenterField() {
        return whiteCenterField;
    }

    public Field getBlackCenterField() {
        return blackCenterField;
    }

    public Field getWhiteEndField() {
        return whiteEndField;
    }

    public Field getBlackEndField() {
        return blackEndField;
    }

    // public methods
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.fields);
        hash = 47 * hash + Objects.hashCode(this.whiteCenterField);
        hash = 47 * hash + Objects.hashCode(this.blackCenterField);
        hash = 47 * hash + Objects.hashCode(this.whiteEndField);
        hash = 47 * hash + Objects.hashCode(this.blackEndField);
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
        final Board other = (Board) obj;
        if (!Objects.equals(this.fields, other.fields)) {
            return false;
        }
        if (!Objects.equals(this.whiteCenterField, other.whiteCenterField)) {
            return false;
        }
        if (!Objects.equals(this.blackCenterField, other.blackCenterField)) {
            return false;
        }
        if (!Objects.equals(this.whiteEndField, other.whiteEndField)) {
            return false;
        }
        return Objects.equals(this.blackEndField, other.blackEndField);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(fields);
        out.writeObject(whiteCenterField);
        out.writeObject(blackCenterField);
        out.writeObject(whiteEndField);
        out.writeObject(blackEndField);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fields = (List<Field>) in.readObject();
        whiteCenterField = (Field) in.readObject();
        blackCenterField = (Field) in.readObject();
        whiteEndField = (Field) in.readObject();
        blackEndField = (Field) in.readObject();
    }

    public Optional<Field> getFieldByNumber(int number) {
        for (Field field : fields) {
            if (field.getNumber() == number) {
                return Optional.of(field);
            }
        }

        return Optional.empty();
    }

    public List<Field> getWhitePlayerFields() {
        return fields
                .stream()
                .filter(field
                        -> !field.getChips().isEmpty()
                && field.getChips().get(0).getColor() == Color.WHITE)
                .collect(Collectors.toList());
    }

    public Optional<Field> getFieldFromPane(Pane pane) {
        if (pane.equals(whiteCenterField.getContainer())) {
            return Optional.of(whiteCenterField);
        } else if (pane.equals(blackCenterField.getContainer())) {
            return Optional.of(blackCenterField);
        } else if (pane.equals(whiteEndField.getContainer())) {
            return Optional.of(whiteEndField);
        } else if (pane.equals(blackEndField.getContainer())) {
            return Optional.of(blackEndField);
        } else {
            return getFieldByNumber(getFieldNumberFromPaneId(pane.getId()));
        }
    }

    public void clearChipsOnAllFields() {
        fields.forEach(field -> {
            field.getChips().clear();
            field.getContainer().getChildren().clear();
        });

        whiteCenterField.getChips().clear();
        whiteCenterField.getContainer().getChildren().clear();
        blackCenterField.getChips().clear();
        blackCenterField.getContainer().getChildren().clear();

        whiteEndField.getChips().clear();
        whiteEndField.getContainer().getChildren().clear();
        blackEndField.getChips().clear();
        blackEndField.getContainer().getChildren().clear();
    }

    // private methods
    private int getFieldNumberFromPaneId(String id) {
        return Integer.parseInt(id.substring(Field.FIELD_ID.length() - 2));
    }
}

