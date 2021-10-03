package com.geekbrains;

public class FileRequest extends Command {

    private final String name;


    public FileRequest (String name){
        this.name = name;
        type = CommandType.FILE_REQUEST;
    }
    public String getName() {
        return name;
    }
}
