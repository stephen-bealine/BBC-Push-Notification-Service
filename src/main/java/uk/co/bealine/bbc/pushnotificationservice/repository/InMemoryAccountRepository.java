package uk.co.bealine.bbc.pushnotificationservice.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;

@Service
public class InMemoryAccountRepository {

  private final Map<String, UserAccount> userAccounts = new HashMap<>();

  public UserAccount createAccount(final AccountRegistration accountRegistration){
    UserAccount newAccount = UserAccount.builder()
                                        .username(accountRegistration.getUsername())
                                        .accessToken(accountRegistration.getAccessToken())
                                        .creationTime(Instant.now())
                                        .build();
    userAccounts.put(accountRegistration.getUsername(), newAccount);
    return newAccount;
  }

  public Optional<UserAccount> retrieveAccount(String username) {
    return Optional.ofNullable(userAccounts.get(username));
  }

  public List<UserAccount> retrieveAccounts() {
    ArrayList<UserAccount> accounts = new ArrayList<>(this.userAccounts.values());
    accounts.sort(Comparator.comparing(UserAccount::getUsername));
    return accounts;
  }

  public void clearAccounts() {
    userAccounts.clear();
  }
}
