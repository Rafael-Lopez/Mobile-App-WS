package lopez.rafael.mobileappws.services.impl;

import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.exceptions.UserServiceException;
import lopez.rafael.mobileappws.models.dtos.AddressDto;
import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.models.responses.ErrorMesages;
import lopez.rafael.mobileappws.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        for(AddressDto address : userDto.getAddresses()){
            address.setUserDetails(userDto);
            address.setAddressId(UUID.randomUUID().toString());
        }

        ModelMapper modelMapper = new ModelMapper();
        User user = modelMapper.map(userDto, User.class);

        user.setUserId(UUID.randomUUID().toString());
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        user = userRepository.save(user);

        UserDto result = modelMapper.map(user, UserDto.class);

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

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList();

        if(page>0) page = page-1;

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<User> usersPage = userRepository.findAll(pageableRequest);
        List<User> users = usersPage.getContent();

        for(User user : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
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
