package dk.nykredit.bank.account.exposure.rs;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.nykredit.api.capabilities.Element;
import dk.nykredit.api.capabilities.Interval;
import dk.nykredit.api.capabilities.Sort;
import dk.nykredit.bank.account.exposure.rs.model.TransactionRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.TransactionUpdateRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.TransactionsRepresentation;
import dk.nykredit.bank.account.model.Account;
import dk.nykredit.bank.account.model.Event;
import dk.nykredit.bank.account.model.Transaction;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.core.logging.LogDuration;
import dk.nykredit.nic.rs.EntityResponseBuilder;
import dk.nykredit.nic.rs.error.ErrorRepresentation;
import dk.nykredit.time.CurrentTime;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.ResponseHeader;

/**
 * REST exposure of account transactions.
 */
@Stateless
@PermitAll
@DeclareRoles("tx-system")
@Path("/accounts/{regNo}-{accountNo}/transactions")
@Api(value = "/accounts/{regNo}-{accountNo}/transactions",
     tags = {"immutable", "transactions"})
public class TransactionServiceExposure {

    private final Map<String, TransactionsProducerMethod> transactionsProducers = new HashMap<>();
    private final Map<String, TransactionProducerMethod> transactionProducers = new HashMap<>();

    @EJB
    private AccountArchivist archivist;

    public TransactionServiceExposure() {
        transactionsProducers.put("application/hal+json", this::listTransactionsSG1V1);
        transactionsProducers.put("application/hal+json;concept=transactionoverview;v=1", this::listTransactionsSG1V1);

        transactionProducers.put("application/hal+json", this::getSG1V1);
        transactionProducers.put("application/hal+json;concept=transaction;v=1", this::getSG1V1);
    }

    @GET
    @Produces({ "application/hal+json", "application/hal+json;concept=transactionoverview;v=1" })
    @ApiOperation(
            value = "obtain all transactions on account for a given account", response = TransactionsRepresentation.class,
            authorizations = {@Authorization(value = "oauth", scopes = {
                    @AuthorizationScope(scope = "customer", description = "allows getting own account"),
                    @AuthorizationScope(scope = "advisor", description = "allows getting every account")})
            },
            tags = {"sort", "elements", "interval", "transactions"},
            produces = "application/hal+json, application/hal+json;concept=transactionoverview;v=1",
            nickname = "listTransactions"
        )
    public Response list(@HeaderParam("Accept") String accept, @PathParam("regNo") String regNo,
                         @PathParam("accountNo") String accountNo,
                         @QueryParam("sort") String sort, @QueryParam("elements") String elements,
                         @QueryParam("interval") String interval,
                         @Context UriInfo uriInfo, @Context Request request) {
        return transactionsProducers.get(accept).getResponse(regNo, accountNo, sort, elements, interval, uriInfo, request);
    }

