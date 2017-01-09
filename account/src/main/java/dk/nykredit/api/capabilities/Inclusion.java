package dk.nykredit.api.capabilities;

/**
 * Signals inclusion or exclusion of a given criterion used in conjunction with Composition.
 *
 * This is used for dynamic projections.
 */
public enum Inclusion {
    INC('+'), EXC('-');

    final char inclusion;

    Inclusion(char inclusion) {
        this.inclusion = inclusion;
    }

    public static Inclusion get(char c) {
        return '-' == c ? EXC : INC;
    }
}
