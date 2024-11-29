package com.codewithkirk.userService.Service;

import com.codewithkirk.userService.Dto.UserDtoResponse;
import com.codewithkirk.userService.Model.Users;
import java.util.Optional;

public interface UserService {
    Users registerUser(UserDtoResponse userDto);	
    Users loginUser(String email, String password);

    Optional<Users> getUserById(Long id);

    Users updateUser(Long id, UserDtoResponse userDto);

    Users safeDeleteUser(Long id);

    void forceDeleteUser(Long id);
}
