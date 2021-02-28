package uk.co.bealine.bbc.pushnotificationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bealine.bbc.pushnotificationservice.TestData.randomUserAccount;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.bealine.bbc.pushnotificationservice.model.PushMessage;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;
import uk.co.bealine.bbc.pushnotificationservice.push_bullet.PushBulletClient;
import uk.co.bealine.bbc.pushnotificationservice.repository.InMemoryAccountRepository;

@WebMvcTest(PushMessageController.class)
class PushMessageControllerTest {

  @Autowired
  private MockMvc mvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockBean
  private InMemoryAccountRepository accountRepository;
  @MockBean
  private PushBulletClient pushBulletClient;

  @Nested
  class PutPushMessage {

    @Test
    @SneakyThrows
    void givenValidMessage_whenPutPushMessage_thenMessageSentAndOK() {
      PushMessage pushMessage = PushMessage.builder()
                                           .username("testUsername")
                                           .title("testTitle")
                                           .message("testMessage")
                                           .build();
      UserAccount testNewAccount = randomUserAccount().username("testUsername").accessToken("testAccessToken").build();
      when(accountRepository.retrieveAccount("testUsername")).thenReturn(Optional.of(testNewAccount));

      mvc.perform(put("/push")
          .content(objectMapper.writeValueAsString(pushMessage))
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isOk());

      verify(accountRepository, times(1)).retrieveAccount("testUsername");
      verify(pushBulletClient, times(1)).pushMessage("testAccessToken", pushMessage);
      assertThat(testNewAccount.getNumOfNotificationsPushed()).hasValue(1);
    }

    @Test
    @SneakyThrows
    void givenUnknownUsername_whenPutPushMessage_thenNothingPushedAndBadRequest() {
      PushMessage pushMessage = PushMessage.builder()
                                           .username("testUsername")
                                           .title("testTitle")
                                           .message("testMessage")
                                           .build();
      when(accountRepository.retrieveAccount("testUsername")).thenReturn(Optional.empty());

      mvc.perform(put("/push")
          .content(objectMapper.writeValueAsString(pushMessage))
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());

      verify(accountRepository, times(1)).retrieveAccount("testUsername");
      verifyNoInteractions(pushBulletClient);
    }

    @Test
    @SneakyThrows
    void givenPushClientFails_whenPutPushMessage_thenBadRequest() {
      PushMessage pushMessage = PushMessage.builder()
                                           .username("testUsername")
                                           .title("testTitle")
                                           .message("testMessage")
                                           .build();
      UserAccount testNewAccount = randomUserAccount().username("testUsername").accessToken("testAccessToken").build();
      when(accountRepository.retrieveAccount("testUsername")).thenReturn(Optional.of(testNewAccount));
      when(pushBulletClient.pushMessage("testAccessToken", pushMessage)).thenThrow(new RuntimeException("blah"));

      mvc.perform(put("/push")
          .content(objectMapper.writeValueAsString(pushMessage))
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());

      verify(accountRepository, times(1)).retrieveAccount("testUsername");
      verify(pushBulletClient, times(1)).pushMessage("testAccessToken", pushMessage);
      assertThat(testNewAccount.getNumOfNotificationsPushed()).hasValue(0);
    }

    @ParameterizedTest
    @MethodSource("uk.co.bealine.bbc.pushnotificationservice.PushMessageControllerTest#invalidPushMessage")
    @SneakyThrows
    void givenInvalidMessage_whenPutPushMessage_thenNothingPushedAndBadRequest(PushMessage pushMessage) {
      mvc.perform(put("/push")
          .content(objectMapper.writeValueAsString(pushMessage))
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());

      verifyNoInteractions(accountRepository);
      verifyNoInteractions(pushBulletClient);
    }

    @Test
    @SneakyThrows
    void givenNoMessage_whenPutPushMessage_thenNothingPushedAndBadRequest() {
      mvc.perform(put("/push")
          .contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());

      verifyNoInteractions(accountRepository);
      verifyNoInteractions(pushBulletClient);
    }
  }

  private static List<PushMessage> invalidPushMessage() {
    return List.of(
        PushMessage.builder().username(null).title("testTitle").message("testMessage").build(),
        PushMessage.builder().username("username").title(null).message("testMessage").build(),
        PushMessage.builder().username("username").title("testTitle").message(null).build(),
        PushMessage.builder().username(null).title(null).message(null).build()
    );
  }
}