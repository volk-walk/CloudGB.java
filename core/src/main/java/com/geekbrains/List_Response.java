package com.geekbrains;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class List_Response extends Command {



    private final List <String> buf;

    public List_Response (Path path) throws IOException {

        buf = Files.list(Paths.get(String.valueOf(path))).map(p->p.getFileName().toString()).collect(Collectors.toList()
        );
        type = CommandType.LIST_RESPONSE;
    }

    public List<String> getBuf() {
        return buf;
    }
}
