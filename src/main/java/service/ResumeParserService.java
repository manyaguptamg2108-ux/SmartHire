package service;

import model.ResumeProfile;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service extracting text and structured candidate information from PDF and DOCX resume files.
 * Rule-based technology dictionary & section detection engine.
 */
public class ResumeParserService {

    // Master technology and skill dictionary for deterministic skill extraction
    private static final String[] SKILL_DICTIONARY = {
        "Java", "Python", "C++", "C#", "JavaScript", "TypeScript", "React", "Angular", "Vue.js", "Node.js",
        "Express", "Spring", "Spring Boot", "Hibernate", "SQL", "MySQL", "MariaDB", "PostgreSQL", "MongoDB",
        "Oracle", "HTML", "CSS", "Git", "GitHub", "Docker", "Kubernetes", "AWS", "Azure", "GCP", "Linux",
        "REST", "RESTful", "GraphQL", "Microservices", "Agile", "Scrum", "Jenkins", "Maven", "Gradle",
        "Data Analysis", "Machine Learning", "System Design", "OOP", "JDBC", "JSP", "Servlet"
    };

    /**
     * Parses uploaded resume InputStream based on filename extension (.pdf or .docx).
     *
     * @param inputStream File input stream
     * @param fileName File name with extension
     * @return Populated ResumeProfile
     * @throws Exception if format is unsupported or text extraction fails
     */
    public ResumeProfile parseResume(InputStream inputStream, String fileName) throws Exception {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty.");
        }

        String lowerName = fileName.trim().toLowerCase();
        String extractedText;

        if (lowerName.endsWith(".pdf")) {
            extractedText = extractPdfText(inputStream);
        } else if (lowerName.endsWith(".docx")) {
            extractedText = extractDocxText(inputStream);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload a PDF or DOCX resume.");
        }

        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new IllegalArgumentException("We couldn't extract readable text from this resume file.");
        }

        return buildProfileFromText(extractedText);
    }

    /**
     * Extracts text from PDF using Apache PDFBox.
     */
    public String extractPdfText(InputStream is) throws Exception {
        byte[] bytes = is.readAllBytes();
        if (bytes.length == 0) {
            throw new IllegalArgumentException("Resume file is empty.");
        }
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Extracts text from DOCX using Apache POI.
     */
    public String extractDocxText(InputStream is) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }

    /**
     * Constructs a structured ResumeProfile from raw text via rule-based extraction.
     */
    public ResumeProfile buildProfileFromText(String text) {
        ResumeProfile profile = new ResumeProfile();
        profile.setRawText(text);

        // [1] Extract Contact Info via Regex
        String email = extractRegexMatch(text, "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        if (email != null) profile.setEmail(email);

        String phone = extractRegexMatch(text, "(\\+\\d{1,3}[- .]?)?\\(?\\d{3}\\)?[- .]?\\d{3}[- .]?\\d{4}");
        if (phone != null) profile.setPhone(phone);

        // Name estimation from first non-empty line
        String[] lines = text.split("\r?\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && trimmed.length() < 50 && !trimmed.toLowerCase().contains("resume") && !trimmed.toLowerCase().contains("curriculum")) {
                profile.setName(trimmed);
                break;
            }
        }

        // [2] Rule-based Skill Extraction & Normalization
        Set<String> detectedSkills = extractSkillsFromText(text);
        profile.setSkills(detectedSkills);

        // [3] Section Extraction (Education & Experience)
        for (String line : lines) {
            String trimmed = line.trim();
            String lLower = trimmed.toLowerCase();

            if (lLower.contains("b.tech") || lLower.contains("b.e.") || lLower.contains("m.tech") ||
                lLower.contains("mca") || lLower.contains("b.sc") || lLower.contains("bachelor") || lLower.contains("master") || lLower.contains("degree")) {
                profile.addEducation(trimmed);
            }

            if (lLower.contains("developer") || lLower.contains("engineer") || lLower.contains("analyst") ||
                lLower.contains("intern") || lLower.contains("experience") || lLower.contains("years")) {
                profile.addExperience(trimmed);
            }
        }

        // [4] Extract Keywords for Live API Queries
        Set<String> keywords = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        keywords.addAll(detectedSkills);
        profile.setExtractedKeywords(keywords);

        // Summary snippet
        if (lines.length > 0) {
            StringBuilder summaryBuilder = new StringBuilder();
            for (int i = 0; i < Math.min(5, lines.length); i++) {
                if (!lines[i].trim().isEmpty()) {
                    summaryBuilder.append(lines[i].trim()).append(" ");
                }
            }
            profile.setSummary(summaryBuilder.toString().trim());
        }

        return profile;
    }

    /**
     * Deterministic rule-based skill extraction from technology dictionary.
     */
    public Set<String> extractSkillsFromText(String text) {
        Set<String> foundSkills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (text == null) return foundSkills;

        String lowerText = text.toLowerCase();

        for (String skill : SKILL_DICTIONARY) {
            String skillLower = skill.toLowerCase();
            // Word boundary match
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skillLower) + "\\b");
            Matcher matcher = pattern.matcher(lowerText);
            if (matcher.find()) {
                foundSkills.add(normalizeSkillName(skill));
            }
        }
        return foundSkills;
    }

    public String normalizeSkillName(String raw) {
        if (raw == null) return "";
        String trimmed = raw.trim();
        for (String dictSkill : SKILL_DICTIONARY) {
            if (dictSkill.equalsIgnoreCase(trimmed)) {
                return dictSkill;
            }
        }
        return trimmed;
    }

    private String extractRegexMatch(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
