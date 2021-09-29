package com.geekbrains.netty;

import com.geekbrains.Command;
import com.geekbrains.FileMessage;
import com.geekbrains.List_Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMessageHandler extends SimpleChannelInboundHandler<Command> {

    private static final Path ROOT = Paths.get("server","root");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        switch (cmd.getType()){

            case FILE_MESSAGE:{
                fileToServer(cmd);
                break;
            }
            case LIST_REQUEST:{
                ctx.writeAndFlush(new List_Response(ROOT));
            }



        }
    }
    public void fileToServer (Command cmd) throws IOException {
        FileMessage fileMessage = (FileMessage) cmd;
        Files.write(ROOT.resolve(fileMessage.getName()), fileMessage.getBytes()
        );
    }
}
