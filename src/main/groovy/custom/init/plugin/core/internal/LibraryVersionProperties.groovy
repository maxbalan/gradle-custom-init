package custom.init.plugin.core.internal

/**
 * Created on 18/01/18.
 *
 * <p>
 *      Reads properties file
 * </p>
 *
 * @author Maxim Balan
 */
class LibraryVersionProperties {

    private final Properties properties

    LibraryVersionProperties(FileResolver library) {
        this.properties = new Properties()
        this.properties.load(library.getInputStream())
    }

    Properties getVersionProperties() {
        return properties
    }

}
