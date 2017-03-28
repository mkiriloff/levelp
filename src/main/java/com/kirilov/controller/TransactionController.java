package com.kirilov.controller;

import com.kirilov.model.RuntimeTransactionException;
import com.kirilov.model.Transaction;
import com.kirilov.model.TransactionBuilder;
import com.kirilov.model.TransactionType;
import com.kirilov.service.TransactionService;
import org.apache.log4j.Logger;
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

    private static Logger logger = Logger.getLogger(TransactionController.class);

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
            handleError(response, "Transaction completed", HttpServletResponse.SC_OK);
        } catch (EmptyResultDataAccessException e) {
            logger.error(e.getMessage());
            handleError(response, "Account not found", HttpServletResponse.SC_BAD_REQUEST);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            handleError(response, "Database unavailable", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Throwable e) {
            logger.error(e.getMessage());
            handleError(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
            handleError(response, "Transaction completed", HttpServletResponse.SC_OK);
        } catch (EmptyResultDataAccessException e) {
            logger.error(e.getMessage());
            handleError(response, "Account not found", HttpServletResponse.SC_BAD_REQUEST);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            handleError(response, "Database unavailable", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (RuntimeTransactionException e) {
            logger.error(e.getMessage());
            handleError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Throwable e) {
            logger.info(e.getMessage());
            handleError(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
            handleError(response, "Transaction completed", HttpServletResponse.SC_OK);
        } catch (EmptyResultDataAccessException e) {
            logger.error(e.getMessage());
            handleError(response, "Account not found", HttpServletResponse.SC_BAD_REQUEST);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            handleError(response, "Database unavailable", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (RuntimeTransactionException e) {
            logger.error(e.getMessage());
            handleError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Throwable e){
            logger.info(e.getMessage());
            handleError(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleError(HttpServletResponse response, String message, int code) throws IOException {
        response.setStatus(code);
        response.getWriter().write(message);
        response.getWriter().flush();
        response.getWriter().close();
    }
}