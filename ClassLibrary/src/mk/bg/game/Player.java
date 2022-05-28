package mk.bg.game;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marta
 */
public class Player implements Externalizable {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            Player.class.getName());
    private static final long serialVersionUID = 1L;

    private int id;
    private String nickname;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    // public constructors
    public Player() {
    }

    public Player(Socket socket) {
        nickname = "";
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public Player(int id, Socket socket) {
        this(socket);
        this.id = id;
    }

    public Player(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // public methods
    @Override
    public String toString() {
        return nickname;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.id;
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
        final Player other = (Player) obj;
        return this.id == other.id;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeUTF(nickname);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        id = in.readInt();
        nickname = in.readUTF();
    }

    public void disconnect() {
        try {
            socket.close();
            oos.close();
            ois.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
