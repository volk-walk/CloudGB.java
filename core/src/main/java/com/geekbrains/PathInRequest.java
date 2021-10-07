package com.geekbrains;

public class PathInRequest extends Command {
    private final String dir;


    public PathInRequest (String dir){
        this.dir =dir;
        type = CommandType.PATH_IN_REQUEST;
    }

    public String getDir() {
        return dir;
    }
}
