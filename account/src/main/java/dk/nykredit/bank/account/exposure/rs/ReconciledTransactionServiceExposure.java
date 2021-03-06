package dk.nykredit.bank.account.exposure.rs;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.nykredit.api.capabilities.Sanitizer;
import dk.nykredit.bank.account.exposure.rs.model.ReconciledTransactionRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.ReconciledTransactionUpdateRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.ReconciledTransactionsRepresentation;
import dk.nykredit.bank.account.model.Account;
import dk.nykredit.bank.account.model.ReconciledTransaction;
import dk.nykredit.bank.account.model.Transaction;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.core.logging.LogDuration;
import dk.nykredit.nic.rs.EntityResponseBuilder;
import dk.nykredit.nic.rs.error.ErrorRepresentation;
import io.swagger.annotations.*;

/**
 * REST service exposing the Reconciled transactions
 *
 * A simple example of a REST exposure of reconciled transactions belonging to transactions from an account.
 * This very simple example has the sole purpose of showing a decoration of resources can be done, in this
 * case the decoration ensures that the transactions may stay immutable and thus be cachable for a significant
 * longer period.
 */
@Stateless
@Path("/accounts/{regNo}-{accountNo}/reconciled-transactions")
@Api(value = "/accounts/{regNo}-{accountNo}/reconciled-transactions",
     tags = {"decorator", "reconciled"})
public class ReconciledTransactionServiceExposure {
    private static final String CONCEPT_NAME = "reconciled-transaction";
    private static final String CONCEPT_VERSION = "1.0.0";

    @EJB
    private AccountArchivist archivist;


    @GET
    @Produces({ "application/hal+json" })
    @ApiOperation(value = "obtain reconciled transactions (added API capabilities not though not implemented)",
            response = ReconciledTransactionsRepresentation.class,
            authorizations = {@Authorization(value = "oauth", scopes = {
                    @AuthorizationScope(scope = "customer", description = "allows getting own account"),
                    @AuthorizationScope(scope = "advisor", description = "allows getting every account")})
            },
            tags = {"select", "sort", "elements", "interval", "filter", "embed", "decorator", "reconciled"},
            notes = "obtain a list of all reconciled transactions from an account" +
            "the reconciled transactions are user controlled checks and notes for transactions " +
            "such as - Yes I have verified that this transaction was correct and thus it is reconciled",
            produces = "application/hal+json, application/hal+json;concept=reconciledtransactions;v=1",
            nickname = "listReconciledTransactions")
    public Response list(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo,
                         @Context UriInfo uriInfo, @Context Request request) {
        return listReconciledTransactionsSG1V1(regNo, accountNo, uriInfo, request);
    }

    @GET
    @Path("{id}")
    @Produces({ "application/hal+json" })
    @LogDuration(limit = 50)
    @ApiOperation(value = "obtain a single reconciled transaction from a given account", response = ReconciledTransactionRepresentation.class,
            authorizations = {@Authorization( value = "oauth",scopes = {
                    @AuthorizationScope( scope = "customer", description = "allows getting own account")})
            },
            produces = "application/hal+json, application/hal+json;concept=reconciledtransaction;v=1",
            nickname = "getReconciledTransaction")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No reconciled transaction found.")
        })
    public Response get(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo, @PathParam("id") String id,
                        @Context UriInfo uriInfo, @Context Request request) {
        return getReconciledSG1V1(regNo, accountNo, id, uriInfo, request);
    }

    @PUT
    @Path("{id}")
    @Produces({ "application/hal+json" })
    @Consumes(MediaType.APPLICATION_JSON)
    @LogDuration(limit = 50)
    @ApiOperation(value = "Create new or update reconciled transaction", response = ReconciledTransactionRepresentation.class,
            authorizations = {@Authorization(value = "oauth", scopes = {
                    @AuthorizationScope(scope = "system", description = "allows getting coOwned account")})
            },
            notes = "reconciled transactions are user controlled checks and notes for transactions" +
                    "such as - Yes I have verified that this transaction was correct and thus it is reconciled",
            nickname = "updateReconciledTransaction")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "No updating possible", response = ErrorRepresentation.class)})
    public Response createOrUpdate(@PathParam("regNo") @Pattern(regexp = "^[0-9]{4}$") String regNo,
                                   @PathParam("accountNo") @Pattern(regexp = "^[0-9]+$") String accountNo,
                                   @PathParam("id") String id,
                                   @ApiParam(value = "reconciled transaction") @Valid ReconciledTransactionUpdateRepresentation rtx,
                                   @Context UriInfo uriInfo, @Context Request request) {


        String txId = Sanitizer.sanitize(id, false, true);
        Transaction tx = archivist.findTransaction(regNo, accountNo, txId);
        if (!defined(tx)) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        ReconciledTransaction reconciled = new ReconciledTransaction(rtx.getReconciled().contains("true"), rtx.getNote(), tx);
        archivist.save(reconciled);
        return new EntityResponseBuilder<>(reconciled.getTransaction(), t -> new ReconciledTransactionRepresentation(reconciled, t, uriInfo))
                .name("reconciledtransaction")
                .version("1")
                .maxAge(60)
                .build(request);
    }

    @GET
    @Produces({"application/hal+json;concept=reconciledtransactionoverview;v=1", "application/hal+json+reconciledtransactionoverview+1" })
    @LogDuration(limit = 50)
    public Response listReconciledTransactionsSG1V1(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo,
                                          @Context UriInfo uriInfo, @Context Request request) {
        Account account = archivist.getAccount(regNo, accountNo);
        return new EntityResponseBuilder<>(account.getReconciledTransactions(),
            transactions -> new ReconciledTransactionsRepresentation(account, uriInfo))
            .name("reconciledtransactions")
            .version("1")
            .maxAge(10)
            .build(request);
    }

    @GET
    @Path("{id}")
    @Produces({"application/hal+json;concept=reconciledtransaction;v=1", "application/hal+json+reconciledtransaction+1" })
    @LogDuration(limit = 50)
    /**
     * If you are running a JEE container that inhibits the creation of resources, because it does
     * not support the specification of the Accept header and thus does not support the media-range
     * parameters, a simple producer has to be annotated and if the
     * "application/hal+json;concept=ReconciledTransaction;v=1.0.0" is removed and replaced with
     * "{"application/hal+json+reconciledtransaction+1" then the endpoint will work with versioning.
     * The correct content-type controlled by the Accept header is "application/hal+json;concept=ReconciledTransaction;v=1.0.0"
     */
    public Response getReconciledSG1V1(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo, @PathParam("id") String id,
                             @Context UriInfo uriInfo, @Context Request request) {
        ReconciledTransaction reconciledTransaction = archivist.getReconciledTransaction(regNo, accountNo, id);
        return new EntityResponseBuilder<>(reconciledTransaction,
            rt -> new ReconciledTransactionRepresentation(rt, rt.getTransaction(), uriInfo))
            .maxAge(24 * 60 * 60)
            .name("reconciledtransaction")
            .version("1")
            .build(request);
    }

    private boolean defined(Transaction tx) {
        return tx != null;
    }
}
