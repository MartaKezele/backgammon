package mk.bg.game;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 *
 * @author Marta
 */
public class Game implements Externalizable {

    // public members
    public static final int NUMBER_OF_CHIPS_PER_PLAYER = 4;

    // private members
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    private Player whitePlayer;
    private Player blackPlayer;
    private Board board;
    private List<Dice> dice;
    private Boolean started;
    private Player turn;

    // public constructors
    public Game() {
    }

    public Game(Player whitePlayer,
            Player blackPlayer,
            Board board,
            List<Dice> dice) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = board;
        this.dice = dice;
        this.started = false;
    }

    public Game(Player whitePlayer,
            Player blackPlayer,
            Board board,
            List<Dice> dice,
            Boolean started) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.board = board;
        this.dice = dice;
        this.started = started;
    }

    // getters
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public List<Dice> getDice() {
        return dice;
    }

    public Boolean getStarted() {
        return started;
    }

    public Player getTurn() {
        return turn;
    }

    // setters
    public void setStarted(Boolean started) {
        this.started = started;
    }

    public void setTurn(Player turn) {
        this.turn = turn;
    }

    // public methods
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.whitePlayer);
        hash = 37 * hash + Objects.hashCode(this.blackPlayer);
        hash = 37 * hash + Objects.hashCode(this.board);
        hash = 37 * hash + Objects.hashCode(this.dice);
        hash = 37 * hash + Objects.hashCode(this.started);
        hash = 37 * hash + Objects.hashCode(this.turn);
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
        final Game other = (Game) obj;
        if (!Objects.equals(this.whitePlayer, other.whitePlayer)) {
            return false;
        }
        if (!Objects.equals(this.blackPlayer, other.blackPlayer)) {
            return false;
        }
        if (!Objects.equals(this.board, other.board)) {
            return false;
        }
        if (!Objects.equals(this.dice, other.dice)) {
            return false;
        }
        if (!Objects.equals(this.turn, other.turn)) {
            return false;
        }
        return Objects.equals(this.started, other.started);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(whitePlayer);
        out.writeObject(blackPlayer);
        out.writeObject(board);
        out.writeObject(dice);
        out.writeBoolean(started);
        out.writeObject(turn);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        whitePlayer = (Player) in.readObject();
        blackPlayer = (Player) in.readObject();
        board = (Board) in.readObject();
        dice = (List<Dice>) in.readObject();
        started = in.readBoolean();
        turn = (Player) in.readObject();
    }
}
