package com.codewithkirk.userService.Dto;


import com.codewithkirk.userService.Model.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoResponse {
    private Long userId;
    private String name;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String photoUrl;
    private Users.UserStatus status;
}
