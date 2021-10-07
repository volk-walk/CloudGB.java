package com.geekbrains;

import java.nio.file.Path;

public class PathResponse extends Command {
    private  String path;

    public PathResponse(String path){
        this.path = path;
        type = CommandType.PATH_RESPONSE;
    }

    public PathResponse(Path root) {
        super();
    }

    public String getPath() {
        return path;
    }


}
