package com.codewithkirk.userService.Controller;

import com.codewithkirk.userService.Dto.UserDto;
import com.codewithkirk.userService.Exception.FieldsException;
import com.codewithkirk.userService.Security.JwtResponse;
import com.codewithkirk.userService.Security.JwtUtility;
import com.codewithkirk.userService.Security.SecretKeyManager;
import com.codewithkirk.userService.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final UserService userService;

    private final JwtUtility jwtUtil;
    private final SecretKeyManager secretKeyManager;


    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDto) {
        try {
            userService.loginUser (
                    userDto.getUsername(),
                    userDto.getPassword()
            );

            String jwtToken = jwtUtil.generateToken(userDto.getUsername()); // Generate JWT token
            String newSecretKey = secretKeyManager.generateNewSecretKey(); // Generate a new secret key
            secretKeyManager.overwriteSecretKey(newSecretKey); // Overwrite the secret key

            // Return the token as a Bearer token
            return ResponseEntity.ok(new JwtResponse(jwtToken));
        } catch (FieldsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
