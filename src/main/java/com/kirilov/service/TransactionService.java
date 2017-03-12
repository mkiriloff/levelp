package com.kirilov.service;

import com.kirilov.dao.AccountDao;
import com.kirilov.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    private void addToProcessIdList(Integer id) {
        synchronized (processIdList) {
            while (processIdList.contains(id)) {
                try {
                    processIdList.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            processIdList.add(id);
        }
    }

    private void removeFromProcessIdList(Integer id) {
        synchronized (processIdList) {
            processIdList.remove(id);
            processIdList.notifyAll();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void addAccount(Account account) throws RuntimeException {
        accountDao.add(account);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void deleteAccount(Transaction transaction) throws RuntimeException {
        accountDao.delete(transaction.getId());
    }

    public List<Account> getAllAccount() throws RuntimeException {
        return accountDao.getAllAccount();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void debitBalanceAccount(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction.getId());
        try {
            Account processAccount = accountDao.findbyId(transaction.getId());
            if (processAccount.getBalance() - transaction.getSum() >= 0) {
                processAccount.setBalance(processAccount.getBalance() - transaction.getSum());
                accountDao.updateBalance(processAccount);
            } else {
                throw new LevelpTransactionException("On account " + transaction.getId() + " has insufficient funds");
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
        } finally {
            removeFromProcessIdList(transaction.getId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void doTransfer(Transaction transaction) throws RuntimeException {
        addToProcessIdList(transaction.getId());
        addToProcessIdList(transaction.getFromId());
        try {
            Account fromAccount = accountDao.findbyId(transaction.getFromId());
            Account toAccount = accountDao.findbyId(transaction.getId());
            if (fromAccount.getBalance() - transaction.getSum() >= 0) {
                fromAccount.setBalance(fromAccount.getBalance() - transaction.getSum());
                toAccount.setBalance(toAccount.getBalance() + transaction.getSum());
            } else {
                throw new LevelpTransactionException("On account" + transaction.getFromId() + " has insufficient funds");
            }
        } finally {
            removeFromProcessIdList(transaction.getId());
            removeFromProcessIdList(transaction.getFromId());
        }
    }
}
