package uk.co.bealine.bbc.pushnotificationservice;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;
import uk.co.bealine.bbc.pushnotificationservice.repository.InMemoryAccountRepository;

@RestController
@RequestMapping("pns")
public class PNSController {

  private final InMemoryAccountRepository accountRepository;

  public PNSController(final InMemoryAccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @PostMapping("register")
  @ResponseStatus(HttpStatus.CREATED)
  public UserAccount registerAccount(@Valid @RequestBody final AccountRegistration accountRegistration){
    return accountRepository.createAccount(accountRegistration);
  }
}
