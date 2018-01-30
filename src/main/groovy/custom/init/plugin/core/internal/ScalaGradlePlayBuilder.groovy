package custom.init.plugin.core.internal

/**
 * Created on 29/01/18.
 *
 * @author Maxim Balan
 */
class ScalaGradlePlayBuilder extends ProjectBuilder {

    private static String PROJECT_TEMPLATE_SOURCE = "/template/playframework/project"
    private static String SCRIPT_TEMPLATE_SOURCE = "/template/playframework/script"

    ScalaGradlePlayBuilder(TemplateProcessor templateProcessor) {
        super(templateProcessor)
    }

    @Override
    void execute() {
        bindings.put("plugin", ["idea", "play", "java", "scala"])
        bindings.putAll(versionProperties.versionProperties)

//        copyTemplates(PROJECT_TEMPLATE_SOURCE, projectTarget, "/project/")
        copyTemplates(SCRIPT_TEMPLATE_SOURCE, projectTarget, "/script/")
    }

}
