package com.kirilov.controller;

import com.kirilov.model.Transaction;
import com.kirilov.model.TransactionBuilder;
import com.kirilov.model.TransactionType;
import com.kirilov.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(value = "/additionalSum", params = {"id", "sum"}, method = RequestMethod.POST)
    @ResponseBody
    public void additionalSum(
            @RequestParam("id") int id,
            @RequestParam("sum") int sum,
            HttpServletResponse response) throws IOException {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.ADDEDBALANCE)
                .setId(id)
                .setSum(sum)
                .build();
        try {
            transactionService.addedBalanceAccount(transaction);
        } catch (EmptyResultDataAccessException e) {
            handleError(response, "Account not found");
        } catch (DataAccessException e) {
            handleError(response, "Database unavailable");
        } catch (Throwable e) {
            handleError(response, e.getMessage());
        }
        handleError(response, "Account updated");
    }

    @RequestMapping(value = "/debitSum", params = {"id", "sum"}, method = RequestMethod.POST)
    @ResponseBody
    public void deductionSum(
            @RequestParam("id") int id,
            @RequestParam("sum") int sum,
            HttpServletResponse response) throws IOException {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.DEBITBALANCE)
                .setId(id)
                .setSum(sum)
                .build();
        try {
            transactionService.debitBalanceAccount(transaction);
        } catch (EmptyResultDataAccessException e) {
            handleError(response, "Account not found");
        } catch (DataAccessException e) {
            handleError(response, "Database unavailable");
        } catch (Throwable e) {
            handleError(response, e.getMessage());
        }
        handleError(response, "Transaction completed");
    }

    @RequestMapping(value = "/doTransfer", params = {"fromid", "toid", "sum"}, method = RequestMethod.POST)
    @ResponseBody
    public void transfer(
            @RequestParam("fromid") int fromId,
            @RequestParam("toid") int toId,
            @RequestParam("sum") int sum,
            HttpServletResponse response) throws IOException {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.TRANSFER)
                .setFromId(fromId)
                .setId(toId)
                .setSum(sum)
                .build();
        try {
            transactionService.doTransfer(transaction);
        } catch (EmptyResultDataAccessException e) {
            handleError(response, "Account not found");
        } catch (DataAccessException e) {
            handleError(response, "Database unavailable");
        } catch (Throwable e) {
            handleError(response, e.getMessage());
        }
        handleError(response, "Transaction completed");
    }

    private void handleError(HttpServletResponse response, String message) throws IOException {
        handleError(response, message, HttpServletResponse.SC_BAD_REQUEST);
    }

    private void handleError(HttpServletResponse response, String message, int code) throws IOException {
        response.setStatus(code);
        response.getWriter().write(message);
        response.getWriter().flush();
        response.getWriter().close();
    }
}