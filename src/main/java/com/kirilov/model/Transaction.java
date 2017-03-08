package com.kirilov.model;

public class Transaction {
    private TransactionType transactionType;
    private int sum;
    private int fromId;
    private int id;

    Transaction(TransactionBuilder transactionBuilder) {
        this.transactionType = transactionBuilder.getTransactionType();
        this.sum = transactionBuilder.getSum();
        this.fromId = transactionBuilder.getFromId();
        this.id = transactionBuilder.getId();
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getSum() {
        return sum;
    }

    public int getFromId() {
        return fromId;
    }

    public int getId() {
        return id;
    }
}