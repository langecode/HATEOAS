package dk.nykredit.bank.account.exposure.rs;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.nykredit.api.capabilities.Interval;
import dk.nykredit.bank.account.exposure.rs.model.EventRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.EventsMetadataRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.EventsRepresentation;
import dk.nykredit.bank.account.model.Event;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.core.logging.LogDuration;
import dk.nykredit.nic.rs.EntityResponseBuilder;
import dk.nykredit.time.CurrentTime;

import io.swagger.annotations.*;

/**
 * REST exposure of events that are related to the account.
 *
 * category - can be regNo-accountNo if the events are related to a given account and that would be the case for eg. transactions
 * belonging to a given account. The default category is named (default) but any category can be used, it has to unique though.
 *
 * The metadata resource should evolve further in order for it to take metadata as something that is delivered by a dynamic
 * content setup such as the the DCN++ a combination between a classical cdn and a json based content delivery.
 */
@Stateless
@PermitAll
@Path("/account-events")
@DeclareRoles("tx-system")
@Api(value = "/account-events",
     tags = {"events"})
public class EventServiceExposure {
    private static final String CONCEPT_NAME = "accountEvent";
    private static final String CONCEPT_VERSION = "1.0.0";

    @EJB
    private AccountArchivist archivist;

    @GET
    @Produces({"application/hal+json;concept=metadata", "application/hal+json+metadata"})
    @ApiOperation(
            value = "metadata for the events endpoint", response = EventsMetadataRepresentation.class,
            authorizations = {@Authorization( value = "Bearer")},
            notes = " the events are signalled by this resource as this this is the authoritative resource for all events that " +
                    "subscribers to the account service should be able to listen for and react to. In other words this is the authoritative" +
                    "feed for the account service",
            tags = {"events"},
            nickname = "getMetadata"
    )
    public Response getMetadata(@Context UriInfo uriInfo, @Context Request request) {
        return getMetaDataSG1V1(uriInfo);
    }

    @GET
    @Produces({"application/hal+json"})
    @ApiOperation(
            value = "obtain all events emitted by the account-event service", response = EventsRepresentation.class,
            notes = " the events are signalled by this resource as this this is the authoritative resource for all events that " +
                    "subscribers to the account service should be able to listen for and react to. In other words this is the authoritative" +
                    "feed for the account service",
            authorizations = {@Authorization( value = "Bearer"), @Authorization(value = "oauth")},
            tags = {"interval", "events"},
            nickname = "listAllEvents"
    )
    public Response listAll(@QueryParam("interval") String interval,
                         @Context UriInfo uriInfo, @Context Request request) {
        return listAllSG1V1(interval, uriInfo, request);
    }


    @GET
    @Path("{category}")
    @Produces({ "application/hal+json" })
    @ApiOperation(value = "obtain all events scoped to a certain category", response = EventsRepresentation.class,
            notes = " the events are signalled by this resource as this this is the authoritative resource for all events that " +
                    "subscribers to the account service should be able to listen for and react to. In other words this is the authoritative" +
                    "feed for the account service, allowing for subscribers to have these grouped into categories",
            authorizations = {@Authorization( value = "Bearer"), @Authorization(value = "oauth")},
            tags = {"interval", "events"},
            nickname = "getEventsByCategory"
    )
    public Response getByCategory(@PathParam("category") String category,
                                @QueryParam("interval") String interval,
                                @Context UriInfo uriInfo, @Context Request request) {
        return listByCategorySG1V1(category, interval, uriInfo, request);
    }

