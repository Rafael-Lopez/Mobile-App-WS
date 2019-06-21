package lopez.rafael.mobileappws.services.impl;

import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        if( userRepository.findByEmail(userDto.getEmail()) != null ){
            throw new RuntimeException("Record already exists!");
        }

        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        user.setUserId(UUID.randomUUID().toString());
        user.setEncryptedPassword("test");

        user = userRepository.save(user);

        UserDto result = new UserDto();
        BeanUtils.copyProperties(user, result);

        return result;
    }
}
