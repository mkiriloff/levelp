package com.kirilov.controller;

import com.kirilov.model.Account;
import com.kirilov.model.Transaction;
import com.kirilov.model.TransactionBuilder;
import com.kirilov.model.TransactionType;
import com.kirilov.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private TransactionService transactionService;

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
        } catch (EmptyResultDataAccessException e) {
            handleError(response, "Account not found", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Throwable e) {
            handleError(response, "Something went wrong",  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            handleError(response, "Account not found", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Throwable e) {
            handleError(response, "Something went wrong",  HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleError(HttpServletResponse response, String message, int code) throws IOException {
        response.setStatus(code);
        response.getWriter().write(message);
        response.getWriter().flush();
        response.getWriter().close();
    }
}