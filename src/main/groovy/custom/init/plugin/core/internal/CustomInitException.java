package custom.init.plugin.core.internal;

import org.gradle.api.GradleException;

/**
 * Created on 18/01/18.
 *
 * @author Maxim Balan
 */
public class CustomInitException extends GradleException {
    
    CustomInitException(String message){
        super(message);
    }
    
    CustomInitException(String message, Throwable t){
        super(message, t);
    }
    
}
