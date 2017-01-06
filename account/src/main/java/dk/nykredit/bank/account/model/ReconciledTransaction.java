package dk.nykredit.bank.account.model;

import dk.nykredit.nic.persistence.jpa.AbstractAuditable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.UUID;

/**
 * Very basic modelling of an transaction concept to show the relation to account handled by JPA.
 */
@Entity
@Table(name = "BANK_RECONCILED_TX", uniqueConstraints = @UniqueConstraint(columnNames = { "FK_TRANSACTION_TID", "SID" }))
public class ReconciledTransaction extends AbstractAuditable {
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
     * Semantic key of a transaction which is exposed as key to the outside world!
     */
    @Column(name = "SID", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String id;

    /**
     * The reconciled transaction "is linked to" a transaction.
     */
    /**
     * The transaction is "owned" by account.
     */
    @ManyToOne
    @JoinColumn(name = "FK_ACCOUNT_TID", nullable = false)
    private Account account;

    @OneToOne
    @JoinColumn(name = "FK_TRANSACTION_TID", nullable = false)
    private Transaction transaction;

    @Column(name = "RECONCILED", nullable = false, columnDefinition = "CHAR(1)")
    private String reconciled;

    @Column(name = "NOTE", length = 500, nullable = false)
    private String note;

    protected ReconciledTransaction() {
        // Required by JPA
    }

    public ReconciledTransaction(Boolean reconciled, String note, Transaction tx) {
        this.reconciled = getStringFromBoolean(reconciled);
        this.note = note;
        transaction = tx;
        tId = UUID.randomUUID().toString();
        id = tx.getId();
    }

    public String getId() {
        return id;
    }

    public Boolean getReconciled() {
        return reconciled.equals("Y");
    }

    public String getNote() {
        return note;
    }

    public void setReconciled(Boolean reconciled) {
        this.reconciled = getStringFromBoolean(reconciled);
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    protected String[] excludedFields() {
        return EXCLUDED_FIELDS;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("id", id)
            .append("reconciled", reconciled)
            .append("note", note)
            .toString();
    }

    private String getStringFromBoolean(Boolean reconciled) {
        return reconciled ? "Y" : "N";
    }

}
