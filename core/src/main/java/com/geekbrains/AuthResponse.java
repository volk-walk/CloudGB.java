package com.geekbrains;

public class AuthResponse extends Command {


    private boolean status ;

    public AuthResponse(boolean status) {
        this.status = status;
        type= CommandType.AUTH_RESPONSE;
    }


    public boolean getStatus (){
        return status;
    }
}
