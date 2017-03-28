package com.kirilov.service;

import com.kirilov.controller.TransactionController;
import com.kirilov.dao.AccountDao;
import com.kirilov.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("transactionService")
public class TransactionService {
    @Autowired
    private AccountDao accountDao;
    private final Set<Integer> processIdList = new HashSet<Integer>();
    private static Logger logger = Logger.getLogger(TransactionController.class);

    private void addToProcessIdList(Integer id) {
        synchronized (processIdList) {
            while (processIdList.contains(id)) {
                try {
                    processIdList.wait();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
            processIdList.add(id);
            logger.info("Account id: " + id + " added to process list");
        }
    }

    private void removeFromProcessIdList(Integer id) {
        synchronized (processIdList) {
            processIdList.remove(id);
            logger.info("Account deleted id: " + id + " from process list");
            processIdList.notifyAll();

        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void addAccount(Account account) throws RuntimeException {
        if (accountDao.add(account)) {
            logger.info("Created new account - " + account.getFirstname() + " " + account.getLastname());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void deleteAccount(Transaction transaction) throws RuntimeException {
        accountDao.findbyId(transaction.getId());
        if (accountDao.delete(transaction.getId())) {
            logger.info("Account deleted id " + transaction.getId());
        }
    }

    public List<Account> getAllAccount() throws RuntimeException {
        List<Account> accounts = accountDao.getAllAccount();
        accounts.sort(Account::compareTo);
        return accounts;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void debitBalanceAccount(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction.getId());
        try {
            Account processAccount = accountDao.findbyId(transaction.getId());
            if (processAccount.getBalance() - transaction.getSum() >= 0) {
                processAccount.setBalance(processAccount.getBalance() - transaction.getSum());
                accountDao.updateBalance(processAccount);
                logger.info("Debit sum " + transaction.getSum() + ", from Account id " + transaction.getId() + ". New balance: " + processAccount.getBalance());
            } else {
                throw new RuntimeTransactionException("On account id " + transaction.getId() + " has insufficient funds");
            }
        } finally {
            removeFromProcessIdList(transaction.getId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void addedBalanceAccount(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction.getId());
        try {
            Account processAccount = accountDao.findbyId(transaction.getId());
            processAccount.setBalance(processAccount.getBalance() + transaction.getSum());
            accountDao.updateBalance(processAccount);
            logger.info("Added sum " + transaction.getSum() + ", from Account id " + transaction.getId() + ". New balance: " + processAccount.getBalance());
        } finally {
            removeFromProcessIdList(transaction.getId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void doTransfer(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction.getFromId());
        addToProcessIdList(transaction.getId());
        try {
            Account fromAccount = accountDao.findbyId(transaction.getFromId());
            Account toAccount = accountDao.findbyId(transaction.getId());
            if (fromAccount.getBalance() - transaction.getSum() >= 0.0) {
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getSum());
                toAccount.setBalance(toAccount.getBalance() + transaction.getSum());
                accountDao.updateBalance(fromAccount);
                accountDao.updateBalance(toAccount);
                logger.info("Transaction done," + " id: " + fromAccount.getid() + ", new balance " + fromAccount.getBalance() +
                        " and id: " + toAccount.getid() + ", new balance " + toAccount.getBalance());
            } else {
                throw new RuntimeTransactionException("On account id " + transaction.getFromId() + " has insufficient funds");
            }
        } finally {
            removeFromProcessIdList(transaction.getFromId());
            removeFromProcessIdList(transaction.getId());

        }
    }
}
