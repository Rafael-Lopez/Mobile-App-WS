package lopez.rafael.mobileappws.services;

import lopez.rafael.mobileappws.data.entities.User;
import lopez.rafael.mobileappws.data.repositories.UserRepository;
import lopez.rafael.mobileappws.models.dtos.AddressDto;
import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private String encryptedPassword = "bbbb";
    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1);
        user.setFirstName("Rafael");
        user.setLastName("Lopez");
        user.setUserId("aaaaa");
        user.setEncryptedPassword(encryptedPassword);
    }

    @Test
    public final void testGetUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(user);

        UserDto userDto = userService.getUser("test@test.com");

        assertNotNull(userDto);
        assertEquals("Rafael", userDto.getFirstName());
    }

    @Test
    public void testGetUserNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> { userService.getUser("test@test.com"); } );
    }

    @Test
    public void createUserMethodTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        AddressDto addressDto = new AddressDto();
        addressDto.setType("Shipping");

        List<AddressDto> addresses = new ArrayList();
        addresses.add(addressDto);

        UserDto userDto = new UserDto();
        userDto.setAddresses(addresses);

        UserDto storedUserDetails = userService.createUser(userDto);

        assertNotNull(storedUserDetails);
        assertEquals(user.getFirstName(), storedUserDetails.getFirstName());
    }
}
