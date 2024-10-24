package com.dws.challenge.service;

import com.dws.challenge.domain.Account;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public interface NotificationService {

  void notifyAboutTransfer(Account account, String transferDescription);

  void notify(@NotNull @NotEmpty Long accountId, String s);
}
