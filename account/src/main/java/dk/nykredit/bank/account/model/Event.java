package dk.nykredit.bank.account.model;

import dk.nykredit.nic.persistence.jpa.AbstractAuditable;
import dk.nykredit.time.CurrentTime;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Very basic modelling of an event concept to show the relation to account handled by JPA.
 *
 * The Event is shown her with a sequence and it uses the database for that, this is not recommended in
 * a cloud capable setup as this will not work in a truly distributed setup. In such as scenario it is better
 * to use som form of consensus algorithm to state what is before what in order to create a sequence.
 * An even better way is to use a dedicated feed reader status per endpoint and what endpoints are redundant
 * and in that know how far the feed reading is (behind or at target).
 *
 * The Events her is said to be auditable, but in reality this may not be the case always as not all events will
 * have to be auditable but events that e.g. includes events around accounts may have that requirement.
 */
@Entity
@SequenceGenerator (name="sequencer", initialValue=1, allocationSize=100)
@Table(name = "ACCOUNT_EVENT", uniqueConstraints = @UniqueConstraint(columnNames = {"SID", "SEQ", "TIME"}))
public class Event extends AbstractAuditable {
    private static final String[] EXCLUDED_FIELDS = new String[]{
            "tId", "lastModifiedBy", "lastModifiedTime"
    };

    /**
     * TID - the technical unique identifier for instance, i.e., primary key. This should NEVER EVER be
     * exposed out side the service since it is a key very internal to this service.
     */
    @Id
    @Column(name = "TID", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String tId;

    /**
     * Semantic key of a event which is exposed as key to the outside world!
     */
    @Column(name = "SID", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "SEQ", nullable = false, columnDefinition = "BIGINT")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sequencer")
    private Long sequence;

    @Column(name = "TIME", nullable = false)
    private Timestamp time;

    @Column(name = "CATEGORY", length = 36, nullable = false)
    private String category;

    @Column(name = "INFO", length = 200, nullable = false)
    private String information;

    @Column(name = "ORIGIN", length = 300, nullable = false)
    private String origin;

    @Transient
    private ZonedDateTime transientTime;

    protected Event() {
        // Required by JPA
    }

    /**
     * @param origin is a url pointing to the origin of the event
     */
    public Event(URI origin) {
        this(origin, CurrentTime.now());
    }

    /**
     * @param origin is a url pointing to the origin of the event
     */
    public Event(URI origin, ZonedDateTime time) {
        this(origin, "default", time);
    }

    /**
     * @param origin is a url pointing to the origin of the event
     */
    public Event(URI origin, String category, ZonedDateTime time) {
        this(UUID.randomUUID().toString(), origin, time, "", category);
    }

    /**
     * @param origin is a url pointing to the origin of the event
     */
    public Event(URI origin, String category, String info) {
        this(UUID.randomUUID().toString(), origin, CurrentTime.now(), info, category);
    }

    /**
     * @param sid    a controlled human readable and url capable identifier
     * @param origin is a url pointing to the origin of the event
     */
    public Event(String sid, URI origin, ZonedDateTime time, String information, String category) {
        this.time = new Timestamp(time.toInstant().toEpochMilli());
        this.transientTime = time;
        this.id = sid;
        this.origin = origin.getPath();
        this.information = information;
        this.category = category;
        tId = UUID.randomUUID().toString();
        // The semantic key might as well be generated as a hash value of the event values
        // for simplicity it is just a unique id here. The reason for having a technical
        // id here is the ability to merge og split events according to needs going forward.
        if (noSequence()) {
           sequence = time.toInstant().toEpochMilli() + time.getNano();
        }
    }


    public String getId() {
        return id;
    }

    public Long getSequence() {
        return sequence;
    }

    public ZonedDateTime getTime() {
        if (transientTime != null) {
            return transientTime;
        } else {
            transientTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.time.getTime()), ZoneId.of("UTC"));
            return transientTime;
        }
    }

    public URI getOrigin() {
        URI uri = null;
        try {
            uri = new URI(origin);
        } catch (URISyntaxException e) {
            //cannot do anything about that
        }
        return uri;
    }

    public String getInformation() {
        return information;
    }

    public String getCategory() {
        return category;
    }

    @Override
    protected String[] excludedFields() {
        return EXCLUDED_FIELDS;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("sequence", sequence)
                .append("time", time)
                .append("origin", origin)
                .append("information", information)
                .toString();
    }

    public static String getCategory(String scope, String name) {
        return scope + "-" + name;
    }

    private boolean noSequence() {
        return null == sequence;
    }

}
