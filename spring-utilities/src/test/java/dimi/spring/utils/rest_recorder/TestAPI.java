package dimi.spring.utils.rest_recorder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAPI
{
    @GetMapping("/api/ping")
    public ResponseEntity<String> ping()
    {
        return ResponseEntity.ok("pong");
    }


    @PostMapping("/api/echo")
    public ResponseEntity<String> echo(@RequestBody String body)
    {
        return ResponseEntity.ok("echo:" + body);
    }


    @GetMapping("/api/status/{code}")
    public ResponseEntity<String> status(@PathVariable(name = "code") int code)
    {
        return ResponseEntity.status(code).body("status:" + code);
    }
}
