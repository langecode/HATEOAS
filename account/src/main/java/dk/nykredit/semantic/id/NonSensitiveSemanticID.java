package dk.nykredit.semantic.id;

/**
 * Non Sensitive Semantic ID interface.
 * 
 * The idea is to create non-sensitive human readable identifiers and do so in approximate alignment with the MIFID
 * concat rules and do that in a way that the ID created can be part of a URL.
 *
 * the interface is implemented and signals the creation of non-sensitive ids for objects that have natural id's
 * that are sensitive.
 *
 * The creation of semantic ids is necessary in situations where the natural semantic id is sensitive and
 * thus cannot be part of the url. An example of this is a personal id used for people in Denmark called CPR number.
 * This number is protected by law and thus this must not be used as semantic id for a person.
 * Persons therefore needs a new non-sensitive id, which can be created from the first name,
 * a potential middle name, the family name, the day in month where the person was born,
 * the month of the year where the person was born and if not unique add a sequence number.
 *
 * This is very similar to the MIFID CONCAT definitions where they unfortunately have used "#" for separation
 * and a length that will cause:
 * {@literal(
 *   - the names to be less human readable
 *   - result in name clashes
 *   - not useful as semantic ids for person in a URL
 * require more added sequence numbers.
 *
 * Therefore the proposed format is: <-sequence number>
 *  )}
 * examples:
 *<code>
 *    hans-peter-hansen-0112
 *
 *    hans-peter-hansen-0112-1
 *</code>
 * The second is created if two persons with the same name and born on the same day and month exists"
 *
 * <code>
 *    mike-hansson-0309
 * </code>
 *
 * If a "restricted length scenario" exist a length of 10 characters for first name, and 10 characters for middle name
 * and equally 10 characters for family name has been suggested and 999 as max sequence number.
 *
 * Such limitations should be written as a part of the service API and thus the consumers of services would know them.
 */

public interface NonSensitiveSemanticID {
    int LENGTH = 10; // MIFID concat is 5
    char SEPARATOR = '-'; // MIFID concat says # which is non URL friendly

    /**
     * @return a non sensitive human readable URL capable semantic ID for a given object
     */
    String getHumanReadableNonSensitiveID();

    /**
     * @param observed the number of instances observed with the same semantic id
     * @return a non sensitive human readable URL capable semantic ID for a given object adjusted to the
     * last observed instance e.g. hans-peter-hansen-0112 means the observation is 1 observed, if the situation is
     * hans-peter-hansen-0112-1 then 2 are observes (the initial and the first duplicate)
     */
    String adjustHumanReadableNonSensitiveID(long observed);
}
