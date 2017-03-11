package com.kirilov.controller;

import com.kirilov.model.Account;
import com.kirilov.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
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

        if (transactionService.addAccount(account)) {
            handleError(response, "Account created");
        } else {
            handleError(response, "Something went wrong");
        }
    }

    @RequestMapping(value = "/delete", params = {"id"}, method = RequestMethod.POST)
    @ResponseBody
    public void deleteAccount(@RequestParam("id") int id,
                              HttpServletResponse response) throws IOException {
        if (transactionService.deleteAccount(id)) {
            handleError(response, "Account deleted");
        } else {
            handleError(response, "Something went wrong");
        }
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