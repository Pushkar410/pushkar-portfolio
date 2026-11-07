package com.pushkar.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Appends each message as one JSON line. Throws if the write fails, so callers never fake success. */
@Service
public class ContactStore {

    private final Path file;
    private final ObjectMapper mapper;

    public ContactStore(@Value("${portfolio.contact.storage-path:data/messages.jsonl}") String path,
                        ObjectMapper mapper) {
        this.file = Path.of(path).toAbsolutePath();
        this.mapper = mapper;
    }

    public synchronized void save(ContactController.ContactRequest request) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Map<String, String> record = new LinkedHashMap<>();
        record.put("receivedAt", Instant.now().toString());
        record.put("name", request.name().trim());
        record.put("email", request.email().trim());
        record.put("message", request.message().trim());
        String line = mapper.writeValueAsString(record) + System.lineSeparator();
        Files.writeString(file, line, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
