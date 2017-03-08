package com.kirilov.model;


public class TransactionBuilder {
    private TransactionType transactionType;
    private int sum;
    private int fromId;
    private int id;

    public TransactionBuilder setTransactionType(TransactionType transactionType) {
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
        return new Transaction(this);
    }
}

