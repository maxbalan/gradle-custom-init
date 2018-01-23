package custom.init.plugin.core.internal

/**
 * Created on 18/01/18.
 *
 * @author Maxim Balan
 */
class Utils {

    static checkNotEmpty(String s) {
        return s != null && s.trim().length() > 0
    }

    static checkEmpty(String s) {
        !checkNotEmpty(s)
    }

}
