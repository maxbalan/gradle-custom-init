package custom.init.plugin.core.internal

/**
 * Created on 18/01/18.
 *
 * <p>
 *      Builder class registry. In order to get any custom builder implementation working it need to be mentioned here
 * </p>
 *
 * @author Maxim Balan
 */
class ProjectBuilderRegistry {

    public static ProjectBuilderRegistry INSTANCE = new ProjectBuilderRegistry()

    private final BasicJavaDropwizardBuilder javaDropwizardBuilder
    private final CustomProjectBuilder customProjectBuilder

    private ProjectBuilderRegistry() {
        def templateProcessor = new TemplateProcessor()
        this.javaDropwizardBuilder = new BasicJavaDropwizardBuilder(templateProcessor)
        this.customProjectBuilder = new CustomProjectBuilder(templateProcessor)
    }

    BasicJavaDropwizardBuilder getJavaDropwizardBuilder() {
        return javaDropwizardBuilder
    }

    CustomProjectBuilder getCustomProjectBuilder() {
        return customProjectBuilder
    }

}
