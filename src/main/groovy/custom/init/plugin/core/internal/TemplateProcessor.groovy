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
        def template = engine.createTemplate(new InputStreamReader(file.getInputStream()))
        def processed = template.make(bindings)

        processed.toString()
    }

    String processWithReplace(FileResolver file, Map bindings) {
        def processingBindings = [:]
        processingBindings.putAll(bindings)

        def (newBindings, content) = findTemplateEngineFailureMarkups(new InputStreamReader(file.inputStream))
        processingBindings.putAll(newBindings)
        file = new FileResolver(content, file.path)

        def markups = findMarkups(new InputStreamReader(file.inputStream))

        markups.each {
            if (!processingBindings.containsKey(it))
                processingBindings.put(it, "\$$it")
        }

        process(file, processingBindings)
    }

    // this is a work around SimpleTemplateEngine from groovy which fails as soon as it gets an unknown property
    // this will extract all the properties that the template engine will encounter and will replace them with the same
    // expression. This should solve the problem for now, the other solution is to write a proper parser that does proper
    // error handling and gives the end user a way to decide what he wants to do.
    private def findMarkups(Reader reader) {
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader)
        }

        def unknownBindings = []
        StringBuilder sb = new StringBuilder()

        boolean writing = false
        int c
        while ((c = reader.read()) != -1) {
            if (c == '$') {
                writing = true
            } else if ((c == ' ' || c == "\'" || c == "\"" || c == "}" || c == ':') && writing) {
                writing = false
                unknownBindings.add(sb.toString())
                sb = new StringBuilder()
            } else if (c == "{" && writing) {
                //skip it
            } else if (writing) {
                sb.append(c as char)
            }
        }

        unknownBindings
    }

    // this is a workaround closure like structure with unexpected chars in the files, yeah groovy template
    // engine is being confused by those and failing the processing. Copy those structure and replace them by a
    // proper markup that will put the expresion back in
    private def findTemplateEngineFailureMarkups(Reader reader) {
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader)
        }

        def newBindings = [:]
        def unexpected = getUnexpectedChars()
        StringBuilder sb = new StringBuilder()
        StringWriter sw = new StringWriter()

        boolean ignoring = false
        boolean remember = false
        int i
        int c
        while ((c = reader.read()) != -1) {
            if (c == '$') { //check beginning of groovy markup
                reader.mark(1)
                c = reader.read()

                if (c == '{') { //check for closure markup
                    reader.mark(1)
                    ignoring = true
                    sb.append('${')
                } else {
                    sw.write('$')
                }

                reader.reset()
            } else if (c == '}' && ignoring) { //check closure expression closing
                sb.append('}')

                if (remember) { //if I have to replace it
                    def  key = "unexpected$i"
                    sw.write("\${$key}")
                    newBindings.put(key, sb.toString())
                    remember=false
                    i++
                } else {
                    sw.write(sb.toString())
                }

                ignoring=false

                sb = new StringBuilder()
            } else if (ignoring && unexpected.contains((c as char) as String)) { // check if closure expression contains unsupported chars
                remember = true
            } else if (ignoring) { //write closure to string builder
                sb.append(c as char)
            } else { // anything else just copy it
                sw.write(c)
            }
        }

        [newBindings, sw.toString()]
    }

    private def getUnexpectedChars() {
        new HashSet<>(['\'', '\"', ':', '\\', '@'])
    }

}
