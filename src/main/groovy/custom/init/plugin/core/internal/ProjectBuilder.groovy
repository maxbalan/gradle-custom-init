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
    private static String BUILD_GRADLE = "build.gradle"
    private static String SETTINGS_GRADLE = "settings.gradle"

    protected static String BUILD_GRADLE_TEMPLATE_PATH = "/template/script/build.gradle.template"
    protected static String SETTINGS_GRADLE_TEMPLATE_PATH = "/template/script/settings.gradle.template"

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

    void copyGradleFile() {
        def buildFile = readFile(BUILD_GRADLE_TEMPLATE_PATH)
        def settingsFile = readFile(SETTINGS_GRADLE_TEMPLATE_PATH)

        def build = new File("${this.projectTarget}/${BUILD_GRADLE}")
        def settings = new File("${this.projectTarget}/${SETTINGS_GRADLE}")

        checkFileExists(build, BUILD_GRADLE)
        checkFileExists(settings, SETTINGS_GRADLE)

        copyFile(build, this.templateProcessor.process(buildFile, this.bindings))
        copyFile(settings, this.templateProcessor.process(settingsFile, this.bindings))
    }

    FileResolver readFile(String path) {
        new FileResolver(path)
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

    // not sure yest if its a good idea to add ignored files when building the template
    //leaving it for now until I have some feedback
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
