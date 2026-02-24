package com.example.urlscanner.controller;

import com.example.urlscanner.service.UrlScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class UrlScannerController {

    @Autowired
    private UrlScannerService scannerService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/scan")
    public String scanUrl(@RequestParam("url") String url, Model model) {

        Map<String, Object> result = scannerService.analyzeUrl(url);

        model.addAttribute("riskScore", result.get("riskScore"));
        model.addAttribute("level", result.get("level"));
        model.addAttribute("report", result.get("report"));
        model.addAttribute("inputUrl", url);

        return "index";
    }
}