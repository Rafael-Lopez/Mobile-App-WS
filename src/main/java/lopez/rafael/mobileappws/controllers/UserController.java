package lopez.rafael.mobileappws.controllers;

import lopez.rafael.mobileappws.models.requests.UserDetailsRequestModel;
import lopez.rafael.mobileappws.models.responses.UserRest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {

    @GetMapping
    public String getUser(){
        return "getUser was called";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails){
        return null;
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
