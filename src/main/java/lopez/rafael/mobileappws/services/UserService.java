package lopez.rafael.mobileappws.services;

import lopez.rafael.mobileappws.models.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
    UserDto createUser(UserDto user);
    UserDto getUser(String email);
    UserDto getUserByUserId(String userId);
    UserDto updateUser(String userId, UserDto user);
    void deleteUser(String userId);
}
