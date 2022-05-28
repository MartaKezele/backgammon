package mk.bg.utilities;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 *
 * @author Marta
 */
public class ResourceUtils {

    // private members
    private static final String SRC = ".\\src";

    // private constructors
    private ResourceUtils() {
    }

    // public methods
    public static Optional<URL> getResource(String packageName, String fileName)
            throws IOException {

        // join package name and file name to make a relative path
        // example: 
        // packageName: com.company 
        // fileName: filename.ext 
        // -> com/company/filename.ext
        String resourcePath = packageName.replace('.', '\\') + "\\" + fileName;

        Optional<Path> resource = Files.walk(Paths.get(SRC))
                .filter(r -> r.toString().length() - resourcePath.length() > 0
                && r.toString().substring(r.toString().length()
                        - resourcePath.length()).equals(resourcePath))
                .findAny();

        if (resource.isPresent()) {
            return Optional.of(resource.get().toUri().toURL());
        }

        return Optional.empty();
    }

}
