package custom.init.plugin.core.internal

/**
 * Created on 22/01/18.
 *
 * <p>
 *      Builds default java project structure
 * </p>
 *
 * @author Maxim Balan
 */
trait JavaProject {

    void javaProjectInit(String sourceDir) {
        def dirPath = "${sourceDir}${getProjectSrcJava()}"
        def src = new File(dirPath)

        if (src.isDirectory()) return

        if (!src.mkdirs()) throw new CustomInitException(String.format("Failed to create required source directories [ %s ]", dirPath))
    }

    String getProjectSrcJava() {
        "/src/main/java"
    }

}
