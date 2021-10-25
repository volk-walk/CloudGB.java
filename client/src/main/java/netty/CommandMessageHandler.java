package netty;
import com.geekbrains.*;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandMessageHandler extends SimpleChannelInboundHandler<Command> {
    private Callback callback;

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public CommandMessageHandler(Callback callback){
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command cmd) throws Exception {
        log.debug("received: {}", cmd);
        callback.call(cmd);
    }
}
