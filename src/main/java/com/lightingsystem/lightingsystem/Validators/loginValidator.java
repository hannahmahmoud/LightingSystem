package com.lightingsystem.lightingsystem.Validators;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class loginValidator {

    @NotBlank(message= "Email is a required field!")
    @Email(message = "Invalid form of email!")
    private String email; 
    
    @NotBlank(message= "Please provide a pin!")
    private String password;
    
    public loginValidator(String email,String password)
{
    this.email=email;
    this.password=password;

}    



    
}
