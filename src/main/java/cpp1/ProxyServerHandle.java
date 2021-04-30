package cpp1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;


public class ProxyServerHandle extends ChannelInboundHandlerAdapter {

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
                    ch.pipeline().addLast(new ByteArrayDecoder());
                    ch.pipeline().addLast(new ByteArrayEncoder());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
                            ctx.channel().writeAndFlush(msg);
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx0) throws Exception {
                            System.out.println("断开连接");
                            ctx.close();
                        }
                    });

                }
            });
            int port =  30000;
            channel = bootstrap.connect("127.0.0.1", 8888).addListener(new ChannelFutureListener() {
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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("proxy通道建立成功");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("proxy通道被关闭");
        if (channel != null) {
            channel.close();
        }
    }
}
