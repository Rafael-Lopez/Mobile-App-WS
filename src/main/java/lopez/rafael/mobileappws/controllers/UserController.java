package lopez.rafael.mobileappws.controllers;

import lopez.rafael.mobileappws.models.dtos.AddressDto;
import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.models.requests.UserDetailsRequestModel;
import lopez.rafael.mobileappws.models.responses.*;
import lopez.rafael.mobileappws.exceptions.UserServiceException;
import lopez.rafael.mobileappws.services.impl.AddressServiceImpl;
import lopez.rafael.mobileappws.services.impl.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
    private UserServiceImpl userService;
    private AddressServiceImpl addressService;

    @Autowired
    public UserController(UserServiceImpl userService, AddressServiceImpl addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping( path = "/{id}",
                 produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public UserRest getUser(@PathVariable String id){
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping( consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
                  produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception{
        if(userDetails.getFirstName().isEmpty()) {
                throw new UserServiceException(ErrorMesages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @PutMapping( path = "/{id}",
                 consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
                 produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails){
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping( path = "/{id}",
                    consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
                    produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public OperationStatusModel deleteUser(@PathVariable String id){
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping( produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList();

        List<UserDto> users = userService.getUsers(page, limit);

        for(UserDto userDto : users) {
            UserRest userModel = new UserRest();
            BeanUtils.copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }

        return returnValue;
    }

    @GetMapping( path = "/{id}/addresses",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public List<AddressRest> getUserAddresses(@PathVariable String id){
        List<AddressRest> returnValue = new ArrayList();

        List<AddressDto> addressesDto = addressService.getAddresses(id);

        if(addressesDto != null && !addressesDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressesDto, listType);
        }

        return returnValue;
    }

    @GetMapping( path = "/{userId}/addresses/{addressId}",
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
    public AddressRest getUserAddress(@PathVariable String userId, @PathVariable String addressId){
        AddressDto addressesDto = addressService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();

        Link addressLink = ControllerLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
        Link userLink = ControllerLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = ControllerLinkBuilder.linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");

        AddressRest addressRestModel = modelMapper.map(addressesDto, AddressRest.class);

        addressRestModel.add(addressLink);
        addressRestModel.add(userLink);
        addressRestModel.add(addressesLink);

        return addressRestModel;
    }
}
