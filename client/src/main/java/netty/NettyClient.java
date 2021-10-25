package netty;
import com.geekbrains.*;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClient {

    private static  NettyClient INSTANCE;

    private static final int PORT = 8189;
    private static final String IP_ADDRESS = "localhost";

    private Callback callback;
    private SocketChannel channel;

    public static NettyClient getInstance(Callback callback) {
        if (INSTANCE == null) {
            INSTANCE = new NettyClient(callback);
        }
        else INSTANCE.setCallback(callback);
        return INSTANCE;
    }


    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
        CommandMessageHandler handler = channel.pipeline().get(CommandMessageHandler.class);
        handler.setCallback(callback);
    }

    public NettyClient(Callback callback) {
        this.callback = callback;
        Thread network = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel s) throws Exception {
                                channel = s;
                                channel.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        new CommandMessageHandler(callback)
                                );
                            }
                        });

                ChannelFuture future = bootstrap.connect(IP_ADDRESS, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        network.setDaemon(true);
        network.start();
    }

    public void close() {
        channel.close();
    }

    public void sendCommand(Command cmd) {
        channel.writeAndFlush(cmd);
    }
}