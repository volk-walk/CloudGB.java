package com.geekbrains.io;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class Server {



    public static void main(String[] args) {


        try(ServerSocket server = new ServerSocket(8189)){
            log.debug("Server started...");

            while (true){
               Socket socket= server.accept();
               log.debug("Client accepted");
               Handler handler = new Handler(socket);
               new Thread(handler).start();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
