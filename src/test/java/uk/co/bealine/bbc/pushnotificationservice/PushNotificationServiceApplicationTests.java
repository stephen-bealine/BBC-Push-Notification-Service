package uk.co.bealine.bbc.pushnotificationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.bealine.bbc.pushnotificationservice.model.AccountRegistration;
import uk.co.bealine.bbc.pushnotificationservice.model.UserAccount;
import uk.co.bealine.bbc.pushnotificationservice.repository.InMemoryAccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
class PushNotificationServiceApplicationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	InMemoryAccountRepository accountRepository;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	void contextLoads() {
		assertThat(mvc).isNotNull();
		assertThat(accountRepository).isNotNull();
	}

	@Nested
	class PostAccountRegistration {

		@Test
		@SneakyThrows
		void givenValidDetails_whenPostRegistration_thenAccountCreated() {
			Instant beforeTest = Instant.now();
			AccountRegistration model = AccountRegistration.builder()
																										 .username("bbcUser1")
																										 .accessToken("someAccessToken")
																										 .build();

			mvc.perform(post("/pns/register")
					.content(objectMapper.writeValueAsString(model))
					.contentType(MediaType.APPLICATION_JSON))
				 .andExpect(status().isCreated())
				 .andExpect(jsonPath("username", is("bbcUser1")))
				 .andExpect(jsonPath("accessToken", is("someAccessToken")))
				 .andExpect(jsonPath("creationTime", notNullValue(String.class)))
				 .andExpect(jsonPath("numOfNotificationsPushed", is(0)))
			;

			Optional<UserAccount> persistedAccount = accountRepository.retrieveAccount("bbcUser1");
			assertThat(persistedAccount).isPresent();
			UserAccount userAccount = persistedAccount.get();
			assertThat(userAccount.getUsername()).isEqualTo("bbcUser1");
			assertThat(userAccount.getAccessToken()).isEqualTo("someAccessToken");
			assertThat(userAccount.getNumOfNotificationsPushed()).hasValue(0);
			assertThat(userAccount.getCreationTime()).isAfterOrEqualTo(beforeTest);
		}
	}
}
