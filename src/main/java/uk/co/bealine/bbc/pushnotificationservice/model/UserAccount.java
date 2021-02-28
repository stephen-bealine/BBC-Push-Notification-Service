package uk.co.bealine.bbc.pushnotificationservice.model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserAccount {

  private static final int INITIAL_PUSH_COUNT = 0;

  private final String username;
  private final String accessToken;
  private final Instant creationTime;
  @Builder.Default
  private final AtomicInteger numOfNotificationsPushed = new AtomicInteger(INITIAL_PUSH_COUNT);

  public void incrementPushCount() {
    numOfNotificationsPushed.incrementAndGet();
  }
}
