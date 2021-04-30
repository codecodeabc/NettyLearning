package proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

public class ProxyServerHandle extends ChannelInboundHandlerAdapter {

    private Channel channel;

    /*private static final EventExecutorGroup EXECUTOR_GROUOP = new DefaultEventExecutorGroup(Runtime.getRuntime().availableProcessors() * 2);
    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    TrafficCounter trafficCounter = trafficHandler.trafficCounter();
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final long totalRead = trafficCounter.cumulativeReadBytes();
                    final long totalWrite = trafficCounter.cumulativeWrittenBytes();
                    System.out.println("total read: " + (totalRead >> 10) + " KB");
                    System.out.println("total write: " + (totalWrite >> 10) + " KB");
                    System.out.println("流量监控: " + System.lineSeparator() + trafficCounter);
                }
            }
        }).start();
    }
    private static final GlobalTrafficShapingHandler trafficHandler = new GlobalTrafficShapingHandler(EXECUTOR_GROUOP, 30, 30);*/



    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {

        if (channel == null) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop());
            bootstrap.channel(ctx.channel().getClass());
            bootstrap.handler(new ChannelInitializer() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ByteArrayEncoder());
                    ch.pipeline().addLast(new ByteArrayDecoder());
                    //ch.pipeline().addLast(new TraficCountHandler());
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


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (channel != null) {
            channel.close();
        }
    }
}
