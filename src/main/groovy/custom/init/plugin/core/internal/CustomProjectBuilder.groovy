package custom.init.plugin.core.internal

import groovy.io.FileType
/**
 * Created on 22/01/18.
 * <p>
 *      Builds a custom project defined by the user with parameter when executing gradle script. For more details check
 *      readme document
 * </p>
 * @author Maxim Balan
 */
class CustomProjectBuilder extends ProjectBuilder {

    CustomProjectBuilder(TemplateProcessor templateProcessor) {
        super(templateProcessor)
    }

    @Override
    void execute() {
        def templateSource = bindings.get("customTemplate")
        resolveTemplateSource(templateSource)
        readProperties()

        try {
            copyProject()
        } finally {
            cleanup("${projectTarget}/temp")
        }
    }

    void readProperties() {
        def templateSourceDir = bindings['customSourceTarget']
        def propertiesPath = "${projectTarget}/temp/custom-template/${templateSourceDir}/script/CustomInit.properties"
        def propertiesResolver = new FileResolver(new File(propertiesPath))

        versionProperties = new LibraryVersionProperties(propertiesResolver)
        bindings.putAll(parseCustomProperties(versionProperties))
    }

    private void copyProject() {
        def fileList = readProject()

        def extractSourcePath = { String path ->
            path = path.replace("\\", "/")
            path.substring(path.indexOf("/template/") + 10, path.length())
        }

        fileList.each {resolver ->
            def sourcePath = resolver.path
            def path = extractSourcePath sourcePath
            def destPath = "${projectTarget}/${path}"
            def destFile = new File(destPath)
            def skipFiles = getSkipProcessFiles()

            makeDirs(destFile.getParentFile().path)

            if (skipFiles.contains(destFile.getName()))
                return

            def content = templateProcessor.process(resolver, bindings)

            if (!destFile.exists())
                copyFile(destFile, content)
        }

    }

    private List<FileResolver> readProject() {
        def projectFiles = []
        def templateSourceDir = bindings['customSourceTarget']

        def templateDir = new File("${projectTarget}/temp/custom-template/${templateSourceDir}/template")
        templateDir.eachFileRecurse(FileType.FILES) { file ->
            projectFiles << new FileResolver(file)
        }

        projectFiles
    }

    private def resolveTemplateSource(String source) {
        def extention = source.substring(source.lastIndexOf(".") + 1, source.length())

        println "got extention $extention..."

        switch (extention) {
            case "git": cloneGit(source); break
            default: throw new CustomInitException(String.format("Could not identify the source of the project [ %s ]", source))
        }
    }

    private def cloneGit(String repo) {
        def proc = "git clone ${repo} ${projectTarget}/temp/custom-template".execute()
        proc.inputStream.eachLine { line -> println line }

        //wait for clone command to finish in 30 sec as we don't want it hang forever
        //todo make it configurable
        proc.waitForOrKill(30000)

        if (proc.exitValue() > 0)
            throw new CustomInitException("Could not clone github repo")
    }

    private static void cleanup(String path) {
        new File(path).deleteDir()
    }

    private static Map parseCustomProperties(LibraryVersionProperties customProperties) {
        def map = [:]

        customProperties.versionProperties.keySet().each { key ->
            def value = customProperties.versionProperties.get(key)

            if (value instanceof String) {
                //read arrays from properties file
                if (value.startsWith('[') && value.endsWith(']')) {
                    def v = value.substring(1, value.length()-1)
                    def tokens = v.split(",")
                    map[key] = tokens
                } else
                    map[key] = value
            } else
                map[key] = value

        }

        map
    }

}
