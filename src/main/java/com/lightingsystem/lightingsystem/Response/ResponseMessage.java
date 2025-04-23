package com.lightingsystem.lightingsystem.Response;



import com.lightingsystem.lightingsystem.Validators.signupValidator;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public   class   ResponseMessage {
    private String status;
        private signupValidator user;
        String message;


    public ResponseMessage(String status,signupValidator user )
    {
        this.status=status;
        this.user=user;
    }
    public ResponseMessage(String status, String message )
    {
        this.status=status;
        this.message=message;
    }
    public ResponseMessage(){}
}
