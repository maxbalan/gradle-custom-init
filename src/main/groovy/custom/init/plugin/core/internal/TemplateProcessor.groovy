package custom.init.plugin.core.internal

import groovy.text.SimpleTemplateEngine

/**
 * Created on 18/01/18.
 *
 * <p>
 *      A wrapper around {@link SimpleTemplateEngine} to process template files
 * </p>
 * @author Maxim Balan
 */
class TemplateProcessor {

    SimpleTemplateEngine engine

    TemplateProcessor() {
        this.engine = new SimpleTemplateEngine()
    }

    String process(FileResolver file, Map bindings) {
        engine.setVerbose(true)
        def template = engine.createTemplate(new InputStreamReader(file.getInputStream()))
        def processed = template.make(bindings)

        processed.toString()
    }

    String processByLine(FileResolver file, Map bindings) {

        file.getInputStream().eachLine {

        }
    }

}
