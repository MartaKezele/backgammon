package mk.bg.controllers;

import static java.lang.Math.abs;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import mk.bg.game.Board;
import mk.bg.game.Chip;
import mk.bg.game.Dice;
import mk.bg.game.Field;
import mk.bg.game.Game;
import mk.bg.game.GameEngine;
import mk.bg.game.Player;
import mk.bg.networking.serverapphandlers.EndOfGameSAH;
import mk.bg.networking.serverapphandlers.GameSAH;
import mk.bg.networking.serverapphandlers.LeaveGameSAH;
import mk.bg.networking.serverapphandlers.PlayerSAH;
import mk.bg.threads.ReplayThread;
import mk.bg.utilities.DOMUtils;

/**
 * FXML Controller class
 *
 * @author Marta
 */
public class GameController implements Initializable {

    // public members
    public static final String GAME_REPLAYED = "game_replayed";
    public static int gameStateCounter = 0;

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            GameController.class.getName());

    private static final String BG_LIGHT_BROWN = "bgLightBrown";
    private static final String BG_DARK_BROWN = "bgDarkBrown";
    private static final String BG_DARKER_BROWN = "bgDarkerBrown";
    private static final String BORDER_BLACK = "borderBlack";
    private static final String BORDER_TOP = "borderTop2";
    private static final String BORDER_BOTTOM = "borderBottom2";
    private static final String BORDER_TOP_OPEN = "borderTopOpen";
    private static final String BORDER_BOTTOM_OPEN = "borderBottomOpen";
    private static final String STROKE_LIGHT_BLUE = "strokeLightBlue3";
    private static final String BORDER_LIGHT_BLUE = "borderLightBlue3";

    private static final String ROLL_DICE_TO_DETERMINE_WHO_STARTS
            = "roll_dice_to_determine_who_starts";
    private static final String ROLLED = "rolled";
    private static final String ROLL_AGAIN = "roll_again";
    private static final String S_TURN = "s_turn";
    private static final String LEAVE_GAME_QUESTION = "leave_game_question";
    private static final String LEAVE_GAME_CONFIRMATION
            = "leave_game_confirmation";
    private static final String GAME_INFO_WILL_BE_LOST
            = "game_info_will_be_lost";
    private static final String REPLAYING_GAME = "replaying_game";
    private static final String YOU_ROLLED = "you_rolled";
    private static final String YOUR_TURN = "your_turn";
    private static final String PLEASE_WAIT_FOR = "please_wait_for";
    private static final String TO_ROLL_THEIR_DICE = "to_roll_their_dice";
    private static final String YOU_WIN = "you_win";
    private static final String WINS = "wins";
    private static final String END_OF_GAME = "end_of_game";
    private static final String YOU_WIN_THE_GAME = "you_win_the_game";
    private static final String YOU_CAN_REPLAY_GAME = "you_can_replay_game";
    private static final String WINS_THE_GAME = "wins_the_game";
    private static final String YOU = "you";
    private static final String PAUSE = "pause";
    private static final String RESUME = "resume";

    private ResourceBundle bundle;
    private PlayerAppController playerAppController;
    private Game game;
    private GameEngine gameEngine;
    private Integer whiteChipCounter = 0;
    private Integer blackChipCounter = 0;
    private Circle circleToMove;
    private boolean replaying;

    @FXML
    private Button btnRollDice;
    @FXML
    private Label lblPlayerRolled;
    @FXML
    private Label lblOpponentRolled;
    @FXML
    private Label lblDice1;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblDice2;
    @FXML
    private Label lblDice3;
    @FXML
    private Label lblDice4;
    @FXML
    private GridPane boardGrid;
    @FXML
    private Button btnLeaveGame;
    @FXML
    private Label lblOpponentNickname;
    @FXML
    private Label lblPlayerNickname;
    @FXML
    private Button btnReplay;
    @FXML
    private RadioButton rb1x;
    @FXML
    private RadioButton rb2x;
    @FXML
    private ToggleGroup tgReplaySpeed;
    @FXML
    private Label lblReplaySpeed;
    @FXML
    private Button btnPauseResume;

    // getters
    public Game getGame() {
        return gameEngine.getGame();
    }

    public boolean isReplaying() {
        return replaying;
    }

    // public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        initBoard();
    }

    public void showMessage(String message) {
        lblMessage.setText(message);
    }

    public void showReplayGameState(Game gameState) {
        updateBoard(gameState.getBoard());
        updateDice(gameState.getDice());
    }

    public void resetRolledLabels() {
        lblPlayerRolled.setText("");
        lblOpponentRolled.setText("");
    }

    public void enableReplay() {
        btnReplay.setDisable(false);
        lblReplaySpeed.setDisable(false);
        btnPauseResume.setDisable(true);
        rb1x.setDisable(false);
        rb2x.setDisable(false);
        gameStateCounter = 0;
    }

    // protected methods
    protected void setPlayerAppController(PlayerAppController controller) {
        this.playerAppController = controller;
    }

    protected void initGame(Player opponentPlayer) {
        Board board = initBoard();
        List<Dice> dice = initDice();
        game = new Game(playerAppController.player, opponentPlayer, board, dice);

        btnLeaveGame.setDisable(false);
        btnRollDice.setDisable(false);

        lblPlayerNickname.setText(bundle.getString(YOU));
        lblOpponentNickname.setText(opponentPlayer.getNickname());

        showMessage(bundle.getString(ROLL_DICE_TO_DETERMINE_WHO_STARTS));
        initGameEngine();
    }

    protected void resetGameTab() {
        btnLeaveGame.setDisable(true);
        btnRollDice.setDisable(true);

        lblPlayerNickname.setText("");
        lblOpponentNickname.setText("");

        lblPlayerRolled.setText("");
        lblOpponentRolled.setText("");

        lblMessage.setText("");

        resetDiceLabels();
        initBoard();

        DOMUtils.resetGameStates();
        disableReplay();
        btnPauseResume.setDisable(true);
    }

    protected void showGameState(Game updatedGame) {
        DOMUtils.addGameState(updatedGame);
        updateBoard(updatedGame.getBoard());
        updateDice(updatedGame.getDice());
        updateRolledLabels(updatedGame.getStarted());
        game.setStarted(updatedGame.getStarted());
        updateTurn(updatedGame.getTurn());
        if (checkAndHandleEndOfGame()) {
            showEndOfGameAlert();
        }
    }

    // private methods
    @FXML
    private void rollDice(ActionEvent event) {
        if (!game.getStarted()) {
            rollOneDice();
        } else {
            rollTwoDice();
            btnRollDice.setDisable(true);
            handleChips();
        }
        DOMUtils.addGameState(game);
        playerAppController.sendMessageToServer(new GameSAH(game));
    }

    @FXML
    private void leaveGame(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString(LEAVE_GAME_QUESTION));
        alert.setHeaderText(bundle.getString(LEAVE_GAME_CONFIRMATION));
        alert.setContentText(bundle.getString(GAME_INFO_WILL_BE_LOST));
        if (alert.showAndWait().get() == ButtonType.OK) {
            playerAppController.sendMessageToServer(new LeaveGameSAH());
            playerAppController.sendMessageToServer(new PlayerSAH(
                    playerAppController.player));
            playerAppController.handleAvailability(true);
            playerAppController.selectTab(playerAppController.getTabPlayers());
            resetGameTab();
            playerAppController.resetChat();
        }
    }

    ReplayThread replayThread;

    @FXML
    private void replay(ActionEvent event) {
        replaying = true;
        btnRollDice.setDisable(true);
        disableReplay();

        showMessage(bundle.getString(REPLAYING_GAME));

        List<Game> gameStates = DOMUtils.loadGameStates();

        int intervalDuration;
        if (tgReplaySpeed.getSelectedToggle().equals(rb1x)) {
            intervalDuration = 2;
        } else {
            intervalDuration = 1;
        }

        lblPlayerRolled.setText(bundle.getString(YOU_ROLLED));
        lblOpponentRolled.setText(
                game.getBlackPlayer() + " " + bundle.getString(ROLLED));

        replayThread = new ReplayThread(gameStates.size(),
                intervalDuration, this, gameStates, bundle);

        replayThread.start();
        btnPauseResume.setDisable(false);
    }

    @FXML
    private void pauseOrResume(ActionEvent event) {
        if (btnPauseResume.getText().equals(bundle.getString(PAUSE))) {
            // pause
            replaying = false;
            btnPauseResume.setText(bundle.getString(RESUME));
        } else {
            // resume
            replaying = true;
            btnPauseResume.setText(bundle.getString(PAUSE));
        }
    }

    // init methods
    private Board initBoard() {
        boardGrid.getChildren().clear();
        Board board = initFields();
        initChips(board);
        return board;
    }

    private Board initFields() {

        List<Field> fields = new ArrayList<>();
        Field whiteCenterField = null;
        Field blackCenterField = null;
        Field whiteEndField = null;
        Field blackEndField = null;

        // j helps calculate field number of top positioned fields
        // z helps calculate field number of bottom positioned fields
        int j = 12;
        int z = 11;
        for (int i = 0; i < 14; i++) {

            Pane topField, bottomField;

            if (i < 6) {
                bottomField
                        = generateField(
                                Pos.BOTTOM_CENTER,
                                i % 2 == 0 ? BG_DARK_BROWN : BG_LIGHT_BROWN,
                                BORDER_TOP,
                                BORDER_BLACK);
                topField
                        = generateField(
                                Pos.TOP_CENTER,
                                i % 2 == 0 ? BG_LIGHT_BROWN : BG_DARK_BROWN,
                                BORDER_BOTTOM,
                                BORDER_BLACK);

                fields.add(new Field(bottomField, z--));
                fields.add(new Field(topField, j++));
            } else if (i == 6) {
                bottomField
                        = generateField(
                                Pos.TOP_CENTER,
                                BG_DARKER_BROWN,
                                BORDER_BOTTOM_OPEN,
                                BORDER_BLACK);
                topField
                        = generateField(
                                Pos.BOTTOM_CENTER,
                                BG_DARKER_BROWN,
                                BORDER_TOP_OPEN,
                                BORDER_BLACK);

                whiteCenterField = new Field(topField, 24);
                blackCenterField = new Field(bottomField, -1);
            } else if (i == 13) {
                bottomField
                        = generateField(
                                Pos.BOTTOM_CENTER,
                                BG_DARKER_BROWN,
                                BORDER_BOTTOM_OPEN,
                                BORDER_BLACK);
                topField
                        = generateField(
                                Pos.TOP_CENTER,
                                BG_DARKER_BROWN,
                                BORDER_TOP_OPEN,
                                BORDER_BLACK);

                whiteEndField = new Field(bottomField, -1);
                blackEndField = new Field(topField, 24);
            } else {
                bottomField
                        = generateField(
                                Pos.BOTTOM_CENTER,
                                i % 2 == 0 ? BG_LIGHT_BROWN : BG_DARK_BROWN,
                                BORDER_TOP,
                                BORDER_BLACK);
                topField
                        = generateField(
                                Pos.TOP_CENTER,
                                i % 2 == 0 ? BG_DARK_BROWN : BG_LIGHT_BROWN,
                                BORDER_BOTTOM,
                                BORDER_BLACK);

                fields.add(new Field(bottomField, z--));
                fields.add(new Field(topField, j++));
            }

            boardGrid.add(bottomField, i, 1);
            boardGrid.add(topField, i, 0);
        }

        return new Board(
                fields,
                whiteCenterField,
                blackCenterField,
                whiteEndField,
                blackEndField);
    }

    private List<Dice> initDice() {
        List<Dice> dice = new ArrayList<>();
        dice.add(new Dice(lblDice1));
        dice.add(new Dice(lblDice2));
        dice.add(new Dice(lblDice3));
        dice.add(new Dice(lblDice4));
        return dice;
    }

    private void initGameEngine() {
        gameEngine = new GameEngine(game);
    }

    private Pane generateField(Pos alignment, String... styleClasses) {
        VBox field = new VBox();
        field.setAlignment(alignment);
        field.getStyleClass().addAll(styleClasses);
        return field;
    }

    private void initChips(Board board) {
        generateChipsInField(board.getFieldByNumber(21).get(),
                2, Color.BLACK, blackChipCounter);
        blackChipCounter += 2;
        generateChipsInField(board.getFieldByNumber(2).get(),
                2, Color.WHITE, whiteChipCounter);
        whiteChipCounter += 2;
        generateChipsInField(board.getFieldByNumber(23).get(),
                2, Color.BLACK, blackChipCounter);
        blackChipCounter += 2;
        generateChipsInField(board.getFieldByNumber(0).get(),
                2, Color.WHITE, whiteChipCounter);
        whiteChipCounter += 2;
//        generateChipsInField(board.getFieldByNumber(0).get(),
//                2, Color.BLACK, blackChipCounter);
//        blackChipCounter += 2;
//        generateChipsInField(board.getFieldByNumber(5).get(),
//                5, Color.WHITE, whiteChipCounter);
//        whiteChipCounter += 5;
//        generateChipsInField(board.getFieldByNumber(7).get(),
//                3, Color.WHITE, whiteChipCounter);
//        whiteChipCounter += 3;
//        generateChipsInField(board.getFieldByNumber(11).get(),
//                5, Color.BLACK, blackChipCounter);
//        blackChipCounter += 5;
//        generateChipsInField(board.getFieldByNumber(12).get(),
//                5, Color.WHITE, whiteChipCounter);
//        whiteChipCounter += 5;
//        generateChipsInField(board.getFieldByNumber(16).get(),
//                3, Color.BLACK, blackChipCounter);
//        blackChipCounter += 3;
//        generateChipsInField(board.getFieldByNumber(18).get(),
//                5, Color.BLACK, blackChipCounter);
//        blackChipCounter += 5;
//        generateChipsInField(board.getFieldByNumber(23).get(),
//                2, Color.WHITE, whiteChipCounter);
//        whiteChipCounter += 2;
    }

    private void generateChipsInField(
            Field field,
            int numberOfChips,
            Color color,
            Integer chipCounter) {

        List<Chip> chips = new ArrayList<>();

        for (int i = 0; i < numberOfChips; i++) {
            Circle circle = new Circle();
            circle.setRadius(Chip.CIRCLE_RADIUS);
            circle.setFill(color);
            chips.add(new Chip(circle, chipCounter++, color));
        }

        field.setChips(chips);
    }

    // other methods
    private void rollOneDice() {
        game.getDice().get(0).roll();
        lblPlayerRolled.setText(bundle.getString(YOU_ROLLED));
        determineTurn();
    }

    private void rollTwoDice() {
        Dice dice0 = game.getDice().get(0);
        Dice dice1 = game.getDice().get(1);

        dice0.roll();
        dice1.roll();

        if (dice0.getValue() == dice1.getValue()) {
            game.getDice().get(2).setValue(dice1.getValue());
            game.getDice().get(3).setValue(dice1.getValue());

            game.getDice().get(2).setVisible(true);
            game.getDice().get(3).setVisible(true);

            game.getDice().get(2).setDisabled(false);
            game.getDice().get(3).setDisabled(false);
        } else {
            game.getDice().get(2).setVisible(false);
            game.getDice().get(3).setVisible(false);
        }
    }

    private void determineTurn() {
        Dice dice1 = game.getDice().get(0);
        Dice dice2 = game.getDice().get(1);

        if (dice1.isVisibleAndEnabled() && dice2.isVisibleAndEnabled()) {
            if (dice1.getValue() > dice2.getValue()) {
                btnRollDice.setDisable(true);
                game.setStarted(true);
                game.setTurn(playerAppController.player);
                handleChips();
                showMessage(bundle.getString(YOUR_TURN));
            } else if (dice1.getValue() == dice2.getValue()) {
                gameEngine.disableDice(dice1, dice2);
                btnRollDice.setDisable(false);
                showMessage(bundle.getString(ROLL_AGAIN));
            } else {
                btnRollDice.setDisable(true);
                game.setStarted(true);
                game.setTurn(game.getBlackPlayer());
                showMessage(game.getBlackPlayer() + bundle.getString(S_TURN));
            }
        } else {
            btnRollDice.setDisable(true);
            showMessage(bundle.getString(PLEASE_WAIT_FOR) + " "
                    + game.getBlackPlayer() + " "
                    + bundle.getString(TO_ROLL_THEIR_DICE));
        }
    }

    private void handleChips() {
        Map<Circle, Set<Pane>> moveOptions
                = gameEngine.fetchWhitePlayerMoveOptions();
        if (moveOptions.isEmpty()) {
            btnRollDice.setDisable(true);
            game.setTurn(game.getBlackPlayer());
            showMessage(game.getBlackPlayer() + bundle.getString(S_TURN));
        } else {
            Set<Circle> circles = moveOptions.keySet();
            circles.forEach((circle) -> {
                circle.getStyleClass().add(STROKE_LIGHT_BLUE);
                circle.setCursor(Cursor.HAND);
                circle.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        showPossibleMoves);
            });
        }
    }

    private void removeStrokeCursorAndEventHandlerOnCircle(Circle circle) {
        circle.getStyleClass().remove(STROKE_LIGHT_BLUE);
        circle.setCursor(Cursor.DEFAULT);
        circle.removeEventHandler(MouseEvent.MOUSE_CLICKED,
                showPossibleMoves);
    }

    private void removeBorderCursorAndEventHandlerOnPane(Pane pane) {
        pane.getStyleClass().remove(BORDER_LIGHT_BLUE);
        pane.setCursor(Cursor.DEFAULT);
        pane.removeEventHandler(MouseEvent.MOUSE_CLICKED, moveCircle);
    }

    private void removeStrokeCursorAndEventHandlerOnChips() {
        game.getBoard().getFields().forEach((field) -> {
            field.getChips()
                    .stream()
                    .map(chip -> chip.getCircle())
                    .forEach(circle -> {
                        removeStrokeCursorAndEventHandlerOnCircle(circle);
                    });
        });
    }

    private void removeBorderCursorAndEventHandlerOnFields() {
        game.getBoard().getFields()
                .stream()
                .map(field -> field.getContainer())
                .forEach(pane -> {
                    removeBorderCursorAndEventHandlerOnPane(pane);
                });

        Pane whiteEndPane = game.getBoard().getWhiteEndField().getContainer();
        Pane blackEndPane = game.getBoard().getBlackEndField().getContainer();

        removeBorderCursorAndEventHandlerOnPane(whiteEndPane);
        removeBorderCursorAndEventHandlerOnPane(blackEndPane);
    }

    private void removeStrokeOnChips() {
        game.getBoard().getFields().forEach((field) -> {
            field.getChips()
                    .stream()
                    .map((chip) -> chip.getCircle())
                    .forEach((circle) -> {
                        circle.getStyleClass().remove(STROKE_LIGHT_BLUE);
                    });
        });
    }

    private void addMoveCircleEventHandlerToPane(Pane pane) {
        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, moveCircle);
    }

    private void expelOpponentChip(Field fromField) {
        Optional<Chip> opponentChip
                = fromField.containsOneBlackChip();
        if (opponentChip.isPresent()) {
            fromField.removeChip(opponentChip.get());

            Field centerField = game.getBoard().getBlackCenterField();
            centerField.addChip(opponentChip.get());
        }
    }

    private List<Integer> getUsedDiceValues(int playedValue) {
        List<Integer> diceValues = gameEngine.fetchDiceValues();
        Map<Integer, List<Integer>> diceCombinations = new HashMap<>();
        diceCombinations
                = gameEngine.fetchDiceCombinations(diceCombinations,
                        diceValues, 0);

        Optional<Integer> playedValueKey = diceCombinations.keySet()
                .stream()
                .filter(key -> key >= playedValue)
                .sorted((val1, val2) -> val1.compareTo(val2))
                .findFirst();

        if (playedValueKey.isPresent()) {
            return diceCombinations.get(playedValueKey.get());
        } else {
            return new ArrayList<>();
        }
    }

    private void disableUsedDice(int playedValue) {

        List<Integer> usedDiceValues
                = getUsedDiceValues(playedValue);

        if (!usedDiceValues.isEmpty()) {
            usedDiceValues.forEach((usedDiceValue) -> {
                Optional<Dice> optDice = game.getDice()
                        .stream()
                        .filter(dice
                                -> dice.getVisible()
                        && !dice.getDisabled()
                        && dice.getValue() == usedDiceValue)
                        .findFirst();
                if (optDice.isPresent()) {
                    optDice.get().setDisabled(true);
                }
            });
        }
    }

    private boolean checkAndHandleEndOfGame() {
        Field whiteEndField = game.getBoard().getWhiteEndField();
        Field blackEndField = game.getBoard().getBlackEndField();

        if (whiteEndField.getChips().size()
                == Game.NUMBER_OF_CHIPS_PER_PLAYER) {
            btnRollDice.setDisable(true);
            showMessage(bundle.getString(YOU_WIN));
            enableReplay();
            playerAppController.getTabChat().setDisable(true);
            return true;
        } else if (blackEndField.getChips().size()
                == Game.NUMBER_OF_CHIPS_PER_PLAYER) {
            btnRollDice.setDisable(true);
            showMessage(game.getBlackPlayer() + " " + bundle.getString(WINS));
            playerAppController.sendMessageToServer(new EndOfGameSAH());
            enableReplay();
            playerAppController.getTabChat().setDisable(true);
            return true;
        }

        return false;
    }

    private void showEndOfGameAlert() {
        Field whiteEndField = game.getBoard().getWhiteEndField();
        Field blackEndField = game.getBoard().getBlackEndField();

        if (whiteEndField.getChips().size()
                == Game.NUMBER_OF_CHIPS_PER_PLAYER) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(END_OF_GAME));
            alert.setHeaderText(bundle.getString(YOU_WIN_THE_GAME));
            alert.setContentText(bundle.getString(YOU_CAN_REPLAY_GAME));
            alert.show();
        } else if (blackEndField.getChips().size()
                == Game.NUMBER_OF_CHIPS_PER_PLAYER) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(END_OF_GAME));
            alert.setHeaderText(game.getBlackPlayer() + " "
                    + bundle.getString(WINS_THE_GAME));
            alert.setContentText(bundle.getString(YOU_CAN_REPLAY_GAME));
            alert.show();
        }
    }

    private void resetDiceLabels() {
        lblDice1.setText("");
        lblDice2.setText("");
        lblDice3.setText("");
        lblDice4.setText("");

        lblDice1.setDisable(false);
        lblDice2.setDisable(false);
        lblDice3.setDisable(false);
        lblDice4.setDisable(false);

        lblDice1.setVisible(false);
        lblDice2.setVisible(false);
        lblDice3.setVisible(false);
        lblDice4.setVisible(false);
    }

    private void updateBoard(Board board) {
        whiteChipCounter = 0;
        blackChipCounter = 0;

        game.getBoard().clearChipsOnAllFields();

        // fields
        board.getFields().forEach(updatedField -> {
            if (!updatedField.getChips().isEmpty()
                    && updatedField.getLastChip().isPresent()) {
                if (updatedField.getLastChip().get().getColor().equals(
                        Color.WHITE)) {
                    generateChipsInField(
                            game.getBoard().getFieldByNumber(
                                    updatedField.getNumber()).get(),
                            updatedField.getChips().size(),
                            updatedField.getLastChip().get().getColor(),
                            whiteChipCounter);

                } else {
                    generateChipsInField(game.getBoard().getFieldByNumber(
                            updatedField.getNumber()).get(),
                            updatedField.getChips().size(),
                            updatedField.getLastChip().get().getColor(),
                            blackChipCounter);
                }
            }
        });

        // white center field
        Field updatedWhiteCenterField = board.getWhiteCenterField();
        if (!updatedWhiteCenterField.getChips().isEmpty()) {
            generateChipsInField(
                    game.getBoard().getWhiteCenterField(),
                    updatedWhiteCenterField.getChips().size(),
                    updatedWhiteCenterField.getLastChip().get().getColor(),
                    whiteChipCounter);
        }

        // black center field
        Field updatedBlackCenterField = board.getBlackCenterField();
        if (!updatedBlackCenterField.getChips().isEmpty()) {
            generateChipsInField(
                    game.getBoard().getBlackCenterField(),
                    updatedBlackCenterField.getChips().size(),
                    updatedBlackCenterField.getLastChip().get().getColor(),
                    blackChipCounter);
        }

        // white end field
        Field updatedWhiteEndField = board.getWhiteEndField();
        if (!updatedWhiteEndField.getChips().isEmpty()) {
            generateChipsInField(
                    game.getBoard().getWhiteEndField(),
                    updatedWhiteEndField.getChips().size(),
                    updatedWhiteEndField.getLastChip().get().getColor(),
                    whiteChipCounter);
        }

        // black end field
        Field updatedBlackEndField = board.getBlackEndField();
        if (!updatedBlackEndField.getChips().isEmpty()) {
            generateChipsInField(
                    game.getBoard().getBlackEndField(),
                    updatedBlackEndField.getChips().size(),
                    updatedBlackEndField.getLastChip().get().getColor(),
                    blackChipCounter);
        }
    }

    private void updateDice(List<Dice> dice) {
        for (int i = 0; i < dice.size(); i++) {
            Dice ud = dice.get(i);
            Dice d = game.getDice().get(i);

            d.setValue(ud.getValue());
            d.setDisabled(ud.getDisabled());
            d.setVisible(ud.getVisible());
        }
    }

    private void updateRolledLabels(Boolean started) {
        if ((!started && game.getDice().get(1).getVisible())
                || (!game.getStarted() && started
                && game.getDice().get(1).getVisible())) {
            lblOpponentRolled.setText(game.getBlackPlayer() + " "
                    + bundle.getString(ROLLED));
        } else if (game.getStarted()) {
            lblPlayerRolled.setText("");
            lblOpponentRolled.setText("");
        }
    }

    private void updateTurn(Player turn) {
        if (!game.getStarted()
                && game.getDice().get(0).isVisibleAndDisabled()
                && game.getDice().get(1).isVisibleAndDisabled()) {
            btnRollDice.setDisable(false);
            showMessage(bundle.getString(ROLL_AGAIN));
        }
        if (game.getStarted()) {
            game.setTurn(turn);
            if (game.getTurn().equals(playerAppController.player)) {
                showMessage(bundle.getString(YOUR_TURN));
                if (lblPlayerRolled.getText().isEmpty()) {
                    btnRollDice.setDisable(false);
                } else {
                    handleChips();
                }
            } else {
                showMessage(game.getBlackPlayer() + bundle.getString(S_TURN));
                btnRollDice.setDisable(true);
            }
        }
    }

    private void disableReplay() {
        btnReplay.setDisable(true);
        lblReplaySpeed.setDisable(true);
        rb1x.setDisable(true);
        rb2x.setDisable(true);
    }

    // event handlers
    EventHandler showPossibleMoves = (EventHandler) (Event event) -> {
        removeStrokeOnChips();
        removeBorderCursorAndEventHandlerOnFields();

        Circle circle = (Circle) event.getSource();
        circle.getStyleClass().add(STROKE_LIGHT_BLUE);

        circleToMove = circle;
        Set<Pane> possibleMoves = gameEngine.fetchWhitePlayerMoveOptions()
                .get(circle);

        if (!possibleMoves.isEmpty()) {
            possibleMoves.forEach(pane -> {
                pane.getStyleClass().add(BORDER_LIGHT_BLUE);
                pane.setCursor(Cursor.HAND);
                addMoveCircleEventHandlerToPane(pane);
            });
        }
    };

    EventHandler moveCircle = (EventHandler) (Event event) -> {
        Pane fromPane = (Pane) circleToMove.getParent();
        Pane toPane = (Pane) event.getSource();

        Optional<Field> fromField = game.getBoard().getFieldFromPane(fromPane);
        Optional<Field> toField = game.getBoard().getFieldFromPane(toPane);
        Optional<Chip> chipToMove = fromField.get().getLastChip();

        expelOpponentChip(toField.get());

        fromField.get().removeChip(chipToMove.get());
        toField.get().addChip(chipToMove.get());

        removeStrokeCursorAndEventHandlerOnCircle(circleToMove);
        removeStrokeCursorAndEventHandlerOnChips();
        removeBorderCursorAndEventHandlerOnFields();

        disableUsedDice(abs(
                toField.get().getNumber() - fromField.get().getNumber()));

        DOMUtils.addGameState(game);

        if (!checkAndHandleEndOfGame()) {
            handleChips();
            playerAppController.sendMessageToServer(new GameSAH(game));
        } else {
            DOMUtils.saveGameStates();
            playerAppController.sendMessageToServer(new GameSAH(game));
            showEndOfGameAlert();
        }
    };

}
