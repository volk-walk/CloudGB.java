package com.geekbrains;

import io.netty.handler.codec.socksx.v4.Socks4CommandType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMessage extends Command {

    private final String filename;
    private final byte[] bufs;


    public FileMessage (Path path) throws IOException {
        filename=path.getFileName().toString();
        bufs = Files.readAllBytes(path);
        type = CommandType.FILE_MESSAGE;
    }

    public String getName() {
        return filename;
    }

    public byte[] getBytes() {
        return bufs;
    }
}
