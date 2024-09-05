package fr.vdm.referentiel.refadmin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/env")
public class EnvironmentController {

    @Value("${environement.label}")
    private String label;

    @GetMapping("/label")
    public ResponseEntity<String> getEnvironmentLabel() {
        return ResponseEntity.ok(label);
    }
}
