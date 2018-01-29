package custom.init.plugin.core.internal

/**
 * Created on 29/01/18.
 *
 * @author Maxim Balan
 */
trait ResourceReader {

    private static String TEMPLATE = ".template"

    static def readResourceFilesRecurse(String resourcePath) {
        def filePath = []

        getClass().getResourceAsStream(resourcePath).eachLine {
            def rPath = resourcePath

            if (!rPath.endsWith('/'))
                rPath="$rPath/"

            if (it.endsWith(TEMPLATE))
                filePath.add("$rPath$it")
            else
                filePath.addAll(readResourceFilesRecurse("$rPath$it"))
        }

        filePath
    }

}