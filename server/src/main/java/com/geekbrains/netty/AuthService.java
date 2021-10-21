package com.geekbrains.netty;

public interface AuthService {

    String getLoginAndPassword (String login,String password);

    boolean registration (String login, String password);
}
