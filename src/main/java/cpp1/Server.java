package cpp1;

import com.cpp1.protobuf.PackDecoder;
import com.cpp1.protobuf.PackEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author lubeilin
 * @date 2021/3/29
 */
public class Server {



    public static void main(String[] args) {
        int num = Runtime.getRuntime().availableProcessors() * 2;
        AtomicInteger portb = new AtomicInteger(30000);
        EventLoopGroup bossGroup = new NioEventLoopGroup(num);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_RCVBUF, 1024*4)
                    .option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(4096));
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) {
                            /*ch.pipeline().addLast(new ByteArrayDecoder());
                            ch.pipeline().addLast(new ByteArrayEncoder());*/
                            //ch.pipeline().addLast(new CustomHandler());
                            ch.pipeline().addLast(new PackDecoder());
                            ch.pipeline().addLast(new PackEncoder());


                            //ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                private Channel channel;
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {

                                    if (channel == null) {
                                        Bootstrap bootstrap = new Bootstrap();
                                        bootstrap.group(ctx.channel().eventLoop());
                                        bootstrap.channel(ctx.channel().getClass());
                                        bootstrap.handler(new ChannelInitializer() {

                                            @Override
                                            protected void initChannel(Channel ch) throws Exception {
                                            /*    ch.pipeline().addLast(new ByteArrayDecoder());
                                                ch.pipeline().addLast(new ByteArrayEncoder());*/
                                                ch.pipeline().addLast(new PackDecoder());
                                                ch.pipeline().addLast(new PackEncoder());
                                                //ch.pipeline().addLast(new CustomHandler());


                                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                                    @Override
                                                    public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
                                                        ctx.channel().writeAndFlush(msg);
                                                    }

                                                    @Override
                                                    public void channelInactive(ChannelHandlerContext ctx0) throws Exception {
                                                        //System.out.println("断开连接");
                                                        ctx.close();
                                                    }
                                                });

                                            }
                                        });
                                        int port = new Random(1).nextInt(400) + 30000;
                                        channel = bootstrap.connect("127.0.0.1", 8999).addListener(new ChannelFutureListener() {
                                            @Override
                                            public void operationComplete(ChannelFuture future) throws Exception {
                                                if (future.isSuccess()) {
                                                    future.channel().writeAndFlush(msg);
                                                } else {
                                                    ctx.channel().close();
                                                }
                                            }
                                        }).channel();
                                        ChannelPipeline pipeline = channel.pipeline();
                                        System.out.println(pipeline);
                                    } else {
                                        channel.writeAndFlush(msg);
                                        ChannelPipeline pipeline = channel.pipeline();
                                        System.out.println(pipeline);
                                    }
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //System.out.println("proxy通道建立成功");
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("proxy通道被关闭");
                                    if (channel != null) {
                                        channel.close();
                                    }
                                }
                            });
                        }

                    });
            ChannelFuture f = b
                    .bind(8080)
                    .sync();
            System.out.println("已启动服务端 8080");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
