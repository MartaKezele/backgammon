package mk.bg.jndi;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

/**
 *
 * @author Marta
 */
public class InitialDirContextCloseable extends InitialDirContext 
        implements AutoCloseable {

    public InitialDirContextCloseable(Hashtable<?, ?> environment) 
            throws NamingException {
        super(environment);
    }

}
