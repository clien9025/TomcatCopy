package com.example.demowebserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/{name}")
    public ResponseEntity<String> getHelloWorld(@PathVariable String name) {
        return ResponseEntity.ok("Hello, " + name);
    }

    @PostMapping("/")
    public ResponseEntity<String> postHelloWorld(@RequestBody String name) {
        return ResponseEntity.ok("Hello, " + name + ". Your name has been posted.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateHelloWorld(@PathVariable Long id, @RequestBody String name) {
        return ResponseEntity.ok("Hello, " + name + ". Your entity with ID " + id + " has been updated.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHelloWorld(@PathVariable Long id) {
        return ResponseEntity.ok("The entity with ID " + id + " has been deleted.");
    }

}
