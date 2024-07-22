package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    @Operation(summary = "Test API", description = "Returns a simple success message")
    @ApiResponse(responseCode = "200", description = "Success")
    public ResponseEntity<String> testMethod() {
        return ResponseEntity.ok("Test successful");
    }
}

