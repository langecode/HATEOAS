package dk.nykredit.bank.account.exposure.rs.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * the necessary input for creation of an Account and used for updating an Account.
 */
@ApiModel(value="AccountUpdate",
        description="the inout necessary for creating an Account")

public class AccountUpdateRepresentation {

    @Pattern(regexp = "^[0-9]{4}$")
    private String regNo;

    @Pattern(regexp = "^[0-9]+$")
    private String accountNo;

    @NotNull
    @Pattern(regexp = ".{1,40}")
    private String name;

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
}
