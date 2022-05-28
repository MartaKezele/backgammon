package mk.bg.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mk.bg.game.Board;
import mk.bg.game.Chip;
import mk.bg.game.Dice;
import mk.bg.game.Field;
import mk.bg.game.Game;
import mk.bg.game.Player;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author Marta
 */
public class DOMUtils {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            DOMUtils.class.getName());

    private static final String GAME_STATES_FILENAME = "game_states.xml";
    private static final String GAME_STATES = "gameStates";
    private static final String GAME_STATE = "gameState";
    private static final String ALL_DICE = "allDice";
    private static final String DICE = "dice";
    private static final String VISIBLE = "visible";
    private static final String DISABLED = "disabled";
    private static final String VALUE = "value";
    private static final String FIELDS = "fields";
    private static final String FIELD = "field";
    private static final String WHITE_CENTER_FIELD = "whiteCenterField";
    private static final String BLACK_CENTER_FIELD = "blackCenterField";
    private static final String WHITE_END_FIELD = "whiteEndField";
    private static final String BLACK_END_FIELD = "blackEndField";
    private static final String ID = "id";
    private static final String CHIPS = "chips";
    private static final String CHIP = "chip";
    private static final String COLOR = "color";
    private static final String STARTED = "started";
    private static final String WHITE_PLAYER = "whitePlayer";
    private static final String BLACK_PLAYER = "blackPlayer";
    private static final String NICKNAME = "nickname";
    private static final String GAME_TEMP_FILENAME = "gametemp.ser";
    private static final List<Game> gameStates = new ArrayList();

    // private constructors
    private DOMUtils() {

    }

    // public methods
    public static void addGameState(Game game) {
        try (ObjectOutputStream oos
                = new ObjectOutputStream(
                        new FileOutputStream(GAME_TEMP_FILENAME))) {
            oos.writeObject(game);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try (ObjectInputStream ois
                = new ObjectInputStream(
                        new FileInputStream(GAME_TEMP_FILENAME))) {
            Game newGame = (Game) ois.readObject();
            gameStates.add(newGame);
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public static void resetGameStates(){
        gameStates.clear();
    }

    public static void saveGameStates() {
        try {
            Document document = createDocument(GAME_STATES);
            gameStates.forEach(game
                    -> document
                            .getDocumentElement()
                            .appendChild(createGameElement(game, document)));
            saveDocument(document, GAME_STATES_FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public static List<Game> loadGameStates() {
        List<Game> loadedGameStates = new ArrayList<>();
        try {
            Document document = createDocument(new File(GAME_STATES_FILENAME));
            NodeList nodes = document.getElementsByTagName(GAME_STATE);
            for (int i = 0; i < nodes.getLength(); i++) {
                loadedGameStates.add(processGameStateNode(
                        (Element) nodes.item(i)));
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return loadedGameStates;
    }

    // private methods
    private static Document createDocument(String root)
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation domImplementation = builder.getDOMImplementation();
        return domImplementation.createDocument(null, root, null);
    }

    private static Document createDocument(File file)
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        return document;
    }

    private static Element createGameElement(Game game, Document document) {
        Element element = document.createElement(GAME_STATE);
        element.setAttributeNode(createAttribute(document, STARTED,
                game.getStarted().toString()));
        element.appendChild(createPlayerElement(WHITE_PLAYER,
                game.getWhitePlayer(), document));
        element.appendChild(createPlayerElement(BLACK_PLAYER,
                game.getBlackPlayer(), document));
        element.appendChild(createAllDiceElement(game.getDice(), document));
        element.appendChild(createFieldsElement(game.getBoard(), document));
        return element;
    }

    private static Element createPlayerElement(String elementName, Player player,
            Document document) {
        Element element = document.createElement(elementName);
        element.setAttributeNode(createAttribute(document, ID,
                String.valueOf(player.getId())));
        element.setAttributeNode(createAttribute(document, NICKNAME,
                player.getNickname()));

        return element;
    }

    private static Element createAllDiceElement(List<Dice> dice,
            Document document) {
        Element element = document.createElement(ALL_DICE);
        dice.forEach(d -> element.appendChild(createDiceElement(d, document)));
        return element;
    }

    private static Element createDiceElement(Dice dice, Document document) {
        Element element = document.createElement(DICE);
        element.setAttributeNode(createAttribute(document, VISIBLE,
                dice.getVisible().toString()));
        element.setAttributeNode(createAttribute(document, DISABLED,
                dice.getDisabled().toString()));
        element.setAttributeNode(createAttribute(document, VALUE,
                String.valueOf(dice.getValue())));
        return element;
    }

    private static Element createFieldsElement(Board board, Document document) {
        Element element = document.createElement(FIELDS);
        element.appendChild(createFieldElement(WHITE_CENTER_FIELD,
                board.getWhiteCenterField(),
                document));
        element.appendChild(createFieldElement(BLACK_CENTER_FIELD,
                board.getBlackCenterField(),
                document));
        element.appendChild(createFieldElement(WHITE_END_FIELD,
                board.getWhiteEndField(),
                document));
        element.appendChild(createFieldElement(BLACK_END_FIELD,
                board.getBlackEndField(),
                document));
        board.getFields().forEach(f -> element.appendChild(createFieldElement(
                FIELD,
                f,
                document)));
        return element;
    }

    private static Element createFieldElement(String elementName, Field field,
            Document document) {
        Element element = document.createElement(elementName);
        element.setAttributeNode(createAttribute(document, ID,
                String.valueOf(field.getNumber())));
        element.appendChild(createChipsElement(field.getChips(), document));
        return element;
    }

    private static Element createChipsElement(List<Chip> chips,
            Document document) {
        Element element = document.createElement(CHIPS);
        chips.forEach(chip -> element.appendChild(createChipElement(
                chip,
                document)));
        return element;
    }

    private static Element createChipElement(Chip chip, Document document) {
        Element element = document.createElement(CHIP);
        element.setAttributeNode(createAttribute(document, ID,
                String.valueOf(chip.getNumber())));
        element.setAttributeNode(createAttribute(document, COLOR,
                chip.getColor().toString()));
        return element;
    }

    private static Attr createAttribute(Document document, String name,
            String value) {
        Attr attr = document.createAttribute(name);
        attr.setValue(value);
        return attr;
    }

    private static Node createElement(Document document, String tagName,
            String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private static void saveDocument(Document document, String filename)
            throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        // format docuemnt with indents
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // set size of indent to 4
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                "4");
        transformer.transform(new DOMSource(document),
                new StreamResult(new File(filename)));
    }

    private static Game processGameStateNode(Element element) {
        return new Game(
                processPlayerNode((Element) element
                        .getElementsByTagName(WHITE_PLAYER).item(0)),
                processPlayerNode((Element) element
                        .getElementsByTagName(BLACK_PLAYER).item(0)),
                processFieldsNode(
                        (Element) element
                                .getElementsByTagName(FIELDS).item(0)),
                processAllDiceNode(
                        (Element) element
                                .getElementsByTagName(ALL_DICE).item(0)),
                Boolean.valueOf(element.getAttribute(STARTED)));
    }

    private static Player processPlayerNode(Element element) {
        return new Player(
                Integer.valueOf(element.getAttribute(ID)),
                element.getAttribute(NICKNAME));
    }

    private static Board processFieldsNode(Element element) {
        List<Field> fields = new ArrayList<>();

        NodeList nodes = element.getElementsByTagName(FIELD);
        for (int i = 0; i < nodes.getLength(); i++) {
            fields.add(processFieldNode((Element) nodes.item(i)));
        }
        return new Board(
                fields,
                processFieldNode((Element) element
                        .getElementsByTagName(WHITE_CENTER_FIELD).item(0)),
                processFieldNode((Element) element
                        .getElementsByTagName(BLACK_CENTER_FIELD).item(0)),
                processFieldNode((Element) element.
                        getElementsByTagName(WHITE_END_FIELD).item(0)),
                processFieldNode((Element) element
                        .getElementsByTagName(BLACK_END_FIELD).item(0))
        );
    }

    private static Field processFieldNode(Element element) {
        return new Field(
                Integer.valueOf(element.getAttribute(ID)),
                processChipsNode((Element) element
                        .getElementsByTagName(CHIPS).item(0))
        );
    }

    private static List<Chip> processChipsNode(Element element) {
        List<Chip> chips = new ArrayList<>();

        NodeList nodes = element.getElementsByTagName(CHIP);
        for (int i = 0; i < nodes.getLength(); i++) {
            chips.add(processChipNode((Element) nodes.item(i)));
        }

        return chips;
    }

    private static Chip processChipNode(Element element) {
        return new Chip(
                Integer.valueOf(element.getAttribute(ID)),
                Color.valueOf(element.getAttribute(COLOR)));
    }

    private static List<Dice> processAllDiceNode(Element element) {
        List<Dice> dice = new ArrayList<>();

        NodeList nodes = element.getElementsByTagName(DICE);
        for (int i = 0; i < nodes.getLength(); i++) {
            dice.add(processDiceNode((Element) nodes.item(i)));
        }

        return dice;
    }

    private static Dice processDiceNode(Element element) {
        return new Dice(
                Integer.valueOf(element.getAttribute(VALUE)),
                Boolean.valueOf(element.getAttribute(DISABLED)),
                Boolean.valueOf(element.getAttribute(VISIBLE)));
    }

}
