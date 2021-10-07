package com.geekbrains;

public class PathResponse extends Command {
    private final String path;

    public PathResponse(String path){
        this.path = path;
        type = CommandType.PATH_RESPONSE;
    }

    public String getPath() {
        return path;
    }


}
