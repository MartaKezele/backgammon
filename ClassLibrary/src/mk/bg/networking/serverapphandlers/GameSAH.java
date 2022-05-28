package mk.bg.networking.serverapphandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import mk.bg.game.Board;
import mk.bg.game.Chip;
import mk.bg.game.Dice;
import mk.bg.game.Field;
import mk.bg.game.Game;
import mk.bg.game.Player;
import mk.bg.networking.playerapphandlers.GamePAH;

/**
 *
 * @author Marta
 */
public class GameSAH extends ServerAppHandler {

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
            GameSAH.class.getName());

    // public constructors
    public GameSAH() {
    }

    public GameSAH(Game message) {
        super(message);
    }

    // public methods
    @Override
    public void handleOnServerApp(Player player, ServerAppMembers members) {
        Game game = (Game) super.message;
        Optional<Player> optOpponentPlayer = super.getOpponentPlayerFromMap(
                members.getPairedPlayers(), player);
        if (optOpponentPlayer.isPresent()) {
            Player opponentPlayer = optOpponentPlayer.get();
            Game flippedGame = flipGame(game);
            sendMessageToPlayer(opponentPlayer, new GamePAH(flippedGame));
            LOGGER.info("Received game message from player " + player.getId()
                    + ", flipped it and forwarded to player "
                    + opponentPlayer.getId() + ".");
        }

//        if (isEndOfGame(game)) {
//            Optional<Map.Entry<Player, Player>> optEntry = getPlayerEntryFromMap(serverAppMembers.pairedPlayers, player);
//            if (optEntry.isPresent()) {
//                Map.Entry<Player, Player> entry = optEntry.get();
//                Player opponentPlayer = entry.getKey().equals(player) ? entry.getValue() : entry.getKey();
//                serverAppMembers.pairedPlayers.entrySet().remove(entry);
//                serverAppMembers.unpairedPlayers.add(player);
//                serverAppMembers.unpairedPlayers.add(opponentPlayer);
//                serverAppMembers.connectedPlayers.add(player);
//                serverAppMembers.connectedPlayers.add(opponentPlayer);
//            }
//        }
    }

    // private methods
    private Game flipGame(Game game) {
        Game flippedGame = new Game(
                game.getBlackPlayer(),
                game.getWhitePlayer(),
                flipBoard(game),
                flipDice(game));
        flippedGame.setStarted(game.getStarted());
        if (game.getStarted()) {
            flippedGame.setTurn(game.getTurn());
        }
        return flippedGame;
    }

    private Board flipBoard(Game game) {
        // flip center and end fields
        List<Chip> whiteCenterFieldChips = new ArrayList<>(
                game.getBoard().getWhiteCenterField().getChips());
        List<Chip> blackCenterFieldChips = new ArrayList<>(
                game.getBoard().getBlackCenterField().getChips());

        whiteCenterFieldChips.forEach((chip) -> {
            chip.setColor(Color.BLACK);
        });

        blackCenterFieldChips.forEach((chip) -> {
            chip.setColor(Color.WHITE);
        });

        game.getBoard().getWhiteCenterField().getChips().clear();
        game.getBoard().getBlackCenterField().getChips().clear();

        game.getBoard().getWhiteCenterField().getChips().addAll(blackCenterFieldChips);
        game.getBoard().getBlackCenterField().getChips().addAll(whiteCenterFieldChips);

        List<Chip> whiteEndFieldChips = new ArrayList<>(
                game.getBoard().getWhiteEndField().getChips());
        List<Chip> blackEndFieldChips = new ArrayList<>(
                game.getBoard().getBlackEndField().getChips());

        whiteEndFieldChips.forEach((chip) -> {
            chip.setColor(Color.BLACK);
        });

        blackEndFieldChips.forEach((chip) -> {
            chip.setColor(Color.WHITE);
        });

        game.getBoard().getWhiteEndField().getChips().clear();
        game.getBoard().getBlackEndField().getChips().clear();

        game.getBoard().getWhiteEndField().getChips().addAll(blackEndFieldChips);
        game.getBoard().getBlackEndField().getChips().addAll(whiteEndFieldChips);

        // flip other fields
        int j = 23;
        for (int i = 0; i < game.getBoard().getFields().size() / 2; i++) {
            Field field1 = game.getBoard().getFieldByNumber(i).get();
            Field field2 = game.getBoard().getFieldByNumber(j--).get();

            List<Chip> chips1 = new ArrayList<>(field1.getChips());
            List<Chip> chips2 = new ArrayList<>(field2.getChips());

            chips1.forEach(chip -> chip.setColor(getOppositeColor(chip.getColor())));
            chips2.forEach(chip -> chip.setColor(getOppositeColor(chip.getColor())));

            field1.getChips().clear();
            field2.getChips().clear();

            field1.getChips().addAll(chips2);
            field2.getChips().addAll(chips1);
        }

        return game.getBoard();
    }

    private List<Dice> flipDice(Game game) {
        List<Dice> flippedDice = new ArrayList<>();
        flippedDice.add(game.getDice().get(1));
        flippedDice.add(game.getDice().get(0));
        flippedDice.add(game.getDice().get(2));
        flippedDice.add(game.getDice().get(3));
        return flippedDice;
    }

    private Color getOppositeColor(Color color) {
        return color.equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private boolean isEndOfGame(Game game) {
        return game.getBoard().getWhiteEndField().getChips().size()
                == Game.NUMBER_OF_CHIPS_PER_PLAYER
                || game.getBoard().getBlackEndField().getChips().size()
                == Game.NUMBER_OF_CHIPS_PER_PLAYER;
    }

}
