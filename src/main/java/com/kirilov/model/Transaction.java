package com.kirilov.model;

import java.util.ArrayList;
import java.util.List;

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

    public List<Integer> getAllId() {
        List<Integer> list = new ArrayList<>();
        if (fromId != 0){
            list.add(fromId);
        }
        if (id != 0){
            list.add(id);
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (sum != that.sum) return false;
        if (fromId != that.fromId) return false;
        if (id != that.id) return false;
        return transactionType == that.transactionType;
    }

    @Override
    public int hashCode() {
        int result = transactionType != null ? transactionType.hashCode() : 0;
        result = 31 * result + sum;
        result = 31 * result + fromId;
        result = 31 * result + id;
        return result;
    }
}