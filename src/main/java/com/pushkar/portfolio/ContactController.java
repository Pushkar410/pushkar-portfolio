package com.pushkar.portfolio;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @PostMapping
    public ResponseEntity<ContactResponse> submit(@Valid @RequestBody ContactRequest request) {
        // Starter implementation: validates the request and returns a response.
        // Add email delivery or PostgreSQL persistence when you are ready.
        return ResponseEntity.ok(new ContactResponse(
                "Thanks, " + request.name().trim() + ". Your message has been received."
        ));
    }

    public record ContactRequest(
            @NotBlank @Size(max = 80) String name,
            @NotBlank @Email @Size(max = 160) String email,
            @NotBlank @Size(max = 2000) String message
    ) {}

    public record ContactResponse(String message) {}
}
