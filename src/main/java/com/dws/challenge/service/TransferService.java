package com.dws.challenge.service;

import java.math.BigDecimal;

public interface TransferService {
    void transferMoney(Long accountFromId, Long accountToId, BigDecimal amount);
}