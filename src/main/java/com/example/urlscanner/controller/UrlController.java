package com.example.urlscanner.controller;

import com.example.urlscanner.service.UrlScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UrlController {

    @Autowired
    private UrlScannerService scannerService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/check")
    public String checkUrl(@RequestParam String url, Model model) {

        String result = scannerService.scanUrl(url);
        model.addAttribute("result", result);

        return "index";
    }
}
