package dk.nykredit.api.capabilities;

import java.util.Optional;

/**
 * Defines a pagination of responses is obtained by using the Query parameter `elements`.
 *
 * The Query Parameter `elements` signals the initial element and the last element that is desired to be part of the response.
 *
 * <p>
 * The syntax is: {@literal(elements="<startingFrom>|<endingAt>"</code> both inclusive.)}
 * </p>
 *
 * Example:
 * <p>
 * https://banking.services.sample-bank.dk/accounts/1234-56789/transactions?elements="10|30"
 * which returns element 10 as the first entry in the json
 * response and element 30 as the last entry in the response.
 * </p>
 * A maximum element size is defined here max size is 500 elements
 */
public class Element {

    public static final int MAX_ELEMENTS = 500;

    private final int start;
    private final int end;

    private Element(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * @return the starting point for the desired sequence if returned instances
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the last entry in the desired sequence if returned instances
     */
    public int getEnd() {
        return end;
    }

    /**
     * A span of elements.
     * The syntax supported is given by the regexp: <code>"^([0-9]+)?(\\|[0-9]+)?"</code>
     * @param element a string that follows the syntax given by the regexp above
     * @return an element set with a "include from element (start)" to "last included element (end)"
     */
    public static Optional<Element> getElement(String element) {
        if (null == element) {
            return Optional.empty();
        }
        if (!element.matches("^([0-9]+)?(\\|[0-9]+)?")) {
            return Optional.empty();
        }
        String result = Sanitizer.sanitize(element, false, true);
        int pipe = result.indexOf('|');
        if (pipe > 0) {
            try {
                int s = Integer.parseInt(result.substring(0, pipe));
                int e = Integer.parseInt(result.substring(pipe + 1));
                if ((e > s) && (s > 0) && (e-s < MAX_ELEMENTS)) {
                    return Optional.of(new Element(s, e));
                }
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
