package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.impl.TransferServiceImpl;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransferServiceTest {

    @Mock
    private AccountsRepository accountRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    public void testTransferMoney() {
        Account accountFrom = new Account(1L, new BigDecimal("100.00"));
        Account accountTo = new Account(2L, new BigDecimal("50.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountTo));

        transferService.transferMoney(1L, 2L, new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), accountFrom.getBalance());
        assertEquals(new BigDecimal("80.00"), accountTo.getBalance());

        verify(notificationService).notify(1L, "Transferred 30.00 to account 2");
        verify(notificationService).notify(2L, "Received 30.00 from account 1");
    }

    @Test(expected = InsufficientFundsException.class)
    public void testInsufficientFunds() {
        Account accountFrom = new Account(1L, new BigDecimal("20.00"));
        Account accountTo = new Account(2L, new BigDecimal("50.00"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountTo));

        transferService.transferMoney(1L, 2L, new BigDecimal("30.00"));
    }
}

