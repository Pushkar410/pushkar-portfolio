# Pushkar R Adiga — Software Engineering & Data Science Portfolio

A minimalist, high-aesthetic dark portfolio inspired by [mhndra.com](https://mhndra.com), custom-built for **Pushkar R Adiga** using **Java 21, Spring Boot 3.5+, Thymeleaf, HTML5, CSS3, and Vanilla JavaScript**.

---

## Profile Summary

- **Name:** Pushkar R Adiga
- **Education:** Bachelor of Engineering in Information Science Engineering (Class of 2028), Siddaganga Institute of Technology, Tumakuru
- **CGPA:** 8.69 / 10
- **Location:** Bengaluru, Karnataka, India
- **Core Focus:** Full Data Lifecycle (EDA, Feature Engineering, Classification/Regression, Tableau) + Production Backend Systems (Java 21, Spring Boot, PostgreSQL, Docker) + Generative AI
- **Key Recognition:** Winner, Smart India Hackathon (SIH) Internal Round; Member, DeCoders Coding Club; Head of Props, Aurora Theatrical Club

---

## Featured Work & Architecture

### 1. URL Shortener Service
- **Category:** Distributed Systems & Backend Engineering
- **Stack:** Java 21, Spring Boot, PostgreSQL, REST APIs, Docker, Hashing Algorithms
- **GitHub:** [Pushkar410/url-shortener-sb](https://github.com/Pushkar410/url-shortener-sb)
- **Features:** Scalable architecture, REST API contracts, normalized PostgreSQL schema with B-tree indexing for fast lookups, multi-stage Docker containerization.

### 2. Text-to-SQL Chatbot
- **Category:** Generative AI & Natural Language Processing
- **Stack:** Generative AI, Python, SQL (PostgreSQL), LLM Tooling, Prompt Engineering
- **GitHub:** [Pushkar410](https://github.com/Pushkar410)
- **Features:** Translates natural language questions into dialect-accurate SQL queries using schema-aware prompt templates to prevent hallucinations and handle ambiguous joins.

### 3. Customer Churn Prediction & Analysis
- **Category:** Data Science & Machine Learning
- **Stack:** Python, Scikit-learn, PostgreSQL, Tableau, Pandas, NumPy
- **GitHub:** [Pushkar410/-churn](https://github.com/Pushkar410/-churn)
- **Features:** End-to-end churn prediction pipeline on transactional data, evaluating Logistic Regression, Decision Trees, and Random Forests with cross-validation; interactive Tableau dashboards communicating key churn drivers.

---

## Key Design Features (Inspired by mhndra.com)

1. **Aesthetic & Theme:** Obsidian dark mode (`#09090b`), card surfaces (`#121318`), subtle border strokes (`#232530`), and soft radial ambient lighting.
2. **Typography:** Pairing modern geometric sans-serif (`Plus Jakarta Sans`) for display headings with monospaced (`DM Mono`) for badges, tags, metrics, and timestamps.
3. **Interactive Components:**
   - Real-time IST Bengaluru clock (`BENGALURU: HH:MM:SS IST`).
   - One-click copy email button with custom Toast notification (`pushkar.1si24is072@gmail.com`).
   - Sticky glassmorphic navigation with active section scroll-spy.
   - Dynamic mobile drawer navigation.
   - Live metrics counter strip (8.69 CGPA, SIH Winner, 3+ Systems, DSA Focused).
   - Direct PDF Resume download/preview endpoint (`/resume` and `/Pushkar_R_Adiga_Resume.pdf`).
   - Asynchronous AJAX contact form hitting `/api/contact` with validation and feedback.

---

## Running Locally

### Prerequisites
- **Java 21** or later
- **Maven 3.9+** (or use the built-in Maven in VS Code / IntelliJ)

### Run with Maven
```bash
mvn spring-boot:run
```

Visit in your browser:
```text
http://localhost:8080
```

### Build Runnable JAR
```bash
mvn clean package
java -jar target/pushkar-portfolio-1.0.0.jar
```

---

## Project Structure

```text
c:\portfolio/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/pushkar/portfolio/
        │   ├── PortfolioApplication.java
        │   ├── HomeController.java        # Serves index.html & /resume PDF
        │   └── ContactController.java     # REST API /api/contact
        └── resources/
            ├── application.properties
            ├── templates/
            │   └── index.html             # Main Thymeleaf portfolio template
            └── static/
                ├── css/styles.css         # Modern dark portfolio styles
                └── js/main.js             # Navigation, clock, toast, contact AJAX
```

---

## Contact & Links

- **Email:** [pushkar.1si24is072@gmail.com](mailto:pushkar.1si24is072@gmail.com)
- **Phone:** +91-8073722418
- **LinkedIn:** [linkedin.com/in/pushkar-r-adiga](https://www.linkedin.com/in/pushkar-r-adiga)
- **GitHub:** [github.com/Pushkar410](https://github.com/Pushkar410)
- **takeUforward:** [takeuforward.org/profile/pushkarradiga](https://takeuforward.org/profile/pushkarradiga)
