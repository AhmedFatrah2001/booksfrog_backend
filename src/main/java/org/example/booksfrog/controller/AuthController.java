package org.example.booksfrog.controller;

import org.example.booksfrog.model.Token;
import org.example.booksfrog.service.CustomUserDetailsService;
import org.example.booksfrog.service.TokenService;
import org.example.booksfrog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.example.booksfrog.model.User;
import org.example.booksfrog.service.UserService;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final TokenService tokenService;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            UserService userService,
            TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.tokenService = tokenService;
    }


    // POST method for login and token generation
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        // Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        // Load user details by username
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // Fetch user entity
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }


        int totalTokens = tokenService.getTotalTokens((user.getId()));

        // Return token and user details
        return ResponseEntity.ok(new AuthResponse(token, user, totalTokens));
    }


    // POST method for user registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Validate required fields
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty() ||
                registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty() ||
                registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username, email, and password are mandatory.");
        }

        // Check if the username or email is already taken
        if (userService.isUsernameTaken(registerRequest.getUsername()) ||
                userService.isEmailTaken(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or email already taken.");
        }

        // Create a new user
        User newUser = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword()) // Encrypt password before saving
                .isPremium(registerRequest.getIsPremium() != null && registerRequest.getIsPremium()) // Optional
                .profilePicture(registerRequest.getProfilePicture()) // Optional
                .build();

        User createdUser = userService.createUser(newUser);

        Token initialToken = new Token(createdUser, 50,0); // Default to 0 tokens
        tokenService.createToken(initialToken); // Save the token entry

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}

// Updated RegisterRequest to handle mandatory and optional fields
class RegisterRequest {
    private String username; // Mandatory
    private String email;    // Mandatory
    private String password; // Mandatory
    private Boolean isPremium; // Optional
    private byte[] profilePicture; // Optional

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
}


// Separate AuthRequest class for username and password
class AuthRequest {
    private String username;
    private String password;

    // Default constructor
    public AuthRequest() {
    }

    // Constructor with parameters
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
}

// Separate AuthResponse class for returning the token
class AuthResponse {
    private Long id; // Add user ID
    private String token;
    private String username;
    private String email;
    private boolean isPremium;
    private byte[] profilePicture;
    private int totalTokens;

    public AuthResponse(String token, User user, int totalTokens) {
        this.id = user.getId();
        this.token = token;
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.isPremium = user.isPremium();
        this.profilePicture = user.getProfilePicture();
        this.totalTokens = totalTokens; // Assign totalTokens
    }


    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }
}


