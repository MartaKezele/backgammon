package mk.bg.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import mk.bg.constants.HtmlConstants;
import mk.bg.utilities.FileUtils;
import mk.bg.utilities.ReflectionUtils;

/**
 * FXML Controller class
 *
 * @author Marta
 */
public class DocumentationController implements Initializable {

    // private members
    private static final Logger LOGGER = Logger.getLogger(
            DocumentationController.class.getName());

    private static final String DOCUMENTATION = "documentation";
    private static final String DOCUMENTATION_CREATED = "documentation_created";
    private static final String DOCUMENTATION_NOT_CREATED
            = "documentation_not_created";
    private static final String CHECK = "check";
    private static final String ERROR_WHILE_GENERATING_DOCS
            = "error_while_generating_documentation";
    private static final String APP_DOCUMENTATION = "app_documentation";
    private static final String LIST_OF_CLASSES_IN_PACKAGE
            = "list_of_classes_in_package";
    private static final String NAME = "name";

    private static final String SRC = ".\\src";
    private static final String JAVA_EXTENSION = ".java";
    private static final String HTML_EXTENSION = "html";
    private ResourceBundle bundle;
    @FXML
    private Button btnGenerateDocumentation;

    // public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
    }

    // private methods
    @FXML
    private void generateDocumentation(ActionEvent event) {

        File documentationFile;
        try {
            documentationFile = FileUtils.saveFileDialog(
                    btnGenerateDocumentation.getScene().getWindow(),
                    HTML_EXTENSION);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return;
        }

        try (BufferedWriter writer
                = Files.newBufferedWriter(Paths.get(
                        documentationFile.getAbsolutePath()))) {

            StringBuilder classAndMembersInfo = new StringBuilder();
            classAndMembersInfo.append(HtmlConstants.DOCTYPE_TAG)
                    .append(HtmlConstants.HTML_OPEN_TAG)
                    .append(HtmlConstants.HEAD_OPEN_TAG)
                    .append(HtmlConstants.TITLE_OPEN_TAG)
                    .append(bundle.getString(APP_DOCUMENTATION))
                    .append(HtmlConstants.TITLE_CLOSE_TAG)
                    .append(HtmlConstants.HEAD_CLOSE_TAG)
                    .append(HtmlConstants.BODY_OPEN_TAG)
                    .append(HtmlConstants.H1_OPEN_TAG)
                    .append(HtmlConstants.H1_OPEN_TAG)
                    .append(bundle.getString(APP_DOCUMENTATION))
                    .append(HtmlConstants.H1_CLOSE_TAG);

            List<String> packages = Files.walk(Paths.get(SRC))
                    .filter(path -> path.toString().length() > 6)
                    .map(path -> path.toString())
                    .filter(p -> p.lastIndexOf(".") == 0)
                    .collect(Collectors.toList());

            packages.forEach(packageName -> {

                String[] classes = new File(packageName).list();

                boolean anyClasses = Arrays.asList(classes)
                        .stream()
                        .anyMatch(className
                                -> className.endsWith(JAVA_EXTENSION));

                if (anyClasses) {
                    classAndMembersInfo.append(HtmlConstants.H2_OPEN_TAG)
                            .append(bundle.getString(LIST_OF_CLASSES_IN_PACKAGE))
                            .append(packageName.substring(6))
                            .append(HtmlConstants.H2_CLOSE_TAG);

                    for (String className : classes) {

                        if (className.endsWith(JAVA_EXTENSION) == false) {
                            continue;
                        }

                        String path = packageName + "\\" + className;

                        int lastDotIdndex = path.lastIndexOf('.');
                        String fqn = path
                                .replace("\\", ".")
                                .substring(6, lastDotIdndex);

                        classAndMembersInfo
                                .append(HtmlConstants.H3_OPEN_TAG)
                                .append(bundle.getString(NAME))
                                .append(HtmlConstants.H3_CLOSE_TAG)
                                .append(className);

                        try {
                            Class<?> clazz
                                    = Class.forName(fqn);
                            ReflectionUtils.readClassAndMembersInfo(clazz,
                                    classAndMembersInfo);
                        } catch (ClassNotFoundException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }

                        classAndMembersInfo.append(HtmlConstants.HR);
                    }
                }
                classAndMembersInfo.append(HtmlConstants.HR);
            });

            classAndMembersInfo.append(HtmlConstants.BODY_CLOSE_TAG);
            classAndMembersInfo.append(HtmlConstants.HTML_CLOSE_TAG);

            writer.write(classAndMembersInfo.toString());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(DOCUMENTATION));
            alert.setHeaderText(bundle.getString(DOCUMENTATION_CREATED));
            alert.setContentText(bundle.getString(CHECK)
                    + " '" + documentationFile.getName() + "'.");
            alert.show();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString(DOCUMENTATION));
            alert.setHeaderText(bundle.getString(DOCUMENTATION_NOT_CREATED));
            alert.setContentText(bundle.getString(ERROR_WHILE_GENERATING_DOCS));
            alert.show();
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
