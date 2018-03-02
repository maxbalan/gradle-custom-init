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

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("${rPath}**")
        def filePath = []

        resources.each { Resource res ->
            if (res.filename.endsWith(TEMPLATE)) {
                filePath.add("/${res.description.substring(res.description.lastIndexOf(resourcePath), res.description.length()-1)}")
            }
        }

        filePath
    }

}