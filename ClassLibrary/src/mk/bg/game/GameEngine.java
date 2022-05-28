package mk.bg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Marta
 */
public class GameEngine {

    // private members
    private final Game game;

    // public constructors
    public GameEngine(Game game) {
        this.game = game;
    }

    // getters
    public Game getGame() {
        return game;
    }

    // public methods
    public Map<Circle, Set<Pane>> fetchWhitePlayerMoveOptions() {
        Map<Circle, Set<Pane>> moves = new HashMap<>();

        Field centerField = game.getBoard().getWhiteCenterField();
        if (!centerField.getChips().isEmpty()) {
            Set<Field> toFields = fetchWhitePlayerFieldOptions(centerField);
            Set<Pane> toPanes = mapFieldsToPanes(toFields);
            if (!toPanes.isEmpty()) {
                moves.put(centerField.getLastChip().get().getCircle(), toPanes);
            }
        } else {
            List<Field> fromFields = game.getBoard().getWhitePlayerFields();
            fromFields.forEach((fromField) -> {
                Set<Field> toFields = fetchWhitePlayerFieldOptions(fromField);
                Set<Pane> toPanes = mapFieldsToPanes(toFields);
                if (!toPanes.isEmpty()) {
                    moves.put(fromField.getLastChip().get().getCircle(), toPanes);
                }
            });
        }

        return moves;
    }

    public List<Integer> fetchDiceValues() {
        return game.getDice()
                .stream()
                .filter(d -> d.getVisible() && !d.getDisabled())
                .map(d -> d.getValue())
                .collect(Collectors.toList());
    }

    public Map<Integer, List<Integer>> fetchDiceCombinations(
            Map<Integer, List<Integer>> diceCombinations,
            List<Integer> diceValues,
            int currentIndex) {

        if (currentIndex == diceValues.size()) {
            return diceCombinations;
        }

        int result = 0;
        List<Integer> numbers = new ArrayList<>();
        for (int i = currentIndex; i < diceValues.size(); i++) {
            result += diceValues.get(i);
            numbers.add(diceValues.get(i));
            diceCombinations.put(result, new ArrayList<>(numbers));
        }
        return fetchDiceCombinations(diceCombinations, diceValues, currentIndex + 1);
    }

    public boolean whitePlayerInHomeBoard() {
        return !game.getBoard().getWhitePlayerFields()
                .stream()
                .anyMatch(field -> field.getNumber() > Board.NUMBER_OF_LAST_FIELD_IN_WHITE_HOME_BOARD);
    }

    public void disableDice(Dice... args) {
        for (Dice d : args) {
            d.setDisabled(true);
        }
    }

    // private methods
    private Set<Field> fetchWhitePlayerFieldOptions(Field fromField) {
        int fromFieldNumber = fromField.getNumber();
        Map<Integer, List<Integer>> jumpDetails = fetchWhitePlayerJumpDetails(fromFieldNumber);

        Set<Field> availableFields = new HashSet<>();

        jumpDetails.keySet().forEach(jumpDestination -> {
            Optional<Field> optField
                    = game.getBoard().getFieldByNumber(jumpDestination);

            if (optField.isPresent() && optField.get().isAvailableForWhitePlayer()) {
                for (Integer jumpStep : jumpDetails.get(jumpDestination)) {
                    Optional<Field> intermediateField = game.getBoard().getFieldByNumber(jumpStep);
                    if (intermediateField.isPresent() && intermediateField.get().isAvailableForWhitePlayer()) {
                        availableFields.add(optField.get());
                    }
                }
            } else if (!optField.isPresent()
                    && whitePlayerInHomeBoard()
                    && jumpDestination <= game.getBoard().getWhiteEndField().getNumber()) {
                for (Integer jumpStep : jumpDetails.get(jumpDestination)) {
                    Optional<Field> intermediateField = game.getBoard().getFieldByNumber(jumpStep);
                    if ((intermediateField.isPresent()
                            && intermediateField.get().isAvailableForWhitePlayer())
                            || (!intermediateField.isPresent()
                            && jumpStep <= game.getBoard().getWhiteEndField().getNumber())) {
                        availableFields.add(game.getBoard().getWhiteEndField());
                    }
                }
            }
        });

        return availableFields;
    }

    private Map<Integer, List<Integer>> fetchWhitePlayerJumpDetails(int fromFieldNumber) {

        List<Integer> diceValues = fetchDiceValues();

        Map<Integer, List<Integer>> diceCombinations = new HashMap<>();
        diceCombinations = fetchDiceCombinations(diceCombinations, diceValues, 0);

        Map<Integer, List<Integer>> jumpDetails = new HashMap<>();

        for (Integer key : diceCombinations.keySet()) {
            Integer jumpDestination
                    = fromFieldNumber - key;

            List<Integer> jumpSteps = diceCombinations.get(key)
                    .stream()
                    .map(value -> fromFieldNumber - value)
                    .collect(Collectors.toList());

            jumpDetails.put(jumpDestination, jumpSteps);
        }

        return jumpDetails;
    }

    private Set<Pane> mapFieldsToPanes(Set<Field> fields) {
        if (!fields.isEmpty()) {
            Set<Pane> panes = fields
                    .stream()
                    .map(f -> f.getContainer())
                    .collect(Collectors.toSet());
            return panes;
        }

        return new HashSet<>();
    }

}
