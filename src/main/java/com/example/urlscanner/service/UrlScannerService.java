package com.example.urlscanner.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Service
public class UrlScannerService {

    public Map<String, Object> analyzeUrl(String url) {
        Map<String, String> report = new LinkedHashMap<>();
        int riskScore = 0;

        try {
            // 1️⃣ Normalize URL
            url = url.trim().toLowerCase();
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
            report.put("Input URL", url);

            // 2️⃣ URL Length
            if (url.length() > 75) {
                riskScore += 15;
                report.put("URL Length", "Too long (Suspicious)");
            } else {
                report.put("URL Length", "Normal");
            }

            // 3️⃣ HTTPS Check
            if (!url.startsWith("https")) {
                riskScore += 20;
                report.put("HTTPS", "Not Secure (HTTP)");
            } else {
                report.put("HTTPS", "Secure");
            }

            // 4️⃣ IP address check in URL
            if (url.matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+.*")) {
                riskScore += 25;
                report.put("IP Address Usage", "Uses IP address (Highly Suspicious)");
            } else {
                report.put("IP Address Usage", "Domain Used");
            }

            // 5️⃣ Extract host and resolve IP
            URL u = new URL(url);
            String host = u.getHost();
            try {
                InetAddress inet = InetAddress.getByName(host);
                report.put("Resolved IP Address", inet.getHostAddress());
            } catch (Exception ex) {
                report.put("Resolved IP Address", "Unable to resolve IP");
            }

            // 6️⃣ Subdomain check
            int subdomains = host.split("\\.").length - 2; // domain + TLD excluded
            if (subdomains > 2) {
                riskScore += 10;
                report.put("Subdomain Check", "Too many subdomains (Suspicious)");
            } else {
                report.put("Subdomain Check", "Normal");
            }

            // 7️⃣ Suspicious keywords in URL
            String[] suspiciousWords = {"login", "verify", "update", "secure", "account", "bank", "confirm", "signin", "wp-login"};
            List<String> foundWords = new ArrayList<>();
            for (String word : suspiciousWords) {
                if (url.contains(word)) {
                    foundWords.add(word);
                    riskScore += 10;
                }
            }
            report.put("Suspicious Keywords", foundWords.isEmpty() ? "None" : String.join(", ", foundWords));

            // 8️⃣ Fetch page content safely
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(5000)
                        .ignoreHttpErrors(true)
                        .get();

                // Check for hidden iframes
                for (Element iframe : doc.select("iframe")) {
                    if (iframe.attr("src") != null && !iframe.attr("src").isEmpty()) {
                        riskScore += 15;
                        report.put("Iframe Check", "Contains iframe pointing to: " + iframe.attr("src"));
                        break;
                    }
                }

                // Check for forms asking sensitive info
                for (Element form : doc.select("form")) {
                    String text = form.text().toLowerCase();
                    if (text.contains("password") || text.contains("credit card") || text.contains("ssn")) {
                        riskScore += 20;
                        report.put("Form Check", "Contains suspicious form asking sensitive info");
                        break;
                    }
                }

            } catch (Exception e) {
                report.put("Content Fetch", "Unable to fetch content / site may be unreachable");
            }

        } catch (MalformedURLException e) {
            report.put("Host Check", "Invalid URL");
        }

        // 9️⃣ Determine risk level
        String level;
        if (riskScore >= 60) level = "HIGH";
        else if (riskScore >= 30) level = "MEDIUM";
        else level = "LOW";

        // 10️⃣ Build result map
        Map<String, Object> result = new HashMap<>();
        result.put("riskScore", riskScore);
        result.put("level", level);
        result.put("report", report);

        return result;
    }
}