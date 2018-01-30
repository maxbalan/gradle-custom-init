package custom.init.plugin.core

import custom.init.plugin.core.internal.CustomInitException
import custom.init.plugin.core.internal.ProjectBuilder
import custom.init.plugin.core.internal.ProjectBuilderRegistry
/**
 * Created on 17/01/18.
 *
 * <p>
 *      An enum which hold all defined project builders types
 * </p>
 * @author Maxim Balan
 */
enum SourceTemplateType {

    JAVA_DROPWIZARD("java-dropwizard", "basic dropwizard project"),
    SCALA_GRADLE_PLAY("scala-gradle-play", "a simple gradle + scala + play project based"),
    CUSTOM("custom", "custom project, user will provide all the templates"),
    UNKNOWN("unknown", "unknown project type")

    private String comment
    private String descriptor

    private SourceTemplateType(def descriptor, def comment) {
        this.comment = comment
        this.descriptor = descriptor
    }

    def getComment() {
        return comment
    }

    String getDescriptor() {
        return descriptor
    }

    private static def getType = { type ->
        def result = UNKNOWN
        values().each {
            if (it.toString().equalsIgnoreCase(type) || it.getDescriptor().equalsIgnoreCase(type))
                result = it
        }
        result
    }

    static boolean contains(String type) {
        if (getType(type) != UNKNOWN) true else false
    }

    static ProjectBuilder getBuilder(ProjectBuilderRegistry  registry, String projectType) {
        def type = getType(projectType)

        switch (type) {
            case JAVA_DROPWIZARD : registry.javaDropwizardBuilder; break
            case SCALA_GRADLE_PLAY : registry.scalaGradlePlayBuilder; break
            case CUSTOM : registry.customProjectBuilder; break
            case UNKNOWN : throw new CustomInitException(String.format("Unknown project type [ %s ]", type)); break
        }
    }
}