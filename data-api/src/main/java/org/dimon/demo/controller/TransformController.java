package org.dimon.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class TransformController {

    @Value("${internal.token}")
    private String internalToken;

    @PostMapping("/transform")
    public ResponseEntity<Map<String, String>> transform(
            @RequestHeader(value = "X-Internal-Token", required = false) String token,
            @RequestBody Map<String, String> payload) {

        if (token == null || !token.equals(internalToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String text = payload.get("text");
        if (text == null) {
            return ResponseEntity.badRequest().build();
        }

        // simple transformation: reverse and uppercase
        String result = new StringBuilder(text).reverse().toString().toUpperCase();

        return ResponseEntity.ok(Map.of("result", result));
    }

    @GetMapping("/transform")
    public String ping() {
        return "OK";
    }
}

