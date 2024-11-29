package com.codewithkirk.userService.Service.impl;

import com.codewithkirk.userService.Dto.UserDtoResponse;
import com.codewithkirk.userService.Exception.FieldsException;
import com.codewithkirk.userService.Model.Users;
import com.codewithkirk.userService.Model.Users.UserStatus; 
import com.codewithkirk.userService.Repository.UserRepository;
import com.codewithkirk.userService.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Users registerUser (UserDtoResponse userDto) {
        String name = userDto.getName().replaceAll("\\s+", " ").trim();
        String username = userDto.getUsername().replaceAll("\\s+", " ").trim();
        String password = userDto.getPassword();
        String confirmPassword = userDto.getConfirmPassword();

        // Validate required fields
        if (name.isEmpty()) {
            throw new FieldsException("Name is required.");
        }

        if(username.isEmpty()) {
            throw new FieldsException("Username is required.");
        }

        if(username.length() < 8 ) {
            throw new FieldsException("Username is too short. min:(8)");
        }

        if(usernameExists(username)) {
            throw new FieldsException("Username already exists.");
        }

        if (password.isEmpty()) {
            throw new FieldsException("Password is required.");
        }

        if(confirmPassword.isEmpty()) {
            throw new FieldsException("Confirm Password is required.");
        }

        if (!password.equals(confirmPassword)) {
            throw new FieldsException("Passwords do not matched.");
        }

        // Build the new user object using the Builder pattern
        Users newUser  = Users.builder()
                .name(name)
                .username(username)
                .email("")
                .password(password)
                .photoUrl("")
                .status(UserStatus.ACTIVE) // Convert string to enum
                .build();

        return userRepository.save(newUser);
    }

    @Override
    public Users loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new FieldsException("Email is required");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new FieldsException("Password is required.");
        }

        Optional<Users> optionalUser  = userRepository.findByUsername(username);

        // Check if user exists and validate password
        Users user = optionalUser .orElseThrow(() -> new FieldsException("Invalid username or password."));

        // Check if the password matches
        if (!user.getPassword().equals(password)) {
            throw new FieldsException("Invalid username or password.");
        }

        return user;
    }

    @Override
    public Users updateUser(Long id, UserDtoResponse userDto) {

        String name = userDto.getName().replaceAll("\\s+", " ").trim();
        String username = userDto.getUsername().replaceAll("\\s+", " ").trim();
        String email = userDto.getEmail().replaceAll("\\s+", " ").trim();
        String password = userDto.getPassword().trim();
        String photoUrl = userDto.getPhotoUrl();

        // Retrieve the user by ID (Assuming user exists)
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Validate required fields
        if (name.isEmpty()) {
            throw new FieldsException("Name is required.");
        }

        if(username.isEmpty()) {
            throw new FieldsException("Username is required.");
        }

        if(username.length() < 8 ) {
            throw new FieldsException("Username is too short. min:(8)");
        }

        // Check if the username is being changed
        if (!user.getUsername().equals(username)) {
            // Check if the new username already exists
            if (usernameExists(userDto.getUsername())) {
                throw new FieldsException("Username already exists.");
            }
        }

        // Validate email format
        if (!isValidEmail(email)) {
            throw new FieldsException("Email must be valid ex. (user@gmail.com)");
        }
        // Check if the email already exists

        if(!user.getEmail().equals(email)) {
            if (emailExists(userDto.getEmail())) {
                throw new FieldsException("Email already exists.");
            }
        }

        if (password.isEmpty()) {
            throw new FieldsException("Password is required.");
        }




        // Update user fields
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        UserStatus status = UserStatus.valueOf(userDto.getStatus().toString());
        user.setPhotoUrl(photoUrl);
        user.setStatus(status);

        // Save the updated user to the database
        return userRepository.save(user);
    }

    @Override
    public Users safeDeleteUser(Long id) {
        Users existingUser  = userRepository.findById(id)
                .orElseThrow(() -> new FieldsException("User not found"));
        existingUser.setStatus(UserStatus.INACTIVE); // Set status to INACTIVE
        return userRepository.save(existingUser);
    }

    @Override
    public void forceDeleteUser(Long id) {
        Users existingUser  = userRepository.findById(id)
                .orElseThrow(() -> new FieldsException("User not found"));
        userRepository.delete(existingUser);
    }


    @Override
    public Optional<Users> getUserById(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new FieldsException("User not found")));
    }

    // Check if the email exists
    public boolean usernameExists(String username) {
        String sanitized_username = username.trim(); // Normalize the email
        return userRepository.findByUsername(sanitized_username).isPresent();
    }

    // Check if the email exists
    public boolean emailExists(String email) {
        String sanitized_email = email.trim().toLowerCase(); // Normalize the email
        return userRepository.findByEmail(sanitized_email).isPresent();
    }

    // validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

}