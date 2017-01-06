package dk.nykredit.api.capabilities;

/**
 * signals whether a sorting criteria for a given attribute is sorted
 * ascending or descending of a an attribute.
 */

public enum Direction {
    ASC('+'), DESC('-');

    final char sign;

    Direction(char d) {
        sign = d;
    }

    static Direction get(char e) {
        if('-'==e) return DESC;
        if(' '==e) return ASC;
        return ASC;
    }
}
