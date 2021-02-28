package uk.co.bealine.bbc.pushnotificationservice;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;
import uk.co.bealine.bbc.pushnotificationservice.repository.InMemoryAccountRepository;

@RestController
@RequestMapping("accounts")
public class AccountsController {

  private final InMemoryAccountRepository accountRepository;

  public AccountsController(final InMemoryAccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public UserAccount registerAccount(@Valid @RequestBody final AccountRegistration accountRegistration){
    return accountRepository.createAccount(accountRegistration);
  }

  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public List<UserAccount> retrieveAccounts(){
    return accountRepository.retrieveAccounts();
  }
}
