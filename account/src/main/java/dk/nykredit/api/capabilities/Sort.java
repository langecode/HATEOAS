package dk.nykredit.api.capabilities;

import java.util.List;
import java.util.Optional;

/**
 * Signals a sorting order by attributes.
 *
 * Implementation of the sorting API capability which follows the
 * structure like {@literal(sort="<attribute>::+/-|<attribute>::+/-|...)}.
 * 
 * The default is {@literal(sort="<attribute>|<attribute>|...)} which means default is ascending,
 * so <code>sort="balance|lastUpdate"</code> would mean ascending in relation
 * to balance and if equal then according to lastUpdate.
 * <p>
 * The <code>sort</code> APi capability is handled by the Sort class and this is a simplistic way to get the
 * QueryParam mapped to a List with a unique representation of the attributes to sort by.
 * The responsibility for having a Map is that every <code>attribute</code> used to specify sorting can
 * only be present once as the same attribute with multiple representations and thus directions does
 * not make sense, the Map allows only one key and having the attribute (stems from the json and thus the representation)
 * as key helps to enforce that.
 * <p>
 * In this case the mapping between the Sort API capability, which is an integral part of the exposure is
 * mapped straight to a corresponding internal model. That is the attribute naming is here expected to be
 * the same for the exposure and representation and onto the model. That will not always be the case and thus
 * the Sort Capability must be extended for these cases and that will enable the a variable mapping from the
 * representation and on to the model.
 * <p>
 * The responsibility for the mapping is chosen to be done in this layer as an extension in order to avoid having the
 * Model "infected" with mapping to and from the representations. Another choice could have been to let the
 * representation(s) do that and that would have concrete model class persistable field knowledge in the representation(s)
 * which is not desirable and thus the choice was to let the Sort class take that responsibility and thus extensions can handle
 * necessary translations or mappings between the exposure and representations from and to the model.
 */
public class Sort {
    private static final String REGEX = "^([a-zA-Z]+[a-zA-Z_0-9]*(::-|::\\+|:: )?)(\\|[a-zA-Z_0-9]+[a-zA-Z_0-9]*(::-|::\\+)?)*";
    private static final CapabilityParser<Sort> PARSER = new CapabilityParser<>(REGEX, Sort::parseToken, Sort::duplicate);

    private String attribute = "";
    private Direction direction = Direction.ASC;

    private Sort(String attribute, Direction direction) {
        this.attribute = attribute;
        this.direction = direction;
    }

    /**
     * the attribute name used in the representation of the object
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * the direction (ASCending or DESCending) {@link Direction} of the attribute
     * name used in the representation of the object with respect to sorting order in the response
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * delivers a set of Select back containing a number of attributes from the part that is within
     * the http:// ..../ some-resource?sort="value" that value may contain one or more attributes
     * which is part of the resource attributes for the endpoint that the request is targeting
     * <p>
     * the format is  The syntax is: {@literal(sort="<attribute>+/-|<attribute>+/-|...")}
     * and is equivalent to: {@literal(sort="<attribute>::+/-|<attribute>::+/-|...")}
     * <p>
     * the regexp is: <code>"^([a-zA-Z]+[a-zA-Z_0-9]*(::-|::\\+|:: )?)(\\|[a-zA-Z_0-9]+[a-zA-Z_0-9]*(::-|::\\+)?)*"</code>
     *
     * @param sorting the sort  Query Parameter
     * @return a map containing attribute and value pairs to control the sorting for the response
     */
    public static List<Sort> getSortings(String sorting) {
        return PARSER.parse(sorting);
    }

    private static Optional<Sort> parseToken(String token) {
        String attribute = getAttribute(token);
        Direction dir = getDirectionFrom(token);
        return Optional.of(new Sort(attribute, dir));
    }

    private static boolean duplicate(Sort sort1, Sort sort2) {
        return sort1.attribute.equals(sort2.attribute);
    }

    private static Direction getDirectionFrom(String sortingPair) {
        return getValue(sortingPair);
    }

    private static String getAttribute(String sort) {
        int endsAt = sort.indexOf(':');
        return endsAt > 0 ? sort.substring(0, endsAt) : sort;
    }

    private static Direction getValue(String sort) {
        int startsAt = sort.indexOf("::") + "::".length();
        if (startsAt < 2) {
            return Direction.ASC;
        }
        int endsAt = sort.indexOf('|');
        endsAt = endsAt > 0 ? endsAt : sort.length();
        return Direction.get(sort.substring(startsAt, endsAt).charAt(0));
    }
}


