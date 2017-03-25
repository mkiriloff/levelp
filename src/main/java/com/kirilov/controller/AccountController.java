package com.kirilov.controller;

import com.kirilov.model.Account;
import com.kirilov.model.Transaction;
import com.kirilov.model.TransactionBuilder;
import com.kirilov.model.TransactionType;
import com.kirilov.service.TransactionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
public class AccountController {

    @Autowired
    private TransactionService transactionService;

    private static Logger logger = Logger.getLogger(AccountController.class);

    @RequestMapping(value = "/add", params = {"firstname", "lastname"}, method = RequestMethod.POST)
    @ResponseBody
    public void addAccount(@RequestParam("firstname") String firstName,
                           @RequestParam("lastname") String lastName,
                           HttpServletResponse response) throws IOException {
        Account account = new Account();
        account.setFirstname(firstName);
        account.setLastname(lastName);

        try {
            transactionService.addAccount(account);
            handleError(response, "Account created", HttpServletResponse.SC_OK);
        } catch (Throwable e) {
            logger.info(e.getMessage());
            handleError(response, "Something went wrong", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/delete", params = {"id"}, method = RequestMethod.POST)
    @ResponseBody
    public void deleteAccount(@RequestParam("id") int id,
                              HttpServletResponse response) throws IOException {
        Transaction transaction = new TransactionBuilder()
                .setTransactionType(TransactionType.REMOVEACCOUNT)
                .setId(id)
                .build();
        try {
            transactionService.deleteAccount(transaction);
            handleError(response, "Account deleted", HttpServletResponse.SC_OK);
        } catch (EmptyResultDataAccessException e) {
            handleError(response, "Account" + transaction.getId() + "not found", HttpServletResponse.SC_BAD_REQUEST);
            logger.info(e.getMessage());
        } catch (Throwable e) {
            handleError(response, "Something went wrong", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.info(e.getMessage());
        }
    }

    private void handleError(HttpServletResponse response, String message, int code) throws IOException {
        response.setStatus(code);
        response.getWriter().write(message);
        response.getWriter().flush();
        response.getWriter().close();
    }
}