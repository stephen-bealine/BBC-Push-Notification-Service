package uk.co.bealine.bbc.pushnotificationservice;

import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bealine.bbc.pushnotificationservice.model.PushFailedException;
import uk.co.bealine.bbc.pushnotificationservice.model.PushMessage;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;
import uk.co.bealine.bbc.pushnotificationservice.model.UsernameNotFoundException;
import uk.co.bealine.bbc.pushnotificationservice.push_bullet.PushBulletClient;
import uk.co.bealine.bbc.pushnotificationservice.repository.InMemoryAccountRepository;

@RestController
@RequestMapping("push")
@Slf4j
public class PushMessageController {

  private final InMemoryAccountRepository accountRepository;
  private final PushBulletClient pushBulletClient;

  public PushMessageController(final InMemoryAccountRepository accountRepository,
      PushBulletClient pushBulletClient) {
    this.accountRepository = accountRepository;
    this.pushBulletClient = pushBulletClient;
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public void pushMessage(@Valid @RequestBody final PushMessage pushMessage) {
    Optional<UserAccount> userAccount = accountRepository.retrieveAccount(pushMessage.getUsername());
    UserAccount userAccount1 = userAccount.orElseThrow(UsernameNotFoundException::new);

    try {
      pushBulletClient.pushMessage(userAccount1.getAccessToken(), pushMessage);
      userAccount1.incrementPushCount();
    } catch (Exception e){
      log.error("Failed to push message", e);
      throw new PushFailedException();
    }
  }
}
