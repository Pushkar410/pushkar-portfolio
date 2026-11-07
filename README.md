# Pushkar R Adiga: portfolio

Personal site built with Java 21, Spring Boot 3.5, Spring MVC, Thymeleaf, Bean Validation and Maven.
Frontend is plain HTML, CSS and vanilla JavaScript. There is no Node.js, npm or separate frontend server.

## Run

```bash
mvn spring-boot:run          # http://localhost:8080
THYMELEAF_CACHE=false mvn spring-boot:run   # while editing templates
mvn clean verify             # compile and run tests
java -jar target/pushkar-portfolio-1.0.0.jar
```

## Resume

Add your PDF at `src/main/resources/resume/Pushkar_R_Adiga_Resume.pdf`.
It is served at `/resume` from the classpath. While the file is missing, the Resume links are hidden
and `/resume` returns 404.

## Contact form

`POST /api/contact` validates the message and appends it as one JSON line to the file set by
`CONTACT_STORAGE_PATH` (default `data/messages.jsonl`). The API returns `201` only after the write
succeeds, `400` with per-field errors for invalid input, `429` after 5 messages per hour from one
address, and `503` if the file cannot be written. The page always shows a direct email link too.

On hosts with an ephemeral filesystem, point `CONTACT_STORAGE_PATH` at a persistent volume, or read
the file before redeploying.

## Configuration

| Variable | Purpose | Default |
| --- | --- | --- |
| `PORT` | HTTP port | `8080` |
| `PORTFOLIO_SITE_URL` | Public URL for canonical and Open Graph tags | empty (tags omitted) |
| `CONTACT_STORAGE_PATH` | Where messages are stored | `data/messages.jsonl` |
| `THYMELEAF_CACHE` | Set `false` while editing templates | `true` |

## Layout

```text
src/main/java/com/pushkar/portfolio/
  PortfolioApplication.java
  HomeController.java        homepage and /resume
  ContactController.java     POST /api/contact
  ContactStore.java          appends messages to disk
  ContactRateLimiter.java    5 messages per hour per address
src/main/resources/
  templates/index.html       the page
  templates/error.html       404 and error page
  static/css/styles.css      tokens first, then sections in page order
  static/js/main.js          contact form only
  resume/                    resume PDF goes here
```

## Fonts

Newsreader and Geist load from Google Fonts with system fallbacks. To remove the third-party request,
download the font files into `static/fonts` and replace the `<link>` with `@font-face` rules.
