package dk.nykredit.api.capabilities;

/**
 * API input sanitizer in a rudimental version.
 */
public class Sanitizer {
    private static final String[] SUSPICIOUS_CONTENT = {"\'", "\"", "\\", "%", "\\%", "\\_", "\0", "\b", "\n", "\t", "\r", "\\Z", "?", "#"};

    private Sanitizer(){
        // reduce scope to avoid default construction
    }

    /**
     * A simple sanitizer that needs to be extended and elaborated to cope with injections and
     * other things that pose as threats to the services and the data they contain and maintain.
     *
     * @param input an input string received from a non-trustworthy source (in reality every source)
     * @param allowSpaces should the string be stripped for spaces or allow these to stay
     * @param allowNumbers can the input contain numbers or not
     * @return a sanitized string or an empty string if the sanitation failed for some reason.
     */
    public static String sanitize(String input, boolean allowSpaces, boolean allowNumbers) {
        String result = input;
        if (null == input) {
            return "";
        }
        if (!allowSpaces) {
            result = result.replaceAll(" ", "");
        }
        if (!allowNumbers) {
            result = result.matches(".*\\d.*") ? "" : result;
        }
        for (String s : SUSPICIOUS_CONTENT) {
            if (result.contains(s)) {
                return "";
            }
        }
        return result;
    }
}
