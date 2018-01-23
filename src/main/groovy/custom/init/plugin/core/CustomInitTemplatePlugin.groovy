package custom.init.plugin.core

import org.gradle.api.Plugin
import org.gradle.api.Project

import custom.init.plugin.task.CustomInit
/**
 * Created on 17/01/18.
 *
 * <p>
 *      Plugin initializer
 * </p>
 * @author Maxim Balan
 */
class CustomInitTemplatePlugin implements Plugin<Project> {

    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target object
     */
    @Override
    void apply(Project target) {
        target.tasks.create('custom-init', CustomInit) {
        }
    }
}
