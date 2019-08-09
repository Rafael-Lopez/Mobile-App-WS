package lopez.rafael.mobileappws.services.impl;

import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.exceptions.UserServiceException;
import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.models.responses.ErrorMesages;
import lopez.rafael.mobileappws.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        if( userRepository.findByEmail(userDto.getEmail()) != null ){
            throw new RuntimeException("Record already exists!");
        }

        User user = new User();
        BeanUtils.copyProperties(userDto, user);

        user.setUserId(UUID.randomUUID().toString());
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        user = userRepository.save(user);

        UserDto result = new UserDto();
        BeanUtils.copyProperties(user, result);

        return result;
    }

    @Override
    public UserDto getUser(String email) {
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException(email);
        }

        UserDto result = new UserDto();
        BeanUtils.copyProperties(user, result);

        return result;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserDto returnValue = new UserDto();
        User user = userRepository.findByUserId(userId);

        if(user == null){
            throw new UsernameNotFoundException("User with ID: " + userId + " not found");
        }

        BeanUtils.copyProperties(user, returnValue);

        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {
        UserDto returnValue = new UserDto();
        User oldUser = userRepository.findByUserId(userId);

        if(oldUser == null){
            throw new UserServiceException(ErrorMesages.NO_RECORD_FOUND.getErrorMessage());
        }

        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());

        User updateUser = userRepository.save(oldUser);
        BeanUtils.copyProperties(updateUser, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        User oldUser = userRepository.findByUserId(userId);
        if(oldUser == null){
            throw new UserServiceException(ErrorMesages.NO_RECORD_FOUND.getErrorMessage());
        }

        userRepository.delete(oldUser);
    }

    //Method used automatically by Spring for user authentication
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException(email);
        }

        return new org.springframework.security.core.userdetails.User( user.getEmail(), user.getEncryptedPassword(), new ArrayList<>() );
    }
}
