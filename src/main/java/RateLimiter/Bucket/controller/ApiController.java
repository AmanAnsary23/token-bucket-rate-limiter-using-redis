package RateLimiter.Bucket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/hello")
    public ResponseEntity<String> hellp() {
        return ResponseEntity.ok("Hello! Request successful.");
    }

    @GetMapping("/data")
    public ResponseEntity<String> getData(){
        return ResponseEntity.ok("Here is you date!");
    }
}
