package dk.nykredit.api.capabilities;

/**
 * Sorting direction for a given attribute used in conjunction with Sort.
 */
public enum Direction {
    ASC('+'), DESC('-');

    final char sign;

    Direction(char d) {
        sign = d;
    }

    static Direction get(char e) {
        return '-' == e ? DESC : ASC;
    }
}
