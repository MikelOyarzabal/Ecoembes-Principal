package DS_06.Ecoembes.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DS_06.Ecoembes.dao.UserRepository;
import DS_06.Ecoembes.entity.User;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    
    // Storage to keep the session of the users that are logged in
    private static Map<String, User> tokenStore = new HashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Login method that checks if the user exists in the database and validates the password
    public Optional<String> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            String token = generateToken();  // Generate a random token for the session
            tokenStore.put(token, userOpt.get());     // Store the token and associate it with the user

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
            // Check if user already exists by email
            Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
            if (existingUserByEmail.isPresent()) {
                throw new RuntimeException("User with email " + user.getEmail() + " already exists");
            }
            
            // Check if user already exists by nickname
            Optional<User> existingUserByNickname = userRepository.findByNickname(user.getNickname());
            if (existingUserByNickname.isPresent()) {
                throw new RuntimeException("User with nickname " + user.getNickname() + " already exists");
            }
            
            userRepository.save(user);
        }
    }
    
    // Method to get the user based on the token
    public User getUserByToken(String token) {
        return tokenStore.get(token); 
    }
    
    // Method to get the user based on the email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    // Method to get the user based on the nickname
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElse(null);
    }

    // Synchronized method to guarantee unique token generation
    private static synchronized String generateToken() {
        return Long.toHexString(System.currentTimeMillis());
    }
}