package custom.init.plugin.core.internal

import java.nio.charset.StandardCharsets
/**
 * Created on 18/01/18.
 *
 * <p>
 *      A wrapper around {@link File}
 * </p>
 *
 * @author Maxim Balan
 */
class FileResolver {

    private final String file
    private final String path

    FileResolver(String filePath) {
        if (Utils.checkEmpty(filePath))
            throw new CustomInitException("Provided file path cannot be NULL")

        this.file = getClass().getResourceAsStream(filePath).text
        this.path = filePath
    }

    FileResolver(File file) {
        if (!file.exists())
            throw new CustomInitException(String.format("Could not find a file under path [ %s ]", file.path))

        this.file = file.text
        this.path = file.path
    }

    InputStream getInputStream() {
        new ByteArrayInputStream(this.file.getBytes(StandardCharsets.UTF_8.name()))
    }

    String getPath() {
        return path
    }

}
