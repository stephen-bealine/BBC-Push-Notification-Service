package uk.co.bealine.bbc.pushnotificationservice.push_bullet;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.co.bealine.bbc.pushnotificationservice.model.PushMessage;

@Service
public class PushBulletClient {

  public static final String PUSHES_URL = "https://api.pushbullet.com/v2/pushes";
  public static final String ACCESS_TOKEN_HEADER = "Access-Token";

  private final RestTemplate restTemplate;

  public PushBulletClient(RestTemplateBuilder restTemplateBuilder) {
    restTemplate = restTemplateBuilder.build();
  }

  public boolean pushMessage(String accessToken, PushMessage pushMessage) {
    PushRequest pushRequest = PushRequest
        .builder()
        .title(pushMessage.getTitle())
        .body(pushMessage.getMessage())
        .build();
    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCESS_TOKEN_HEADER, accessToken);
    HttpEntity<PushRequest> request = new HttpEntity<>(pushRequest, headers);
    restTemplate.postForObject(PUSHES_URL, request, PushResponse.class);
    return true;
  }
}
