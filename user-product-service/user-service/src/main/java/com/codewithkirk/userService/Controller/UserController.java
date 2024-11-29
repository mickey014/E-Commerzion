package com.codewithkirk.userService.Controller;

import com.codewithkirk.userService.Dto.UserCustomDtoResponse;
import com.codewithkirk.userService.Dto.UserDto;
import com.codewithkirk.userService.Dto.UserDtoResponse;
import com.codewithkirk.userService.Exception.FieldsException;
import com.codewithkirk.userService.Exception.UserNotFoundException;
import com.codewithkirk.userService.Model.Users;
import com.codewithkirk.userService.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;


    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDtoResponse userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (FieldsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }  catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        try {
            Optional<Users> user = userService.getUserById(id);
            Users foundUser  = user.get();
            UserCustomDtoResponse responseDTO = new UserCustomDtoResponse(
                    foundUser.getUserId(),
                    foundUser.getName(),
                    foundUser.getUsername(),
                    foundUser.getEmail(),
                    foundUser.getPhotoUrl(),
                    foundUser.getStatus());
            return ResponseEntity.ok(responseDTO);
        } catch (FieldsException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDtoResponse userDto) {
        try {
            // Call the service to update the user
            userService.updateUser(id, userDto);
            // Return the updated user as the response
            return ResponseEntity.ok(null);
        } catch (UserNotFoundException e) {
            // Handle SellerException Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (FieldsException e) {
            // Handle FieldsException (validation errors)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/users/safe/delete/{id}")
    public ResponseEntity<?> safeDeleteUser(@PathVariable Long id) {
        try {
            userService.safeDeleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (FieldsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // Return 404 with no body
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/users/force/delete/{id}")
    public ResponseEntity<?> forceDeleteUser(@PathVariable Long id) {
        try {
            userService.forceDeleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (FieldsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // Return 404 with no body
        } catch (Exception e) {
            // Catch all other exceptions (generic errors)
            throw new RuntimeException(e.getMessage());
        }
    }


}
