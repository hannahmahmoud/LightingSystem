package com.lightingsystem.lightingsystem.Validators;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class signupValidator {

    @NotBlank(message="First Name is a required field!")
    private String firstName;

    
    @NotBlank(message="Last Name is a required field!")
    private String lastName;

    @NotBlank(message="Phone Number is a required field!")
    @Size(min= 12, max=15, message = "Min length are 12 numbers, Max length are 14 numbers! ")
    private String phoneNumber;

    @NotBlank(message="Password is a required field!")
    @Size(min= 8, message = "min length of a password is at least 8 charcters!" )
    private String password;

    @NotBlank(message="Confrim Password is required field!")
    @Size(min= 8, message = "min length of a password is at least 8 charcters!" )
    private String confrimPasword;

    @NotBlank(message= "Email is a required field")
    @Email
    private String email;



    public signupValidator(String firstName, String lastName, String email, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber=phoneNumber;
    }

    
}
