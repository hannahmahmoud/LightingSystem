package com.lightingsystem.lightingsystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lightingsystem.lightingsystem.Services.userServics;
import com.lightingsystem.lightingsystem.Validators.loginValidator;
import com.lightingsystem.lightingsystem.Validators.signupValidator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


// controller hwa el route in node.js 

@RestController // used to tell spring boot this is controller and its using rest api
@CrossOrigin // for security purposes
// hena b7ot el link leh controller dah 
@RequestMapping("api/v1/user")

public class userController {
    @Autowired
    private userServics userService;

    // Post request 
    @PostMapping( value = "/signup", consumes = "application/json", produces = "application/json")// ashan kda asmha post mapping 
    public ResponseEntity<Object> signupString(@RequestBody signupValidator user) {
        //TODO: process POST request
        return  userService.signup(user);
       
    }
    //Post Request
     @PostMapping(value ="/login", consumes = "application/json", produces = "application/json")
     public  ResponseEntity<Object>  loginEntity(@RequestBody loginValidator user ) {
         
         
         return userService.login(user);
     }
     
    
    
}
