package com.kirilov.model;


import java.io.Serializable;
import java.util.Objects;

public class TransactionBuilder {
    private TransactionType transactionType;
    private Integer sum;
    private Integer fromId;
    private Integer id;

    private interface Transfer{}
    private interface Debitbalance{}
    private interface Removeaccount{}
    private interface Addedbalance{}

    private interface TransferParams{}
    private interface DebitParams{}
    private interface RemoteParams{}
    private interface AddedParams{}
    private interface Build{}

    private class TransactionTypeBuilder implements Transfer, Debitbalance, Removeaccount, Addedbalance{}

    public TransactionBuilder setTransactionType(TransactionTypeBuilder transactionTypeBuilder) {
        this.transactionType = transactionType;
        return this;

    }

    public TransactionBuilder setSum(int sum) {
        this.sum = sum;
        return this;
    }

    public TransactionBuilder setFromId(int fromId) {
        this.fromId = fromId;
        return this;
    }

    public TransactionBuilder setId(int id) {
        this.id = id;
        return this;
    }

    TransactionType getTransactionType() {
        return transactionType;
    }

    int getSum() {
        return sum;
    }

    int getFromId() {
        return fromId;
    }

    int getId() {
        return id;
    }

    public Transaction build() {
/*        switch (this.transactionType) {
            case TRANSFER:
                if (Objects.isNull(fromId) || Objects.isNull(id) || Objects.isNull(sum)) {
                    throw new LevelpTransactionException("Invalid transaction parameters");
                } else {
                    return new Transaction(this);
                }
            case DEBITBALANCE:
                if (Objects.isNull(id) || Objects.isNull(id)) {
                    throw new LevelpTransactionException("Invalid transaction parameters");
                } else {
                    return new Transaction(this);
                }
            case ADDEDBALANCE:
                if (Objects.isNull(id) || Objects.isNull(sum)) {
                    throw new LevelpTransactionException("Invalid transaction parameters");
                } else {
                    return new Transaction(this);
                }
            case REMOVEACCOUNT:
                if (Objects.isNull(id)) {
                    throw new LevelpTransactionException("Invalid transaction parameters");
                } else {
                    return new Transaction(this);
                }
            default:
                throw new LevelpTransactionException("Invalid transaction parameters");
*/
        return new Transaction(this);
    }


}





