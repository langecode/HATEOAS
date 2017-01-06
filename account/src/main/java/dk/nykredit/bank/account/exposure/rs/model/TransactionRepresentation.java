package dk.nykredit.bank.account.exposure.rs.model;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.bank.account.model.Transaction;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represents a single transaction in its default projection as returned by the REST service.
 */
@Resource
@ApiModel(value="Transaction",
        description="An classical domain immutable transaction")

public class TransactionRepresentation {
    private String id;
    private String description;
    private String amount;

    @Link
    private HALLink self;

    public TransactionRepresentation(Transaction transaction, UriInfo uriInfo) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount().toPlainString();
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(TransactionServiceExposure.class)
            .path(TransactionServiceExposure.class, "get")
            .build(transaction.getAccount().getRegNo(), transaction.getAccount().getAccountNo(), transaction.getId()))
            .build();
    }

    @ApiModelProperty(
            access = "public",
            name = "id",
            notes = "a semantic (here shown as UUID) identifier for the transaction.",
            value = "Read-only")
    public String getId() {
        return id;
    }

    @ApiModelProperty(
            access = "public",
            name = "description",
            notes = "the human readable description of the transaction.",
            value = "Read-only")
    public String getDescription() {
        return description;
    }

    @ApiModelProperty(
            access = "public",
            name = "amount",
            notes = "the amount - in this example without currency.",
            value = "Read-only")
    public String getAmount() {
        return amount;
    }

    @ApiModelProperty(
            access = "public",
            name = "self",
            notes = "link to the transaction itself.")
    public HALLink getSelf() {
        return self;
    }
}
