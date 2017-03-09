package com.kirilov.controller;

import com.kirilov.model.LevelpTransactionException;
import com.kirilov.model.Transaction;
import com.kirilov.model.TransactionBuilder;
import com.kirilov.model.TransactionType;
import com.kirilov.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/additionalSum", params = {"id", "sum"}, method = RequestMethod.POST)
    public
    @ResponseBody
    String additionalSum(
            @RequestParam("id") int toId,
            @RequestParam("sum") int sum) {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.ADDEDBALANCE)
                .setId(toId)
                .setSum(sum)
                .build();
        try {
            transactionService.addedBalanceAccount(transaction);
        } catch (EmptyResultDataAccessException e) {
            return "Account not found";
        }
        return "OK";
    }

    @RequestMapping(value = "/debitSum", params = {"id", "sum"}, method = RequestMethod.POST)
    public
    @ResponseBody
    String deductionSum(
            @RequestParam("id") int toId,
            @RequestParam("sum") int sum) {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.DEBITBALANCE)
                .setId(toId)
                .setSum(sum)
                .build();
        try {
            transactionService.debitBalanceAccount(transaction);
        } catch (LevelpTransactionException e) {
            return e.getMessage();
        }
        return "OK";
    }

    @RequestMapping(value = "/doTransfer", params = {"fromId", "toId", "sum"}, method = RequestMethod.POST)
    public
    @ResponseBody
    String transfer(
            @RequestParam("fromId") int fromId,
            @RequestParam("toId") int toId,
            @RequestParam("sum") int sum) {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.TRANSFER)
                .setFromId(fromId)
                .setId(toId)
                .setSum(sum)
                .build();
        try {
            transactionService.doTransfer(transaction);
        } catch (LevelpTransactionException e) {
            return e.getMessage();
        } catch (EmptyResultDataAccessException e) {
            return "Account not found";
        }
        return "OK";
    }
}