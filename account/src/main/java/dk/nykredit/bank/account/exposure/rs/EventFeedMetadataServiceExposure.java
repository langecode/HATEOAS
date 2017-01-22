package dk.nykredit.bank.account.exposure.rs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.model.EventsMetadataRepresentation;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.core.logging.LogDuration;
import dk.nykredit.time.CurrentTime;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

/**
 * REST exposure of metadata for events that are related to the account.
 *
 * Remark:
 *  the <code>/account-events-metadata/{category}/{id} </code> is experimental and only used for API discussions
 *  the correct implementation would be having a <code>/events/{id}</code> resource and a <code>/categories/{id}</code>
 *  resource with a relation to the events so in effect you could perceive this as  <code>/categories/{id}/events</code>
 *  where the latter part <code>"/events"</code> are a curie.
 *
 *
 * category - can be regNo-accountNo if the events are related to a given account and that would be the case for eg. transactions
 * belonging to a given account. The default category is named (default) but any category can be used, it has to unique though.
 *
 * The metadata resource should evolve further in order for it to take metadata as something that is delivered by a dynamic
 * content setup such as the the DCN++ a combination between a classical cdn and a json based content delivery.
 */
@Stateless
@PermitAll
@Path("/account-events-metadata")
@DeclareRoles("tx-system")
@Api(value = "/account-events-metadata",
     tags = {"metadata"})
public class EventFeedMetadataServiceExposure {
    private final Map<String, EventMetadataProducerMethod> eventMetadataProducers = new HashMap<>();

    @EJB
    private AccountArchivist archivist;

    public EventFeedMetadataServiceExposure() {
        eventMetadataProducers.put("application/hal+json", this::getMetaDataSG1V1);
        eventMetadataProducers.put("application/hal+json;concept=metadata;v=1", this::getMetaDataSG1V1);
    }

    @GET
    @Produces({"application/hal+json", "application/hal+json;concept=metadata;v=1"})
    @ApiOperation(
            value = "metadata for the events endpoint", response = EventsMetadataRepresentation.class,
            authorizations = {@Authorization(value = "Bearer")},
            notes = " the events are signalled by this resource as this this is the authoritative resource for all events that " +
                    "subscribers to the account service should be able to listen for and react to. In other words this is the authoritative" +
                    "feed for the account service",
            tags = {"events"},
            produces = "application/hal+json,  application/hal+json;concept=metadata;v=1",
            nickname = "getMetadata"
    )
    public Response getMetadata(@HeaderParam("Accept") String accept, @Context UriInfo uriInfo, @Context Request request) {
        return eventMetadataProducers.get(accept).getResponse(uriInfo, request);
    }

    @LogDuration(limit = 50)
    public Response getMetaDataSG1V1(UriInfo uriInfo, Request request) {
        EventsMetadataRepresentation em  = new EventsMetadataRepresentation("", uriInfo);
        CacheControl cc = new CacheControl();
        int maxAge = 4 * 7 * 24 * 60 * 60;
        cc.setMaxAge(maxAge);

        return Response.ok()
                .entity(em)
                .cacheControl(cc).expires(Date.from(CurrentTime.now().plusSeconds(maxAge)))
                .type("application/hal+json;concept=metadata;v=1")
                .build();
    }

    interface EventMetadataProducerMethod {
        Response getResponse(UriInfo uriInfo, Request request);
    }

}
