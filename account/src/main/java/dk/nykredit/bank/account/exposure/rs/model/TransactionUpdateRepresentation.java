package dk.nykredit.bank.account.exposure.rs.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * the necessary input for creation of a Transaction.
 */
@ApiModel(value="TransactionUpdate",
        description="the input necessary for creating a transaction")
public class TransactionUpdateRepresentation {

    @NotNull
    @Pattern(regexp = ".{1,256}")
    private String description;

    @NotNull
    @Pattern(regexp = "^([0-9]{1,9})((\\.)([0-9]{2}))?")
    private String amount;


    @ApiModelProperty(
            access = "public",
            name = "description",
            notes = "the human readable description of the transaction.",
            value = "Readable and Writeable")
    public String getDescription() {
        return description;
    }

    @ApiModelProperty(
            access = "public",
            name = "amount",
            notes = "the amount - in this example without currency.",
            value = "Readable and Writeable")
    public String getAmount() {
        return amount;
    }
}
