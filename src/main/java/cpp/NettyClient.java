package cpp;

import com.cpp1.protobuf.PackDecoder;
import com.cpp1.protobuf.PackEncoder;
import com.cpp1.protobuf.SmartCarProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();
        //创建客户端启动对象
        //注意客户端使用的不是 ServerBootstrap 而是 Bootstrap
        Bootstrap bootstrap = new Bootstrap();

        //设置相关参数
        bootstrap.group(group) //设置线程组
                .channel(NioSocketChannel.class) // 设置客户端通道的实现类(反射)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new PackDecoder());
                        ch.pipeline().addLast(new PackEncoder());
                        //ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });


        try {
            //客户端需要一个事件循环组
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
            System.out.println("客户端 ok..");
            Channel channel = channelFuture.channel();
            //对关闭通道事件  进行监听
            int k = 0;
            System.out.println("6666".getBytes().length);
            SmartCarProtocol smartCarProtocol = new SmartCarProtocol("6666".getBytes().length,
                    "6666".getBytes());

            channelFuture.channel().writeAndFlush(smartCarProtocol);
            //System.out.println("666");
        } catch (InterruptedException e) {
            System.exit(0);
            System.out.println("===============");
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }


    }

}
