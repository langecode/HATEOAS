package dk.nykredit.api.capabilities;

/**
 * signals whether a criteria for a given API capability is used
 * for inclusion or exclusion of a an attribute when filtering the
 * desired response. This is used for dynamic projections.
 */
public enum Inclusion {
    INC('+'), EXC('-');

    final char inclusion;

    Inclusion(char inclusion) {
        this.inclusion = inclusion;
    }

    public static Inclusion get(char c) {
        if('-'==c) return EXC;
        return INC;
    }
}
