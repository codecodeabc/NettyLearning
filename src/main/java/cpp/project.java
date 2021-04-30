package cpp;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.net.InetSocketAddress;

public class project {

    private static BiMap<String,String> map = HashBiMap.create();


    public static void main(String[] params) throws InterruptedException {

        map.put("10.32.17.20","10.32.88.178");

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // TODO Auto-generated method stub
                        ch.pipeline().addLast(new ByteArrayDecoder());
                        ch.pipeline().addLast(new ByteArrayEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            private Channel channel;

                            @Override
                            public void channelActive(ChannelHandlerContext ctx)
                                    throws Exception {
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (channel == null) {
                                    Bootstrap bootstrap = new Bootstrap();
                                    bootstrap.group(ctx.channel().eventLoop());
                                    bootstrap.channel(ctx.channel().getClass());
                                    bootstrap.handler(new ChannelInitializer() {

                                        @Override
                                        protected void initChannel(Channel ch) throws Exception {
                                            ch.pipeline().addLast(new ByteArrayDecoder());
                                            ch.pipeline().addLast(new ByteArrayEncoder());
                                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                                @Override
                                                public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
                                                    ctx.channel().writeAndFlush(msg);
                                                }

                                                @Override
                                                public void channelInactive(ChannelHandlerContext ctx0) throws Exception {
                                                    ctx.close();
                                                }
                                            });

                                        }
                                    });
                                    channel = bootstrap.connect("10.32.88.178", 3389).addListener(new ChannelFutureListener() {
                                        @Override
                                        public void operationComplete(ChannelFuture future) throws Exception {
                                            if (future.isSuccess()) {
                                                future.channel().writeAndFlush(msg);
                                            } else {
                                                ctx.channel().close();
                                            }
                                        }
                                    }).channel();
                                } else {
                                    channel.writeAndFlush(msg);
                                }
                            }
                        });

                    }
                });

        bootstrap.bind(new InetSocketAddress(8080)).channel().closeFuture().sync();
    }
}
