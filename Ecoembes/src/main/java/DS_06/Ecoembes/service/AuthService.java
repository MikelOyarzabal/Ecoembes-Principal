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
    private final TokenStateManager tokenStateManager;
    
    // Storage to keep the session of the users that are logged in
    
    //private static Map<String, User> tokenStore = new HashMap<>();

    public AuthService(UserRepository userRepository, TokenStateManager tokenStateManager) {
        this.userRepository = userRepository;
        this.tokenStateManager = tokenStateManager;

    }

    // Login method that checks if the user exists in the database and validates the password
    public Optional<String> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent() && userOpt.get().checkPassword(password)) {
            String token =tokenStateManager.generateToken(userOpt.get());  // Generate a random token for the session
            return Optional.of(token);
        } else {
            return Optional.empty();
        }
    }
    
    // Logout method to remove the token from the session store
    public Optional<Boolean> logout(String token) {
    	boolean revoked = tokenStateManager.revokeToken(token);
        return revoked ? Optional.of(true) : Optional.empty();
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
    
    
 // Method to get the user based on the token (ahora valida estado)
    public User getUserByToken(String token) {
        System.out.println("===== AuthService.getUserByToken =====");
        System.out.println("Buscando token: [" + token + "]");
        
        Optional<User> userOpt = tokenStateManager.validateToken(token);
        
        System.out.println("Token válido: " + userOpt.isPresent());
        if (userOpt.isPresent()) {
            System.out.println("Usuario: " + userOpt.get().getEmail());
        }
        System.out.println("=======================================");
        
        return userOpt.orElse(null);
    }
    
    // Method to get the user based on the email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    // Method to get the user based on the nickname
    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElse(null);
    }
    // Nuevo método para validar token sin obtener usuario
    public boolean isValidToken(String token) {
        return tokenStateManager.validateToken(token).isPresent();
    }
    // Nuevo método para limpieza de tokens expirados
    public void cleanupTokens() {
        tokenStateManager.cleanupExpiredTokens();
    }

    // Synchronized method to guarantee unique token generation
    private static synchronized String generateToken() {
        return Long.toHexString(System.currentTimeMillis());
    }
}