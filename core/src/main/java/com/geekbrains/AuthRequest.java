package com.geekbrains;

public class AuthRequest extends Command {
    private final String login;
    private final String pass;

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public AuthRequest (String login , String pass){
        this.login=login;
        this.pass= pass;
        type = CommandType.AUTH_REQUEST;
    }


}
