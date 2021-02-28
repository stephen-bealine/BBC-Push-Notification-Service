package uk.co.bealine.bbc.pushnotificationservice.push_bullet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;
import static uk.co.bealine.bbc.pushnotificationservice.push_bullet.PushBulletClient.ACCESS_TOKEN_HEADER;
import static uk.co.bealine.bbc.pushnotificationservice.push_bullet.PushBulletClient.PUSHES_URL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.bealine.bbc.pushnotificationservice.model.PushMessage;

@RestClientTest(PushBulletClient.class)
class PushBulletClientTest {

  @Autowired
  private PushBulletClient client;

  @Autowired
  private MockRestServiceServer server;

  @Value("pushBullet/successResponse.json")
  private Resource successResponse;

  @Value("pushBullet/invalidAccessTokenResponse.json")
  private Resource invalidAccessTokenResponse;

  @Test
  void givenValidAccessKeyAndMessage_whenPushMessage_then(){
    String accessKey = "testAccessKey";
    PushMessage pushMessage = PushMessage.builder().title("testTitle").message("testMessage").build();

    this.server.expect(requestTo(PUSHES_URL))
               .andExpect(method(HttpMethod.POST))
               .andExpect(header(ACCESS_TOKEN_HEADER, accessKey))
               .andExpect(jsonPath("title", is("testTitle")))
               .andExpect(jsonPath("body", is("testMessage")))
               .andRespond(withSuccess(successResponse, MediaType.APPLICATION_JSON));

    boolean result = client.pushMessage(accessKey, pushMessage);

    assertThat(result).isTrue();
  }


  @Test
  void givenInValidAccessKeyAndMessage_whenPushMessage_thenException(){
    String accessKey = "testAccessKey";
    PushMessage pushMessage = PushMessage.builder().title("testTitle").message("testMessage").build();

    this.server.expect(requestTo(PUSHES_URL))
               .andExpect(method(HttpMethod.POST))
               .andExpect(header(ACCESS_TOKEN_HEADER, accessKey))
               .andExpect(jsonPath("title", is("testTitle")))
               .andExpect(jsonPath("body", is("testMessage")))
               .andRespond(withUnauthorizedRequest().body(invalidAccessTokenResponse));

    assertThatThrownBy(()->client.pushMessage(accessKey, pushMessage))
        .isInstanceOf(HttpClientErrorException.Unauthorized.class);
  }

}