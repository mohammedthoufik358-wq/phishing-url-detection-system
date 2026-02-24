package com.example.urlscanner.service;

import org.springframework.stereotype.Service;

@Service
public class UrlScannerService {

    public String scanUrl(String url) {

        int riskScore = 0;

        if (url.startsWith("http://")) {
            riskScore += 30;
        }

        if (url.contains("@")) {
            riskScore += 20;
        }

        if (url.length() > 75) {
            riskScore += 20;
        }

        String[] keywords = {"login", "verify", "update", "bank", "secure"};

        for (String word : keywords) {
            if (url.toLowerCase().contains(word)) {
                riskScore += 20;
                break;
            }
        }

        if (riskScore <= 30) {
            return "Safe URL (Risk Score: " + riskScore + "%)";
        } else if (riskScore <= 60) {
            return "Medium Risk URL (Risk Score: " + riskScore + "%)";
        } else {
            return "High Risk URL ⚠️ (Risk Score: " + riskScore + "%)";
        }
    }
}
