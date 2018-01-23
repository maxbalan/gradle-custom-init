package custom.init.plugin.core.internal
/**
 * Created on 18/01/18.
 * <p>
 *      Creates a predefined HelloWorld dropwizard project
 * </p>
 *
 * @author Maxim Balan
 */
class BasicJavaDropwizardBuilder extends ProjectBuilder {

    private static String TEMPLATE = "template"
    private static String APP_YAML = "app.yaml"
    private static String APP = "App.java"
    private static String APP_CONFIG = "AppConfig.java"
    private static String GREETING = "Greeting.java"
    private static String HELLO_WORLD = "HelloWorldResource.java"
    private static String TEMPLATE_SOURCE = "/template/dropwizard"
    private static String CONFIG = "TestConfig.java"

    BasicJavaDropwizardBuilder(TemplateProcessor templateProcessor) {
        super(templateProcessor)
    }

    @Override
    void execute() {
        bindings.put("dropwizard", true)
        bindings.put("plugin", ["java"])

        javaProjectInit(projectTarget)
        copyGradleFile()
        copyConfig()
        copyClasses()
    }

    private void copyConfig() {
        def confPath = "${projectTarget}/conf"
        def fileResolver = readFile("${TEMPLATE_SOURCE}/conf/${APP_YAML}.${TEMPLATE}")
        def processedFile = templateProcessor.process(fileResolver, bindings)

        makeDirs(confPath)
        copyFile(new File("${confPath}/${APP_YAML}"), processedFile)
    }

    private void copyClasses() {
        def sourceDir = getPathWithProjectStructure()

        makeDirs("${sourceDir}/config")
        makeDirs("${sourceDir}/resources")

        def configTemplate = readFile("${TEMPLATE_SOURCE}/config/${CONFIG}.${TEMPLATE}")
        def appTemplate = readFile("${TEMPLATE_SOURCE}/${APP}.${TEMPLATE}")
        def appConfigTemplate = readFile("${TEMPLATE_SOURCE}/${APP_CONFIG}.${TEMPLATE}")
        def resourceGreetingTemplate = readFile("${TEMPLATE_SOURCE}/resources/${GREETING}.${TEMPLATE}")
        def resourceHelloWOrldTemplate = readFile("${TEMPLATE_SOURCE}/resources/${HELLO_WORLD}.${TEMPLATE}")

        copyFile(new File("${sourceDir}/config/${CONFIG}"), templateProcessor.process(configTemplate, bindings))
        copyFile(new File("${sourceDir}/${APP}"), templateProcessor.process(appTemplate, bindings))
        copyFile(new File("${sourceDir}/${APP_CONFIG}"), templateProcessor.process(appConfigTemplate, bindings))
        copyFile(new File("${sourceDir}/resources/${GREETING}"), templateProcessor.process(resourceGreetingTemplate, bindings))
        copyFile(new File("${sourceDir}/resources/${HELLO_WORLD}"), templateProcessor.process(resourceHelloWOrldTemplate, bindings))
    }

    private String getPathWithProjectStructure() {
        String structure = bindings.get("projectStructure")

        if (Utils.checkNotEmpty(structure))
            "${projectTarget}${getProjectSrcJava()}/${structure.replace(".", "/")}"
        else
            "${projectTarget}${getProjectSrcJava()}"
    }

}
