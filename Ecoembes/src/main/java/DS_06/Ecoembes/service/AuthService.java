package DS_06.Ecoembes.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import DS_06.Ecoembes.entity.User;

@Service
public class AuthService {

    // Simulating a user repository
    private static Map<String, User> userRepository = new HashMap<>();
    
    // Storage to keep the session of the users that are logged in
    private static Map<String, User> tokenStore = new HashMap<>(); 

    // Login method that checks if the user exists in the database and validates the password
    public Optional<String> login(String email, String password) {
        User user = userRepository.get(email);
        
        if (user != null && user.checkPassword(password)) {
            String token = generateToken();  // Generate a random token for the session
            tokenStore.put(token, user);     // Store the token and associate it with the user

            return Optional.of(token);
        } else {
        	return Optional.empty();
        }
    }
    
    // Logout method to remove the token from the session store
    public Optional<Boolean> logout(String token) {
        if (tokenStore.containsKey(token)) {
            tokenStore.remove(token);

            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }
    
    // Method to add a new user to the repository
    public void addUser(User user) {
    	if (user != null) {
    		userRepository.putIfAbsent(user.getEmail(), user);
    	}
    }
    
    // Method to get the user based on the token
    public User getUserByToken(String token) {
        return tokenStore.get(token); 
    }
    
    // Method to get the user based on the email
    public User getUserByEmail(String email) {
		return userRepository.get(email);
	}

    // Synchronized method to guarantee unique token generation
    private static synchronized String generateToken() {
        return Long.toHexString(System.currentTimeMillis());
    }
}