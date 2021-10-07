package com.geekbrains.netty;

import com.geekbrains.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileMessageHandler extends SimpleChannelInboundHandler <Command> {

    private static Path ROOT = Paths.get("server","root");

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new List_Response(ROOT));
        ctx.writeAndFlush(new PathResponse(ROOT));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        log.debug("received: {}",cmd.getType());
        switch (cmd.getType()){
            case FILE_REQUEST:{
                fileRequest(cmd);
            }
            case FILE_MESSAGE:{
                fileToServer(cmd);
                break;
            }
            case LIST_REQUEST:{
                ctx.writeAndFlush(new List_Response(ROOT));
            }
            case PATH_UP_REQUEST:
                if(ROOT.getParent() != null){
                    ROOT = ROOT.getParent();
                }
                ctx.writeAndFlush(new PathResponse(ROOT.toString()));
                ctx.writeAndFlush(new List_Response(ROOT));
                break;
            case PATH_IN_REQUEST:
                PathInRequest request = (PathInRequest) cmd;
                Path newPath = ROOT.resolve(request.getDir());
                if (Files.isDirectory(newPath)){
                    ROOT = newPath;
                    ctx.writeAndFlush(new PathResponse(ROOT.toString()));
                    ctx.writeAndFlush(new List_Response(ROOT));
                }
                break;
        }
    }
    public void fileToServer (Command cmd) throws IOException {
        FileMessage fileMessage = (FileMessage) cmd;
        Files.write(ROOT.resolve(fileMessage.getName()), fileMessage.getBytes());
    }
    public void fileRequest(Command cmd)throws Exception{
        FileRequest fileRequest =(FileRequest) cmd;
        FileMessage msg = new FileMessage(ROOT.resolve(fileRequest.getName()));

    }
}
