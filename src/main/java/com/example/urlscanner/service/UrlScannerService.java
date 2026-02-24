package com.example.urlscanner.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UrlScannerService {

    public Map<String, Object> analyzeUrl(String url) {

        int riskScore = 0;
        Map<String, String> report = new LinkedHashMap<>();

        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }

        // 1️⃣ URL Length
        if (url.length() > 75) {
            riskScore += 15;
            report.put("URL Length", "Too long (Suspicious)");
        } else {
            report.put("URL Length", "Normal");
        }

        // 2️⃣ HTTPS Check
        if (!url.startsWith("https")) {
            riskScore += 20;
            report.put("HTTPS", "Not Secure (HTTP)");
        } else {
            report.put("HTTPS", "Secure");
        }

        // 3️⃣ IP Address Check
        if (url.matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+.*")) {
            riskScore += 25;
            report.put("IP Address Usage", "Uses IP Address (Highly Suspicious)");
        } else {
            report.put("IP Address Usage", "Domain Used");
        }

        // 4️⃣ Suspicious Keywords
        String[] suspiciousWords = {"login", "verify", "update", "secure", "account", "bank", "confirm"};
        boolean keywordFound = false;

        for (String word : suspiciousWords) {
            if (url.toLowerCase().contains(word)) {
                riskScore += 10;
                report.put("Suspicious Keyword", word);
                keywordFound = true;
                break;
            }
        }

        if (!keywordFound) {
            report.put("Suspicious Keyword", "None");
        }

        // 5️⃣ Multiple Subdomains
        if (url.split("\\.").length > 5) {
            riskScore += 10;
            report.put("Subdomain Check", "Too many subdomains (Suspicious)");
        } else {
            report.put("Subdomain Check", "Normal");
        }

        // Final Risk Level
        String level;
        if (riskScore >= 60) level = "HIGH";
        else if (riskScore >= 30) level = "MEDIUM";
        else level = "LOW";

        Map<String, Object> result = new HashMap<>();
        result.put("riskScore", riskScore);
        result.put("level", level);
        result.put("report", report);

        return result;
    }
}