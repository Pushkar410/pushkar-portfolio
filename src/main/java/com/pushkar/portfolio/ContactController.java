package com.pushkar.portfolio;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Receives messages from the contact form and stores them. It answers 201 only after the message
 * has actually been written; any failure is reported as a failure.
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    private final ContactStore store;
    private final ContactRateLimiter limiter;

    public ContactController(ContactStore store, ContactRateLimiter limiter) {
        this.store = store;
        this.limiter = limiter;
    }

    @PostMapping
    public ResponseEntity<ContactResponse> submit(@Valid @RequestBody ContactRequest request,
                                                  HttpServletRequest http) {
        // Humans never see the "website" field. A bot that fills it gets a quiet 201 and nothing is stored.
        if (request.website() != null && !request.website().isBlank()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ContactResponse(SAVED, null));
        }

        if (!limiter.tryAcquire(http.getRemoteAddr())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ContactResponse(
                    "Too many messages from this connection. Please email me directly.", null));
        }

        try {
            store.save(request);
        } catch (IOException e) {
            log.error("Could not store contact message", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ContactResponse(
                    "The message could not be saved. Please email me directly.", null));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ContactResponse(SAVED, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ContactResponse> invalid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), describe(error));
        }
        return ResponseEntity.badRequest().body(new ContactResponse("Check the highlighted fields.", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ContactResponse> unreadable() {
        return ResponseEntity.badRequest().body(new ContactResponse("The request could not be read.", null));
    }

    private static String describe(FieldError error) {
        return switch (String.valueOf(error.getCode())) {
            case "NotBlank" -> switch (error.getField()) {
                case "name" -> "Enter your name.";
                case "email" -> "Enter your email so I can reply.";
                default -> "Write a message.";
            };
            case "Email" -> "That email address does not look right.";
            case "Size" -> "That is too long.";
            default -> "This field is not valid.";
        };
    }

    private static final String SAVED = "Message saved. I will reply by email.";

    public record ContactRequest(
            @NotBlank @Size(max = 80) String name,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Size(max = 2000) String message,
            @Size(max = 200) String website
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ContactResponse(String message, Map<String, String> errors) {}
}
