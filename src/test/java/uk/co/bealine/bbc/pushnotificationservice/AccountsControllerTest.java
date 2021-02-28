package uk.co.bealine.bbc.pushnotificationservice;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bealine.bbc.pushnotificationservice.TestData.randomRegistration;
import static uk.co.bealine.bbc.pushnotificationservice.TestData.randomUserAccount;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;
import uk.co.bealine.bbc.pushnotificationservice.repository.InMemoryAccountRepository;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

  @Autowired
  private MockMvc mvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private InMemoryAccountRepository accountRepository;

  @Nested
  class PostAccountRegistration {

    @Test
    @SneakyThrows
    void givenValidDetails_whenPostRegistration_thenAccountCreated() {
      AccountRegistration model = randomRegistration().build();
      Instant testTime = Instant.now();
      UserAccount testNewAccount = randomUserAccount().creationTime(testTime).build();
      when(accountRepository.createAccount(any())).thenReturn(testNewAccount);

      mvc.perform(post("/accounts")
          .content(objectMapper.writeValueAsString(model))
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isCreated())
         .andExpect(jsonPath("username", is(testNewAccount.getUsername())))
         .andExpect(jsonPath("accessToken", is(testNewAccount.getAccessToken())))
         .andExpect(jsonPath("creationTime", is(testTime.toString())))
         .andExpect(jsonPath("numOfNotificationsPushed", is(0)))
      ;

      verify(accountRepository, times(1)).createAccount(model);
    }

    @Test
    @SneakyThrows
    void givenInValidDetails_whenPostRegistration_thenAccountNotCreatedAndBadRequest() {
      AccountRegistration model = AccountRegistration.builder()
                                                     .username(null)
                                                     .accessToken("someAccessToken")
                                                     .build();
      mvc.perform(post("/accounts")
          .content(objectMapper.writeValueAsString(model))
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest())
      ;

      verifyNoInteractions(accountRepository);
    }

    @Test
    @SneakyThrows
    void givenNoDetails_whenPostRegistration_thenAccountNotCreatedAndBadRequest() {
      mvc.perform(post("/accounts")
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest())
      ;

      verifyNoInteractions(accountRepository);
    }
  }

  @Nested
  class GetAllAccounts {

    @Test
    @SneakyThrows
    void givenNoAccounts_whenGetAllAccounts_thenEmptyListReturned() {
      mvc.perform(get("/accounts")
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$", empty()));

      verify(accountRepository, times(1)).retrieveAccounts();
    }

    @Test
    @SneakyThrows
    void givenAccounts_whenGetAllAccounts_thenListOfAccountsReturned() {
      Instant testTime1 = Instant.now();
      UserAccount account1 = randomUserAccount().creationTime(testTime1).build();
      Instant testTime2 = Instant.now().minusSeconds(120);
      UserAccount account2 = randomUserAccount().creationTime(testTime2).build();

      when(accountRepository.retrieveAccounts()).thenReturn(List.of(account1, account2));

      mvc.perform(get("/accounts")
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$", hasSize(2)))
         .andExpect(jsonPath("$[0].username", is(account1.getUsername())))
         .andExpect(jsonPath("$[0].accessToken", is(account1.getAccessToken())))
         .andExpect(jsonPath("$[0].creationTime", is(testTime1.toString())))
         .andExpect(jsonPath("$[0].numOfNotificationsPushed", is(0)))
         .andExpect(jsonPath("$[1].username", is(account2.getUsername())))
         .andExpect(jsonPath("$[1].accessToken", is(account2.getAccessToken())))
         .andExpect(jsonPath("$[1].creationTime", is(testTime2.toString())))
         .andExpect(jsonPath("$[1].numOfNotificationsPushed", is(0)));

      verify(accountRepository, times(1)).retrieveAccounts();
    }
  }
}