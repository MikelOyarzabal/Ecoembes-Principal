package DS_06.Ecoembes.facade;


import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DS_06.Ecoembes.dto.CredentialsDTO;
import DS_06.Ecoembes.entity.User;
import DS_06.Ecoembes.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag; 
@RestController
@RequestMapping("/auth")
@Tag(name = "Authorization Controller", description = "Login and logout operations")
public class AuthController {

    private AuthService authService;
    
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
    
    // Login endpoint
    @Operation(
        summary = "Login to the system",
        description = "Allows a user to login by providing email and password. Returns a token if successful.",
        responses = {
            @ApiResponse(responseCode = "200", description = "OK: Login successful, returns a token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid credentials, login failed"),
        }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(
    		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User's credentials", required = true)
    		@RequestBody CredentialsDTO credentials) {    	
        Optional<String> token = authService.login(credentials.getEmail(), credentials.getPassword());
        
    	if (token.isPresent()) {
    		return new ResponseEntity<>(token.get(), HttpStatus.OK);
    	} else {
    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    	}
    }

    // Logout endpoint
    @Operation(
        summary = "Logout from the system",
        description = "Allows a user to logout by providing the authorization token.",
        responses = {
            @ApiResponse(responseCode = "204", description = "No Content: Logout successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid token, logout failed"),
        }
    )    
    @PostMapping("/logout")    
    public ResponseEntity<Void> logout(
    		@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authorization token in plain text", required = true)
    		@RequestBody String token) {    	
        Optional<Boolean> result = authService.logout(token);
    	
        if (result.isPresent() && result.get()) {
        	return new ResponseEntity<>(HttpStatus.NO_CONTENT);	
        } else {
        	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }        
    }
    
//    @Operation(
//    	    summary = "Sign up to the system",
//    	    description = "Allows a new user to register by providing nickname, email and password. " +
//    	                  "Validates uniqueness of email and nickname.",
//    	    responses = {
//    	        @ApiResponse(responseCode = "204", description = "No Content: User created successfully"),
//    	        @ApiResponse(responseCode = "409", description = "Conflict: User already exists (email or nickname)"),
//    	    }
//    	)
//    	@PostMapping("/signup")
//    	public ResponseEntity<Void> signup(
//    	    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//    	        description = "User registration data (nickname, email, password)", 
//    	        required = true
//    	    )
//    	    @RequestBody User userData) {
//    	    
//    	    try {
//    	        authService.signup(userData);
//    	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    	    } catch (RuntimeException e) {
//    	        // Usuario ya existe (por email o nickname)
//    	        return new ResponseEntity<>(HttpStatus.CONFLICT);
//    	    }
//    	}
    
    
    @Operation(
            summary = "Validate token",
            description = "Validates if a token is still active and valid.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Token is valid"),
                @ApiResponse(responseCode = "401", description = "Token is invalid or expired"),
            }
        )
        @PostMapping("/validate")
        public ResponseEntity<Void> validateToken(@RequestBody String token) {
            if (authService.isValidToken(token)) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
}
