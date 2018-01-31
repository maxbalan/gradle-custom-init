package custom.init.plugin.core.internal

/**
 * Created on 19/01/18.
 *
 * <p>
 *      Abstract implementation of the basic functionality for initializing template projects. Any other builder should
 *      extend it in order to be taken into account by the plugin
 * </p>
 * @author Maxim Balan
 */
abstract class ProjectBuilder implements JavaProject {

    protected LibraryVersionProperties versionProperties
    protected String projectTarget
    protected Map bindings
    protected TemplateProcessor templateProcessor


    ProjectBuilder(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor
        this.bindings = new HashMap()
    }

    def init(String projectTarget, LibraryVersionProperties versionProperties) {
        this.projectTarget = projectTarget
        this.versionProperties = versionProperties
        this.bindings.putAll(getBindings())
        this
    }

    def setBinding(String key, String value) {
        this.bindings.put(key, value)
        this
    }

    abstract void execute()

    void copyTemplates(String templateSource, String targetDir, String readPathAfterIndex, boolean removeTemplateTag = true) {
        def templatesList = FileResolver.readTemplates(templateSource)

        templatesList.each {file ->
            def filePath = file.path
            filePath = filePath.substring(filePath.lastIndexOf(readPathAfterIndex)+readPathAfterIndex.length(), filePath.length())

            if (removeTemplateTag && filePath.endsWith('.template'))
                filePath = filePath.substring(0, filePath.lastIndexOf('.template'))

            def processed

            try {
                processed = templateProcessor.processWithReplace(file, bindings)
            } catch (GroovyRuntimeException e) {
                println "WARNING: $e"
                processed = file.content
            }

            def destFile = new File("${targetDir}/${filePath}")

            println "copy file ${destFile.path}"

            makeDirs(destFile.getParentFile().path)
            copyFile(destFile, processed)
        }
    }

    void copyFile(File destFile, String content) {
        destFile << content
    }

    void makeDirs(String dirPath) {
        def src = new File("${dirPath}")

        if (src.isDirectory()) return

        if (!src.mkdirs()) throw new CustomInitException(
                String.format("Failed to create required source directories [ %s ]", dirPath))
    }

    // not sure yet if its a good idea to add ignored files when building the template
    // leaving it for now until I have some feedback
    def getSkipProcessFiles() {
        ['gradlew', 'gradlew.bat']
    }

    private def getBindings() {
        def properties = this.versionProperties.getVersionProperties()

        [
                plugin           : [],
                dropwizardVersion: "${properties.getProperty("dropwizard")}",
                testFramework    : "junit",
                junitVersion     : "${properties.getProperty("junit")}",
                spockVersion     : "${properties.getProperty("spock")}",
                groovyVersion    : "${properties.getProperty("groovy")}"
        ]
    }

    private void checkFileExists(File file, String fileName) {
        if (file.exists())
            throw new CustomInitException(
                    String.format("File [ %s ] already exists, cannot continue the build", fileName))
    }

}
