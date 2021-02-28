package uk.co.bealine.bbc.pushnotificationservice;

import static org.hamcrest.CoreMatchers.is;
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
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;
import uk.co.bealine.bbc.pushnotificationservice.repository.InMemoryAccountRepository;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PNSController.class)
class PNSControllerTest {

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

      mvc.perform(post("/pns/register")
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
      mvc.perform(post("/pns/register")
          .content(objectMapper.writeValueAsString(model))
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest())
      ;

      verifyNoInteractions(accountRepository);
    }

    @Test
    @SneakyThrows
    void givenNoDetails_whenPostRegistration_thenAccountNotCreatedAndBadRequest() {
      mvc.perform(post("/pns/register")
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest())
      ;

      verifyNoInteractions(accountRepository);
    }
  }
}