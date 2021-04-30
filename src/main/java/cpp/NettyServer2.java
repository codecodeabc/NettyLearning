package cpp;

import com.cpp1.protobuf.PackDecoder;
import com.cpp1.protobuf.PackEncoder;
import com.cpp1.protobuf.SmartCarProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer2 {

    public static void main(String[] params) throws InterruptedException {
        //创建BossGroup 和 WorkerGroup
        //说明
        //1. 创建两个线程组 bossGroup 和 workerGroup
        //2. bossGroup 只是处理连接请求 , 真正的和客户端业务处理，会交给 workerGroup完成
        //3. 两个都是无限循环
        //4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数
        //   默认实际 cpu核数 * 2
       /* for(int i = 0;i<400;i++){
            final int finalI = i;
            new Thread(() ->{*/
        int finalI = 8999;
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup(); //8

                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup) //设置两个线程组
                            .channel(NioServerSocketChannel.class) //使用NioSocketChannel 作为服务器的通道实现
                            .option(ChannelOption.SO_RCVBUF, 1024 * 4)
                            .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(4096))
                            .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道初始化对象(匿名对象)
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(new PackDecoder());
                                    ch.pipeline().addLast(new PackEncoder());
                                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            System.out.println(finalI + ":接收到数据：" + new String(((SmartCarProtocol)msg).getContent()));
                                        }

                                        @Override
                                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            cause.printStackTrace();
                                            ctx.close();
                                        }
                                    });
                                }
                            });

                    final ChannelFuture cf = bootstrap.bind(finalI).sync();
                    cf.addListener((ChannelFutureListener) future -> {
                        if (cf.isSuccess()) {
                            System.out.println("监听端口 " + (finalI+30000) + " 成功");
                        } else {
                            System.out.println("监听端口 " + (finalI+30000)  + " 失败");
                        }
                    });
                    //对关闭通道事件  进行监听
                    cf.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }

         /*   }).start();
        }*/

    }

}
