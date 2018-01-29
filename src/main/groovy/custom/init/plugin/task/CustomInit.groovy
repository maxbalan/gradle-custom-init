package custom.init.plugin.task

import org.apache.commons.lang3.StringUtils

import org.gradle.api.DefaultTask
import org.gradle.api.internal.tasks.options.Option
import org.gradle.api.tasks.TaskAction

import custom.init.plugin.core.SourceTemplateType
import custom.init.plugin.core.internal.CustomInitException
import custom.init.plugin.core.internal.FileResolver
import custom.init.plugin.core.internal.LibraryVersionProperties
import custom.init.plugin.core.internal.ProjectBuilderRegistry
import custom.init.plugin.core.internal.Utils
/**
 * Created on 17/01/18.
 *
 * <p>
 *      Plugin task
 * </p>
 *
 * @author Maxim Balan
 */
class CustomInit extends DefaultTask {

    private String projectType
    private String projectName
    private String projectGroup
    private String projectStructure
    private String projectTarget
    private String customTemplate
    private String customTemplateTarget
    private LibraryVersionProperties libraryVersionSource
    private ProjectBuilderRegistry registry

    CustomInit() {
        this.libraryVersionSource = new LibraryVersionProperties(new FileResolver('/template/CustomInit.properties'))
        this.registry = ProjectBuilderRegistry.INSTANCE
        setCurrentFolderAsProjectSource()
    }

    @TaskAction
    def taskAction() {
        if (SourceTemplateType.contains(this.projectType)) {
            initProject()
        } else
             throw new CustomInitException(String.format("Unknown project type [ %s ]", this.projectType))
    }

    def initProject() {
        if (this.libraryVersionSource == null)
            throw new CustomInitException("Unable to locate source library file")

        def builder = SourceTemplateType.getBuilder(this.registry, this.projectType)
        builder.init(this.projectTarget, this.libraryVersionSource)
                .setBinding("group", this.projectGroup)
                .setBinding("projectName", this.projectName)
                .setBinding("projectStructure", this.projectStructure == null ? "" : this.projectStructure)
                .setBinding("packagePath", this.projectStructure == null ? "" : this.projectStructure)
                .setBinding("customTemplate", this.customTemplate == null ? "" : this.customTemplate)
                .setBinding("customSourceTarget", this.customTemplateTarget == null ? "" : this.customTemplateTarget)
                .execute()
    }

    @Option(option = "project-type", description = "Set project type.", order = 0)
    void setType(String type) {
        if (Utils.checkEmpty(type))
            throw new CustomInitException("Cannot resolve [ project-type ] parameter")

        this.projectType = type

        logger.quiet("Setting project type: {}", this.projectType)
    }

    @Option(option = "project-structure", description = "Set project type.", order = 0)
    void setProjectStructure(String structure) {
        if (Utils.checkEmpty(structure))
            throw new CustomInitException("Cannot resolve [ project-type ] parameter")

        this.projectStructure = structure

        logger.quiet("Setting project structure: {}", this.projectStructure)
    }

    @Option(option = "group", description = "Set project group, default will result in project name", order = 0)
    void setCustomProjectGroup(String group) {
        if (Utils.checkEmpty(group))
            throw new CustomInitException("Cannot resolve [ group ] parameter")

        this.projectGroup = group

        logger.quiet("Setting project group: {}", this.projectGroup)
    }

    @Option(option = "custom-template", description = "Set custom project template source", order = 0)
    void setCustomTemplate(String customTemplate) {
        if (StringUtils.isEmpty(customTemplate.trim()))
            throw new CustomInitException("Cannot resolve [ custom-template ] parameter")

        this.customTemplate = customTemplate

        logger.quiet("Setting custom template source: {}", this.customTemplate)
    }

    @Option(option = "custom-template-target", description = "Set custom project template location in the provided [ custom-template ]", order = 0)
    void setCustomTemplateLocation(String customTemplateTarget) {
        if (Utils.checkEmpty(customTemplateTarget))
            throw new CustomInitException("Cannot resolve [ custom-template ] parameter")

        this.customTemplateTarget = customTemplateTarget

        logger.quiet("Setting custom template source target: {}", this.customTemplateTarget)
    }

    @Option(option = "library-version-source", description = "Set an alternative source for library versions ", order = 0)
    void setLibraryVersionSource(String library) {
        if (Utils.checkEmpty(library))
            throw new CustomInitException("Provided file path cannot be Empty")

        def resolver = new FileResolver(new File(library))
        this.libraryVersionSource = new LibraryVersionProperties(resolver)

        logger.quiet("Setting new library source: {}", library)
    }

    @Option(option = "project-target", description = "Set project location directory, if directory does not exists will try to create one", order = 0)
    void setLocation(String projectTarget) {
        if (Utils.checkEmpty(projectTarget))
            throw new CustomInitException("Provided file path cannot be Empty")

        def source = new File(projectTarget)

        if (!source.isDirectory()) {
            if(!source.mkdirs())
                throw new CustomInitException("Unable to create project directory")
        }

        this.projectTarget = projectTarget

        logger.quiet("Setting new project source directory: {}", this.projectTarget)

        setProjectName()
    }

    private void setCurrentFolderAsProjectSource() {
        try {
            this.projectTarget = System.getProperty("user.dir").replace("\\", "/")

            logger.quiet("Setting default project source directory: {}", this.projectTarget)

            setProjectName()
        } catch (Exception e) {
            throw new CustomInitException("Unable to get current source dir", e)
        }
    }

    private void setProjectName() {
        this.projectName = this.getProjectSourceDir().substring(this.getProjectSourceDir().lastIndexOf("/")+1, this.getProjectSourceDir().length())
        logger.quiet("Setting default project name: {}", this.projectName)

        setProjectGroup()
    }

    String getProjectSourceDir() {
        return projectTarget
    }

    private void setProjectGroup() {
        if (this.projectGroup != null && project.hasProperty("group"))
            return

        this.projectGroup = this.projectName
        logger.quiet("Group parameter was not provided, project name will be used instead")
    }

}

