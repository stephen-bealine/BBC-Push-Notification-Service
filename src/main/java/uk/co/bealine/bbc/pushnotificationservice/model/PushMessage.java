package uk.co.bealine.bbc.pushnotificationservice.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
public class PushMessage {

  @NotBlank
  String username;
  @NotBlank
  String title;
  @NotBlank
  String message;
}
