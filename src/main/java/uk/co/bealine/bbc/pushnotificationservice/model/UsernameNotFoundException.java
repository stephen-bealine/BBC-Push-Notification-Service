package uk.co.bealine.bbc.pushnotificationservice.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameNotFoundException extends RuntimeException {
}
