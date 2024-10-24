package com.dws.challenge.service.impl;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

@Service
public class TransferServiceImpl implements TransferService {

    private final AccountsRepository accountsRepository;
    private final NotificationService notificationService;

    @Autowired
    public TransferServiceImpl(AccountsRepository accountRepository, NotificationService notificationService) {
        this.accountsRepository = accountRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional // Ensures atomicity of the transaction
    public synchronized void transferMoney(Long accountFromId, Long accountToId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // Lock accounts in a consistent order to avoid deadlock
        Account accountFrom;
        Account accountTo;
        if (accountFromId < accountToId) {
            accountFrom = accountsRepository.findById(accountFromId).orElseThrow();
            accountTo = accountsRepository.findById(accountToId).orElseThrow();
        } else {
            accountTo = accountsRepository.findById(accountToId).orElseThrow();
            accountFrom = accountsRepository.findById(accountFromId).orElseThrow();
        }

        // Ensure accounts are not null
        if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance in the account");
        }

        // Perform the transfer
        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));

        accountsRepository.save(accountFrom);
        accountsRepository.save(accountTo);

        // Notify both parties
        notificationService.notify(accountFrom.getAccountId(), "Transferred " + amount + " to account " + accountTo.getAccountId());
        notificationService.notify(accountTo.getAccountId(), "Received " + amount + " from account " + accountFrom.getAccountId());
    }
}

