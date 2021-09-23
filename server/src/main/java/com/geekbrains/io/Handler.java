package com.geekbrains.io;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class Handler implements Runnable {
    private static final int buffer_size = 256;
    private static final String Root = "server/root";
    private static final byte [] buffer = new byte[buffer_size];
    private final Socket socket;


    public Handler(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {

        try(DataOutputStream os= new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream())

            ) {
                while (true) {
                    String fileName = is.readUTF();
                    log.debug("Received fileName: {}", fileName);
                    long size = is.readLong();
                    log.debug("File size: {}", size);
                    int read;
                    try (OutputStream osf = Files.newOutputStream(Paths.get(Root, fileName))) {
                        for (int i = 0; i < (size + buffer_size - 1) / buffer_size; i++) {
                            read = is.read(buffer);
                            osf.write(buffer,0,read);
                        }
                    } catch (Exception e) {
                        log.error("Problem with file");
                    }
                }

        }catch (Exception e){
            log.error("stacktrace: ",e);
        }

    }
}
