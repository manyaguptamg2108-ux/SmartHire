# SmartHire – Smart Resume Match & Job Recommendation Documentation

## 1. Feature Architecture Overview
The **Smart Resume Match** feature enables candidates to upload resumes in PDF (`.pdf`) or Word (`.docx`) format, extract structured candidate data and skills using a deterministic technology dictionary, compare candidate attributes against both internal active database jobs and live external market jobs, calculate percentage match scores, and receive ranked recommendations complete with missing skills analysis and optional profile synchronization.

```
Candidate Resume (.pdf / .docx)
             │
             ▼
[ ResumeParserService ] ──▶ PDFBox 3.x / Apache POI 5.x Text Stripping
             │
             ▼ Rule-Based Technology Dictionary Matching (45+ Technologies)
[ ResumeProfile ] ───────▶ Normalized Skill Set, Education, Experience
             │
             ▼
[ ResumeJobMatchingService ] ──▶ Compares against DB Jobs & Live Adzuna API
             │
             ▼
Ranked Job Recommendations ──▶ Match %, Matched Skills, Missing Skills, Explanations
```

---

## 2. Technical Component Matrix

| Component Class | Role / Purpose | Key Implementation Details |
| :--- | :--- | :--- |
| `model.ResumeProfile` | Candidate Data Container | Case-insensitive skill normalization using `TreeSet<String>`, raw text storage, education & experience list parsing. |
| `model.JobRecommendation` | Ranked Result DTO | Implements `Comparable<JobRecommendation>`, holds match percentage, matched/missing skill lists, action URL, and job source type. |
| `service.ResumeParserService` | Text & Skill Extraction | Uses `org.apache.pdfbox.Loader` for PDF text extraction and `XWPFWordExtractor` for `.docx`. Enforces rule-based dictionary matching. |
| `service.ResumeJobMatchingService` | Matching Engine | Calculates weighted match scores based on skill overlap (70%) and title/description keyword density (30%). Ranks recommendations descending. |
| `controller.ResumeMatchServlet` | HTTP Controller | Annotated with `@WebServlet("/resume-match")` & `@MultipartConfig`. Handles resume upload, file validation (<5MB), parsing, and profile sync. |
| `resume-match.jsp` | User View | Integrated under `http://localhost:8080/SmartHire/resume-match`. Follows SmartHire design system, pill badges, and progress meters. |
| `util.ResumeMatchingTest` | Verification Suite | Automated unit test verifying skill dictionary matching, score calculations, missing skills, profile creation, and recommendation ranking. |

---

## 3. Technology Dictionary & Rule-Based Parsing
Resume skill extraction relies on a rule-based technology dictionary including:
- **Languages**: Java, Python, C++, C#, JavaScript, TypeScript, SQL, HTML, CSS.
- **Frameworks & Libraries**: React, Angular, Vue.js, Node.js, Express, Spring, Spring Boot, Hibernate, JDBC, Servlet, JSP.
- **Databases**: MySQL, MariaDB, PostgreSQL, MongoDB, Oracle.
- **Cloud & DevOps**: Docker, Kubernetes, AWS, Azure, GCP, Linux, Git, GitHub, Jenkins, Maven, Gradle.
- **Architecture & Concepts**: REST, RESTful, GraphQL, Microservices, Agile, Scrum, Data Analysis, Machine Learning, System Design, OOP.

---

## 4. Match Percentage Formula
Match percentages are computed deterministically as follows:
$$ \text{Skill Overlap Score} = \left( \frac{\text{Matched Skills Count}}{\text{Required Job Skills Count}} \right) \times 70\% $$
$$ \text{Keyword Density Bonus} = \text{Title \& Description Skill Mentions} \times 5\% \quad (\text{Max } 30\%) $$
$$ \text{Total Match Percentage} = \min(100.0\%, \max(15.0\%, \text{Skill Overlap Score} + \text{Keyword Density Bonus})) $$

---

## 5. Verification & Testing Results
Execution of `mvn exec:java "-Dexec.mainClass=util.ResumeMatchingTest"` yields:
- `[TEST 1] ResumeProfile structure & skill normalization: PASSED`
- `[TEST 2] ResumeParserService dictionary skill extraction: PASSED`
- `[TEST 3] ResumeJobMatchingService scoring & ranking: PASSED (Top match score: 76.0%)`
- `[TEST 4] Missing skills categorization: PASSED`
- **Result**: `ALL RESUME MATCHING TESTS PASSED SUCCESSFULLY!`
