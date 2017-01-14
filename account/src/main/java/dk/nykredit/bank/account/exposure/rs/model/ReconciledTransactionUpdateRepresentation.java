package dk.nykredit.bank.account.exposure.rs.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import dk.nykredit.jackson.dataformat.hal.annotation.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * Represents a single transaction as returned by the REST service in a default projection.
 */
@Resource
@ApiModel(value = "ReconciledtransactionUpdate",
        description = "A way to reconcile transactions")
public class ReconciledTransactionUpdateRepresentation {

    @NotNull
    @Pattern(regexp = ".{1,256}")
    private String note;

    @NotNull
    @Pattern(regexp = "true|false")
    private String reconciled;

    @ApiModelProperty(
            access = "public",
            name = "note",
            notes = "contains information relevant to the reconciled decorated transaction.",
            value = "Read-only")
    public String getNote() {
        return note;
    }

    @ApiModelProperty(
            access = "public",
            name = "reconciled",
            notes = "signals whether the transaction is reconciled or not.",
            value = "Read-only")
    public String getReconciled() {
        return reconciled;
    }
}
