package lopez.rafael.mobileappws.services.impl;

import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.models.dtos.UserDto;
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
