package com.pushkar.portfolio;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class HomeController {

    private static final String RESUME_FILENAME = "Pushkar_R_Adiga_Resume.pdf";
    private static final String UPLOADED_RESUME_PATH =
            "C:/Users/KILLE/.gemini/antigravity/brain/28af692c-93b1-4c3f-bcf2-39d7c8c5d5b9/.user_uploaded/media_1794030575523_c1f82c34.pdf";

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Pushkar R Adiga — Data Science & Software Engineering");
        model.addAttribute("name", "Pushkar R Adiga");
        model.addAttribute("email", "pushkar.1si24is072@gmail.com");
        model.addAttribute("phone", "+91-8073722418");
        model.addAttribute("location", "Bengaluru, Karnataka, India");
        model.addAttribute("college", "Siddaganga Institute of Technology, Tumakuru");
        model.addAttribute("cgpa", "8.69 / 10");
        model.addAttribute("github", "https://github.com/Pushkar410");
        model.addAttribute("linkedin", "https://www.linkedin.com/in/pushkar-r-adiga");
        model.addAttribute("takeuforward", "https://takeuforward.org/profile/pushkarradiga");
        return "index";
    }

    @GetMapping(value = {"/resume", "/" + RESUME_FILENAME})
    @ResponseBody
    public ResponseEntity<Resource> downloadResume() {
        try {
            // 1. Try local uploaded path
            Path uploadedPath = Path.of(UPLOADED_RESUME_PATH);
            if (Files.exists(uploadedPath)) {
                byte[] data = Files.readAllBytes(uploadedPath);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + RESUME_FILENAME + "\"")
                        .body(new ByteArrayResource(data));
            }

            // 2. Try static classpath resource
            ClassPathResource classPathResource = new ClassPathResource("static/" + RESUME_FILENAME);
            if (classPathResource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + RESUME_FILENAME + "\"")
                        .body(classPathResource);
            }

            // 3. Try project directory relative path
            File localFile = new File("src/main/resources/static/" + RESUME_FILENAME);
            if (localFile.exists()) {
                byte[] data = Files.readAllBytes(localFile.toPath());
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + RESUME_FILENAME + "\"")
                        .body(new ByteArrayResource(data));
            }
        } catch (IOException ignored) {}

        return ResponseEntity.notFound().build();
    }
}
