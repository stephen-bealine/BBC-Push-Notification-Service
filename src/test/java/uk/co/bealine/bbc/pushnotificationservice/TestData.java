package uk.co.bealine.bbc.pushnotificationservice;

import java.time.Instant;
import org.apache.commons.lang3.RandomStringUtils;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;

public class TestData {
  public static AccountRegistration.AccountRegistrationBuilder randomRegistration(){
    return AccountRegistration.builder()
                              .username(RandomStringUtils.randomAlphanumeric(10))
                              .accessToken(RandomStringUtils.randomAlphanumeric(15));
  }

  public static UserAccount.UserAccountBuilder randomUserAccount(){
    return UserAccount.builder()
                      .username(RandomStringUtils.randomAlphanumeric(10))
                      .accessToken(RandomStringUtils.randomAlphanumeric(15))
                      .creationTime(Instant.now());
  }
}
