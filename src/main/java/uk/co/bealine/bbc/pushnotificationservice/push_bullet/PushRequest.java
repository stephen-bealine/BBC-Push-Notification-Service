package uk.co.bealine.bbc.pushnotificationservice.push_bullet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@AllArgsConstructor
public class PushRequest {
  public static final String MSG_TYPE = "note";

  String title;
  String body;
  @Builder.Default
  String type = MSG_TYPE;
}
