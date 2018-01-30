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

    private static String PROJECT_TEMPLATE_SOURCE = "/template/dropwizard/project"
    private static String SCRIPT_TEMPLATE_SOURCE = "/template/dropwizard/script"

    BasicJavaDropwizardBuilder(TemplateProcessor templateProcessor) {
        super(templateProcessor)
    }

    @Override
    void execute() {
        bindings.put("plugin", ["java"])

        javaProjectInit(projectTarget)
        def targetDir = getPathWithProjectStructure()

        copyTemplates(PROJECT_TEMPLATE_SOURCE, targetDir, "/project/")
        copyTemplates(SCRIPT_TEMPLATE_SOURCE, projectTarget, "/script/")
    }

    private String getPathWithProjectStructure() {
        String structure = bindings.get("projectStructure")

        if (Utils.checkNotEmpty(structure))
            "${projectTarget}${getProjectSrcJava()}/${structure.replace(".", "/")}"
        else
            "${projectTarget}${getProjectSrcJava()}"
    }

}
