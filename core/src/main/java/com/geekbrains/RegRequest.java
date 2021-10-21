package com.geekbrains;

public class RegRequest extends Command {
    private final String login;
    private final String pass;

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public RegRequest (String login , String pass){
        this.login=login;
        this.pass= pass;
        type = CommandType.REG_REQUEST;
    }
}
