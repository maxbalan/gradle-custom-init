package custom.init.plugin.core.internal

import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

/**
 * Created on 29/01/18.
 *
 * @author Maxim Balan
 */
trait ResourceReader {

    private static String TEMPLATE = ".template"

    static def readResourceFilesRecurse(String resourcePath) {
        def rPath = resourcePath
        if (!rPath.endsWith('/'))
            rPath="$rPath/"

        def resP = resourcePath
        if (resourcePath.startsWith("/"))
            resP = resourcePath.substring(1)

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver()
        Resource[] resources = resolver.getResources("${rPath}**")
        def filePaths = []

        resources.each { Resource res ->
            if (res.filename.endsWith(TEMPLATE)) {
                println(">>>>>>>>>>>> ${res.description} <<<<<<<<<<<<<<<<")

                filePaths.add("${res.description.substring(res.description.lastIndexOf(resP), res.description.length()-1)}")
            }
        }

        def results = []

        filePaths.forEach { String path ->
            if (path.startsWith("/"))
                results.add(path)
            else
                results.add("/${path}")
        }

        results
    }

}