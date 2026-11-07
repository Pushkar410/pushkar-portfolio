package com.pushkar.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Year;

@Controller
public class HomeController {

    /** The resume lives on the classpath (src/main/resources/resume), never at a machine-specific path. */
    static final String RESUME_LOCATION = "resume/Pushkar_R_Adiga_Resume.pdf";
    private static final String RESUME_DOWNLOAD_NAME = "Pushkar_R_Adiga_Resume.pdf";

    private final Resource resume = new ClassPathResource(RESUME_LOCATION);
    private final String siteUrl;

    public HomeController(@Value("${portfolio.site-url:}") String siteUrl) {
        this.siteUrl = siteUrl;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Pushkar R Adiga, Backend Developer");
        model.addAttribute("description",
                "Pushkar R Adiga is a backend developer and software engineering student in Bengaluru, "
                        + "working with Java, Spring Boot, PostgreSQL and REST APIs.");
        model.addAttribute("siteUrl", siteUrl);
        model.addAttribute("name", "Pushkar R Adiga");
        model.addAttribute("location", "Bengaluru, India");
        model.addAttribute("email", "pushkar.1si24is072@gmail.com");
        model.addAttribute("github", "https://github.com/Pushkar410");
        model.addAttribute("linkedin", "https://www.linkedin.com/in/pushkar-r-adiga");
        model.addAttribute("takeuforward", "https://takeuforward.org/profile/pushkarradiga");
        model.addAttribute("resumeAvailable", resume.exists());
        model.addAttribute("year", Year.now().getValue());
        return "index";
    }

    @GetMapping("/resume")
    public ResponseEntity<Resource> resume() {
        if (!resume.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(RESUME_DOWNLOAD_NAME).build().toString())
                .body(resume);
    }
}