    @GET
    @Path("{id}")
    @Produces({ "application/hal+json", "application/hal+json;concept=transaction;v=1"})
    @LogDuration(limit = 50)
    @ApiOperation(
            value = "obtain the individual single transaction from an account", response = TransactionRepresentation.class,
            authorizations = {@Authorization(value = "oauth", scopes = {
                    @AuthorizationScope(scope = "customer", description = "allows getting own account"),
                    @AuthorizationScope(scope = "advisor", description = "allows getting every account")})
            },
            produces = "application/hal+json, application/hal+json;concept=transaction;v=1",
            nickname = "getTransaction")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No transaction found.")
            })
    /**
     * the use of authorization scopes to signal roles is a bit dubious and thus this may change in the future
     */
    public Response get(@HeaderParam("Accept") String accept,
                        @PathParam("regNo") String regNo,
                        @PathParam("accountNo") String accountNo,
                        @PathParam("id") String id,
                        @Context UriInfo uriInfo, @Context Request request) {
        return transactionProducers.get(accept).getResponse(regNo, accountNo, id, uriInfo, request);
    }

    @PUT
    @Path("{id}")
    @RolesAllowed("tx-system")
    @Produces({ "application/hal+json"})
    @Consumes("application/json")
    @LogDuration(limit = 50)
    @ApiOperation(value = "creates a single transaction on an account", response = TransactionRepresentation.class,
            authorizations = {
                    @Authorization(value = "oauth", scopes = {
                        @AuthorizationScope(scope = "system", description = "allows getting coOwned account")})
            },
            consumes = "application/json",
            produces = "application/hal+json, application/hal+json;concept=transaction;v=1",
            nickname = "setTransaction")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Could create the new transaction", response = ErrorRepresentation.class),
            @ApiResponse(code = 201, message = "New transaction created.", response = TransactionRepresentation.class),
            @ApiResponse(code = 202, message = "New transaction will be created at some point in time.", response = Void.class,
                    responseHeaders = {
                            @ResponseHeader(name = "Location", description = "A link to where the response can be obtained"),
                            @ResponseHeader(name = "Retry-After", description = "A time where you when you can expect the response ")
                    })
            })
    /**
     * note that the 202 is included from the beginning of the API to avoid breaking the API  when the 202 is used e.g.
     * if unavoidable external dependencies are consuming time above the 50 ms limit and a 202 is returned instead of
     * the 200. Therefore it may be a good idea to include the 202 at an early stage in you API to avoid introducing
     * the need for the X-Service-Generation header signalling that the API is broken not for content but structurally
     * broken. Content is handled through the versioning mechanism in the content-type for producers and possibly for
     * the consumers (the latter will not happen as often)
     */
    public Response set(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo, @PathParam("id") String id,
                        @ApiParam(value = "transaction") @Valid TransactionUpdateRepresentation tx,
                        @Context UriInfo uriInfo, @Context Request request) {

        Optional<Account> acc = archivist.findAccount(regNo, accountNo);
        Account a;
        Transaction t;
        if (acc.isPresent()) {
            a = acc.get();
            try {
                t = new Transaction(a, new BigDecimal(tx.getAmount()), tx.getDescription());
                a.addTransaction(t.getDescription(), t.getAmount());
                archivist.save(a);

                CacheControl cc = new CacheControl();
                int maxAge = 30;
                cc.setMaxAge(maxAge);

                TransactionRepresentation transaction = new TransactionRepresentation(t, uriInfo);
                Response response = Response.created(URI.create(uriInfo.getPath()))
                        .entity(transaction)
                        .cacheControl(cc).expires(Date.from(CurrentTime.now().plusSeconds(maxAge)))
                        .status(201)
                        .type("application/hal+json;concept=transaction;v=1")
                        .build();
                Event newTX = new Event(new URI(uriInfo.getPath()), Event.getCategory(accountNo, regNo),
                        "new transaction on account " + regNo + "-" + accountNo);
                archivist.save(newTX);
                return response;
            } catch (URISyntaxException e) {
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            } catch (NumberFormatException nfe) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            } catch (RuntimeException e) {
                throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
            }
        }
        throw new WebApplicationException(Response.Status.SERVICE_UNAVAILABLE);
    }

    @LogDuration(limit = 50)
    public Response listTransactionsSG1V1(String regNo, String accountNo, String sort, String elements,
                                          String interval, UriInfo uriInfo, Request request) {
        List<Sort> sortAs = Sort.getSortings(sort);
        Optional<Element> elementSet = Element.getElement(elements);
        Optional<Interval> withIn = Interval.getInterval(interval);
        List<Transaction> transactions = archivist.getTransactions(regNo, accountNo, elementSet, withIn, sortAs);
        return new EntityResponseBuilder<>(transactions, txs -> new TransactionsRepresentation(regNo, accountNo, transactions, uriInfo))
                .name("transactionoverview")
                .version("1")
                .maxAge(10)
                .build(request);
    }

    @LogDuration(limit = 50)
    public Response getSG1V1(String regNo, String accountNo, String id, UriInfo uriInfo, Request request) {
        Transaction transaction = archivist.getTransaction(regNo, accountNo, id);
        return new EntityResponseBuilder<>(transaction, t -> new TransactionRepresentation(t, uriInfo))
                .maxAge(7 * 24 * 60 * 60)
                .name("transaction")
                .version("1")
                .build(request);
    }

    interface TransactionsProducerMethod {
        Response getResponse(String regNo, String accountNo, String sort, String elements, String interval,
                             UriInfo uriInfo, Request request);
    }

    interface TransactionProducerMethod {
        Response getResponse(String regNo, String accountNo, String id,
                             UriInfo uriInfo, Request request);
    }
}
