package dk.nykredit.api.capabilities;

import java.util.List;
import java.util.Optional;

/**
 * Filter signals a dynamic projection needed from a consumer perspective.
 *
 * The Query parameters filter is used for signalling to the server that a dynamic projection is desired as the response from the service.
 * The service is not obliged to be able to do that, but may return the standard projection of the objects given for that concrete endpoint.
 * This can be used for discovery of what projections service consumers would like to have and
 * help evolving the API to stay relevant and aligned with the consumers use of the service.
 * <p>
 * The syntax is: {@literal(filter="<attribute>::+/-|<attribute>::+/-")}
 * + means include only
 * - means exclude only
 * <p>
 * Example:
 * https://banking.services.sample-bank.dk/accounts/1234-56789?filter="balance::-|name::-"
 * which ideally returns a account object in the response without balance and name attributes.
 * </p>
 * The service may however in the event that this is not supported, choose to return a complete object and not this sparse dynamic view.
 * <p>
 * Example:
 * https://banking.services.sample-bank.dk/accounts/1234-56789?filter="balance::+|name::+"
 * which ideally returns a account object in the response with only balance and name attributes.
 * </p>
 * The service may however in the event that this is not supported, choose to return a complete object and not this sparse dynamic view.
 * The <code>filter</code> APi capability is handled by the Filter class and this is a simplistic way to get the
 * QueryParam mapped to a Map. The responsibility for having a Map is that every <code>attribute</code> used to specify
 * sorting can only be present once as the same attribute with multiple representations and thus include or not does
 * not make sense, the Map allows only one key and having the attribute (stems from the json and thus the representation)
 * as key helps to enforce that.
 *
 * In this case the mapping between the Filter API capability, which is an integral part of the exposure is
 * mapped straight to a corresponding internal model. That is the attribute naming is here expected to be
 * the same for the exposure and representation and onto the model. That will not always be the case and thus
 * the Filter Capability must be extended for these cases and that will enable the a variable mapping from the
 * representation and on to the model.
 *
 * The responsibility for the mapping is chosen to be done in this layer as an extension in order to avoid having the
 * Model "infected" with mapping to and from the representations. Another choice could have been to let the
 * representation(s) do that and that would have concrete model class persistable field knowledge in the representation(s)
 * which is not desirable and thus the choice was to let the Filter class take that responsibility and thus extensions can handle
 * necessary translations or mappings between the exposure and representations from and to the model.
 *
 */
public class Filter {
    private static final String REGEX = "^([a-zA-Z_0-9]+[a-zA-Z_0-9]*(::-|::\\+|:: |::)?)(\\|[a-zA-Z_0-9]+[a-zA-Z_0-9]*(::-|::\\+|:: |::)?)*";
    private static final CapabilityParser<Filter> PARSER = new CapabilityParser<>(REGEX, Filter::parseToken, Filter::duplicate);

    private String attribute = "";
    private Inclusion inclusion = Inclusion.INC;

    private Filter(String attribute, Inclusion inclusion) {
        this.attribute = attribute;
        this.inclusion = inclusion;
    }

    /**
     * @return the attribute that is part of default projection
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @return the information on whether the default projection attribute is part of the Filter aka. the dynamic projection or not
     */
    public Inclusion getInclusion() {
        return inclusion;
    }

    /**
     * Delivers the set of attributes that can be part of a dynamic projection.
     * The syntax supported is given by the regexp: 
     * <code>"^([a-zA-Z_0-9]+[a-zA-Z_0-9]*(::-|::\\+|:: |::)?)(\\|[a-zA-Z_0-9]+[a-zA-Z_0-9]*(::-|::\\+|:: |::)?)*"</code>
     * @param filter a string containing the criteria for a dynamic projection either include or exclude as principle {@link Inclusion}
     * @return the resulting attributes and the inclusion or exclusion of these
     *
     */
    public static List<Filter> getFilter(String filter) {
        return PARSER.parse(filter);
    }

    private static Optional<Filter> parseToken(String token) {
        String attribute = getAttributeFrom(token);
        Inclusion inc = getInclusion(token);
        return Optional.of(new Filter(attribute, inc));
    }

    private static boolean duplicate(Filter filter1, Filter filter2) {
        return filter1.attribute.equals(filter2.attribute);
    }

    private static Inclusion getInclusion(String filterPair) {
        return getValueFrom(filterPair);
    }

    private static String validValue(String filter) {
        return Sanitizer.sanitize(filter, true, true);
    }

    private static String getAttributeFrom(String filter) {
        int endsAt = filter.indexOf(':');
        return endsAt > 0 ? filter.substring(0, endsAt) : filter;
    }

    private static Inclusion getValueFrom(String inclusion) {
        String result = validValue(inclusion);
        int startsAt = result.indexOf("::") + "::".length();
        if (startsAt < 2) {
            return Inclusion.INC;
        }
        int endsAt = result.indexOf('|');
        endsAt = endsAt > 0 ? endsAt : result.length();
        result = result.substring(startsAt, endsAt);
        return startsAt < endsAt ? Inclusion.get(result.charAt(0)) : Inclusion.INC;
    }
}
