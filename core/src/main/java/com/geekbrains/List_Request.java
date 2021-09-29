package com.geekbrains;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class List_Request extends Command {

    public List_Request () throws IOException {
        type = CommandType.LIST_REQUEST;
    }
}
