package uk.co.bealine.bbc.pushnotificationservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.bealine.bbc.pushnotificationservice.TestData.randomRegistration;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;

class InMemoryAccountRepositoryTest {

  private InMemoryAccountRepository accountRepository;

  @BeforeEach
  void setup(){
    accountRepository = new InMemoryAccountRepository();
  }

  @Nested
  class CreateAccount {

    @Test
    void givenAccountRegistration_whenCreateAccount_thenAccountCreatedAndReturned() {
      Instant beforeTest = Instant.now();
      AccountRegistration accountRegistration = AccountRegistration.builder()
                                                                   .username("bbcUser1")
                                                                   .accessToken("someAccessToken")
                                                                   .build();

      UserAccount actual = accountRepository.createAccount(accountRegistration);

      assertThat(actual).isNotNull();
      assertThat(actual.getUsername()).isEqualTo("bbcUser1");
      assertThat(actual.getAccessToken()).isEqualTo("someAccessToken");
      assertThat(actual.getNumOfNotificationsPushed()).hasValue(0);
      assertThat(actual.getCreationTime()).isAfterOrEqualTo(beforeTest);
    }
  }

  @Nested
  class RetrieveAccount {
    @Test
    void givenCreatedAccount_whenRetrieveAccount_thenAccountDetailsReturned() {
      AccountRegistration accountRegistration = randomRegistration().username("bbcUser1").build();
      UserAccount createdAccount = accountRepository.createAccount(accountRegistration);

      Optional<UserAccount> retrieveAccount = accountRepository.retrieveAccount("bbcUser1");

      assertThat(retrieveAccount).containsSame(createdAccount);
    }

    @Test
    void givenMultipleAccounts_whenRetrieveAccount_thenCorrespondingAccountDetailsReturned() {
      accountRepository.createAccount(randomRegistration().build());
      AccountRegistration accountRegistration = randomRegistration().username("bbcUser1").build();
      UserAccount createdAccount = accountRepository.createAccount(accountRegistration);

      Optional<UserAccount> retrieveAccount = accountRepository.retrieveAccount("bbcUser1");

      assertThat(retrieveAccount).containsSame(createdAccount);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "unknownUsername")
    void givenNullOrUnknownUsername_whenRetrieveAccount_thenNoDetailsReturned(String invalidUsername) {
      Optional<UserAccount> retrieveAccount = accountRepository.retrieveAccount(invalidUsername);
      assertThat(retrieveAccount).isNotPresent();
    }
  }
}