    @GET
    @Path("{category}/{id}")
    @Produces({ "application/hal+json" })
    @LogDuration(limit = 50)
    @ApiOperation(
            value = "obtain the individual events from an account", response = EventRepresentation.class,
            notes = "the event her is immutable and thus can be cached for a long time",
            authorizations = {@Authorization( value = "Bearer"), @Authorization(value = "oauth")},
            tags = {"immutable", "events"},
            nickname = "getEvent")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No event found.")
    })
    public Response getSingle(@PathParam("category") String category, @PathParam("id") String id,
                        @Context UriInfo uriInfo, @Context Request request) {
        return getSG1V1(category, id, uriInfo, request);
    }


    @GET
    @Produces({"application/hal+json;concept=events;v=1", "application/hal+json+account+events+1"})
    @LogDuration(limit = 50)
    /**
     * If you are running a JEE container that inhibits the creation of resources, because it does
     * not support the specification of the Accept header and thus does not support the media-range
     * parameters, a simple producer has to be annotated and if the
     * "application/hal+json;concept=TransactionOverview;v=1.0.0" is removed and replaced with
     * "{"application/hal+json+account+event+1" then the endpoint will work with versioning.
     * The correct content-type controlled by the Accept header is "application/hal+json;concept=Event;v=1.0.0"
     */
    public Response listAllSG1V1(String interval, UriInfo uriInfo, Request request) {
        Optional<Interval> withIn = Interval.getInterval(interval);
        List<Event> events = archivist.findEvents(withIn);
        return new EntityResponseBuilder<>(events, txs -> new EventsRepresentation(events, uriInfo))
                .maxAge(60)
                .build(request);
    }


    @GET
    @Path("{category}")
    @Produces({"application/hal+json;concept=eventcategory;v=1", "application/hal+json+account+eventcategory+1" })
    @LogDuration(limit = 50)
    /**
     * If you are running a JEE container that inhibits the creation of resources, because it does
     * not support the specification of the Accept header and thus does not support the media-range
     * parameters, a simple producer has to be annotated and if the
     * "application/hal+json;concept=AccountEvent;v=1.0.0" is removed and replaced with
     * "{"application/hal+json+account+event+1" then the endpoint will work with versioning.
     * The correct content-type controlled by the Accept header is "application/hal+json;concept=Event;v=1.0.0"
     */
    public Response listByCategorySG1V1(@PathParam("category") String category,
                                        @QueryParam("interval") String interval,
                                        @Context UriInfo uriInfo, @Context Request request) {
        Optional<Interval> withIn = Interval.getInterval(interval);
        List<Event> events = archivist.getEventsForCategory(category, withIn);
        return new EntityResponseBuilder<>(events, txs -> new EventsRepresentation(events, uriInfo))
                .maxAge(60)
                .build(request);
    }

    @GET
    @Path("{category}/{id}")
    @Produces({"application/hal+json;concept=event;v=1", "application/hal+json+account+event+1" })
    @LogDuration(limit = 50)
    /**
     * If you are running a JEE container that inhibits the creation of resources, because it does
     * not support the specification of the Accept header and thus does not support the media-range
     * parameters, a simple producer has to be annotated and if the
     * "application/hal+json;concept=Event;v=1.0.0" is removed and replaced with
     * "{"application/hal+json+account+event+1" then the endpoint will work with versioning.
     * The correct content-type controlled by the Accept header is "application/hal+json;concept=Event;v=1.0.0"
     */
    public Response getSG1V1(@PathParam("category") String category, @PathParam("id") String id,
                        @Context UriInfo uriInfo, @Context Request request) {
        Event event = archivist.getEvent(category, id);
        return new EntityResponseBuilder<>(event, e -> new EventRepresentation(e,uriInfo))
                .maxAge(7 * 24 * 60 * 60)
                .name(CONCEPT_NAME)
                .version(CONCEPT_VERSION)
                .build(request);
    }

    @GET
    @Produces({"application/hal+json;concept=metadata;v=1", "application/hal+json+metadata+1" })
    @LogDuration(limit = 50)
    public Response getMetaDataSG1V1(@Context UriInfo uriInfo) {
        EventsMetadataRepresentation em  = new EventsMetadataRepresentation("", uriInfo);
        CacheControl cc = new CacheControl();
        int maxAge = 4*7*24*60*60;
        cc.setMaxAge(maxAge);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("concept", "metadata");
        parameters.put("v", "1.0.0");
        return Response.ok()
                .entity(em)
                .cacheControl(cc).expires(Date.from(CurrentTime.now().toInstant().plusSeconds(maxAge)))
                .type(EntityResponseBuilder.getMediaType(parameters, false))
                .build();
    }
}
