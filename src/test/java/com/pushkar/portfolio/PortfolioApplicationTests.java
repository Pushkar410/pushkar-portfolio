package com.pushkar.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioApplicationTests {

    private static final Path STORAGE = createTempFilePath();

    private static Path createTempFilePath() {
        try {
            return Files.createTempDirectory("portfolio-test").resolve("messages.jsonl");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("portfolio.contact.storage-path", STORAGE::toString);
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void homepageRendersWithoutLeftoverTemplateSyntax() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Building reliable")))
                .andExpect(content().string(containsString("backend systems.")))
                .andExpect(content().string(containsString("Pushkar R Adiga")))
                .andExpect(content().string(containsString("https://github.com/Pushkar410/url-shortener-sb")))
                .andExpect(content().string(containsString("mailto:pushkar.1si24is072@gmail.com")))
                .andExpect(content().string(not(containsString("th:text"))))
                .andExpect(content().string(not(containsString("${"))));
    }

    @Test
    void staticAssetsAreServed() throws Exception {
        mvc.perform(get("/css/styles.css")).andExpect(status().isOk());
        mvc.perform(get("/js/main.js")).andExpect(status().isOk());
        mvc.perform(get("/favicon.svg")).andExpect(status().isOk());
        mvc.perform(get("/robots.txt")).andExpect(status().isOk());
    }

    @Test
    void resumeIsServedFromTheClasspathOrIsAnHonest404() throws Exception {
        boolean present = new ClassPathResource(HomeController.RESUME_LOCATION).exists();
        if (present) {
            mvc.perform(get("/resume"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        } else {
            mvc.perform(get("/resume")).andExpect(status().isNotFound());
        }
        // The homepage must only link to the resume when it can actually be delivered.
        String link = "href=\"/resume\"";
        mvc.perform(get("/")).andExpect(content().string(present ? containsString(link) : not(containsString(link))));
    }

    @Test
    void validContactMessageIsStoredAndOnlyThenAcknowledged() throws Exception {
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Maya","email":"maya@example.com","message":"Hello there","website":""}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists());

        assertTrue(Files.exists(STORAGE), "message file should exist after a 201");
        String stored = Files.readString(STORAGE);
        assertTrue(stored.contains("maya@example.com") && stored.contains("Hello there"));
    }

    @Test
    void invalidContactMessageIsRejectedWithFieldErrors() throws Exception {
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"","email":"not-an-email","message":""}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.message").exists());
    }

    @Test
    void malformedJsonIsABadRequest() throws Exception {
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content("{nope"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void honeypotSubmissionsAreNotStored() throws Exception {
        long before = Files.exists(STORAGE) ? Files.readAllLines(STORAGE).size() : 0;
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Bot","email":"bot@example.com","message":"spam","website":"http://spam.example"}
                        """))
                .andExpect(status().isCreated());
        long after = Files.exists(STORAGE) ? Files.readAllLines(STORAGE).size() : 0;
        org.junit.jupiter.api.Assertions.assertEquals(before, after);
    }

    @Test
    void unknownPathsReturn404() throws Exception {
        mvc.perform(get("/does-not-exist")).andExpect(status().isNotFound());
    }
}
