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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("transactionService")
public class TransactionService {
    @Autowired
    private AccountDao accountDao;
    private final Set<Integer> processIdList = new HashSet<Integer>();
    private static Logger logger = Logger.getLogger(TransactionController.class);

    private final Set<Integer> process = Collections.synchronizedSet(new HashSet<Integer>());

    private void addToProcessIdList(Transaction transaction) {
        synchronized (processIdList) {
            while (processIdList.containsAll(transaction.getAllId())) {
                try {
                    processIdList.wait();
                } catch (InterruptedException e) {
                    logger.error(e.getStackTrace());
                }
            }
            processIdList.addAll(transaction.getAllId());
            logger.info(TransactionService.class.getSimpleName() + ": addToProcessIdList id: " + transaction.getAllId());
        }
    }

    private void removeFromProcessIdList(Transaction transaction) {
        synchronized (processIdList) {
            processIdList.removeAll(transaction.getAllId());
            logger.info(TransactionService.class.getSimpleName() + ": removeFromProcessIdList id: " + transaction.getAllId());
            processIdList.notifyAll();

        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void addAccount(Account account) throws RuntimeException {
        if (accountDao.add(account)) {
            logger.info(TransactionService.class.getSimpleName() + ": addAccount: " + account.getFirstname() + ", " + account.getLastname());

        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void deleteAccount(Transaction transaction) throws RuntimeException {
        accountDao.findbyId(transaction.getId());
        if (accountDao.delete(transaction.getId())) {
            logger.info(TransactionService.class.getSimpleName() + ": deleteAccount id: " + transaction.getId());
        }
    }

    public List<Account> getAllAccount() throws RuntimeException {
        List<Account> accounts = accountDao.getAllAccount();
        accounts.sort(Account::compareTo);
        return accounts;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void debitBalanceAccount(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction);
        try {
            Account processAccount = accountDao.findbyId(transaction.getId());
            if (processAccount.getBalance() - transaction.getSum() >= 0) {
                processAccount.setBalance(processAccount.getBalance() - transaction.getSum());
                accountDao.updateBalance(processAccount);
                logger.info(TransactionService.class.getSimpleName() + ": debitBalanceAccount id: " + transaction.getId() + " Balance: " + processAccount.getBalance());
            } else {
                throw new RuntimeTransactionException("On account id " + transaction.getId() + " has insufficient funds");
            }
        } finally {
            removeFromProcessIdList(transaction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void addedBalanceAccount(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction);
        try {
            Account processAccount = accountDao.findbyId(transaction.getId());
            processAccount.setBalance(processAccount.getBalance() + transaction.getSum());
            accountDao.updateBalance(processAccount);
            logger.info(TransactionService.class.getSimpleName() + ": addedBalanceAccount id: " + transaction.getId() + " Balance: " + processAccount.getBalance());
        } finally {
            removeFromProcessIdList(transaction);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void doTransfer(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction);
        try {
            Account fromAccount = accountDao.findbyId(transaction.getFromId());
            Account toAccount = accountDao.findbyId(transaction.getId());
            if (fromAccount.getBalance() - transaction.getSum() >= 0.0) {
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getSum());
                toAccount.setBalance(toAccount.getBalance() + transaction.getSum());
                accountDao.updateBalance(fromAccount);
                accountDao.updateBalance(toAccount);
                logger.info(TransactionService.class.getSimpleName() + ": " +
                        "doTransfer id: " + fromAccount.getid() + " > " + toAccount.getid() +
                        ", Balance: " + fromAccount.getBalance() + ", " + toAccount.getBalance());
            } else {
                throw new RuntimeTransactionException("On account id " + transaction.getFromId() + " has insufficient funds");
            }
        } finally {
            removeFromProcessIdList(transaction);
        }
    }
}
