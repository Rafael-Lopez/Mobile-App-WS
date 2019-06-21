package lopez.rafael.mobileappws.services;

import lopez.rafael.mobileappws.models.dtos.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);
}
