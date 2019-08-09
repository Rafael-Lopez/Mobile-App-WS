package lopez.rafael.mobileappws.controllers;

import lopez.rafael.mobileappws.models.dtos.UserDto;
import lopez.rafael.mobileappws.models.requests.UserDetailsRequestModel;
import lopez.rafael.mobileappws.models.responses.ErrorMesages;
import lopez.rafael.mobileappws.models.responses.UserRest;
import lopez.rafael.mobileappws.services.impl.UserServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
    private UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
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
                throw new Exception(ErrorMesages.MISSING_REQUIRED_FIELD.getErrorMessage());
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        UserRest returnValue = new UserRest();
        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping
    public String updateUser(){
        return "updateUser was called";
    }

    @DeleteMapping
    public String deleteUser(){
        return "deleteUser was called";
    }
}
