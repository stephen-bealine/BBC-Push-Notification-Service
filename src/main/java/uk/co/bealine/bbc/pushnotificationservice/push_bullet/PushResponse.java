package uk.co.bealine.bbc.pushnotificationservice.push_bullet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@AllArgsConstructor
public class PushResponse {
  Boolean active;
  String title;
  String body;
}
