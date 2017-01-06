package dk.nykredit.bank.account.exposure.rs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.AccountServiceExposure;
import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.bank.account.model.Account;
import dk.nykredit.bank.account.model.Transaction;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represents a single Account as returned from REST service in the default projection.
 */
@Resource
@ApiModel(value="Account",
        description="the Account")
public class AccountRepresentation {
    private String regNo;
    private String accountNo;
    private String name;

    @EmbeddedResource("transactions")
    private Collection<TransactionRepresentation> transactions;

    @Link("account:transactions")
    private HALLink transactionsResource;

    @Link
    private HALLink self;

    public AccountRepresentation(Account account, Set<Transaction> transactions, UriInfo uriInfo) {
        this(account, uriInfo);
        this.transactions = new ArrayList<>();
        this.transactions.addAll(transactions.stream()
            .map(transaction -> new TransactionRepresentation(transaction, uriInfo))
            .collect(Collectors.toList()));
    }

    public AccountRepresentation(Account account, UriInfo uriInfo) {
        this.regNo = account.getRegNo();
        this.accountNo = account.getAccountNo();
        this.name = account.getName();
        this.transactionsResource = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(TransactionServiceExposure.class)
            .build(account.getRegNo(), account.getAccountNo())).build();
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(AccountServiceExposure.class)
            .path(AccountServiceExposure.class, "get")
            .build(account.getRegNo(), account.getAccountNo()))
            .build();
    }

    @ApiModelProperty(
            access = "public",
            name = "regno",
            notes = "the registration number preceeding the account  number.",
            value = "Read-only")
    public String getRegNo() {
        return regNo;
    }

    @ApiModelProperty(
            access = "public",
            name = "accountno",
            notes = "the account  number.",
            value = "Read-only")
    public String getAccountNo() {
        return accountNo;
    }

    @ApiModelProperty(
            access = "public",
            name = "name",
            notes = "the human readable name of the account.",
            value = "Readable and Writeable")
    public String getName() {
        return name;
    }

    @ApiModelProperty(
            access = "public",
            name = "transactions",
            notes = "the transactions that have taken place for the account.",
            value = "Read-only")
    public Collection<TransactionRepresentation> getTransactions() {
        if (transactions == null) {
            return null;
        } else {
            return Collections.unmodifiableCollection(transactions);
        }
    }

    @ApiModelProperty(
            access = "public",
            name = "transactions",
            notes = "link to the collection of transactions that have taken place for the account.")
    public HALLink getTransactionsResource() {
        return transactionsResource;
    }

    @ApiModelProperty(
            access = "public",
            name = "self",
            notes = "link to the account itself.")
    public HALLink getSelf() {
        return self;
    }
}
