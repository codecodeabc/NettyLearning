package cpp1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.Callable;

public class NettyHandler extends ChannelInboundHandlerAdapter {
    static EventExecutorGroup group = new DefaultEventExecutorGroup(3);//业务线程组
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().config().setWriteBufferHighWaterMark(1024*1024*10);//对此管道设置10兆高水位
        ctx.channel().config().setWriteBufferLowWaterMark(1024*1024*3);//对此管道设置3兆低水位

    }
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("触发了高低水位改变,回调函数");
        if(ctx.channel().isWritable()==true) {//6.恢复至低水位了
            //7.把记录下来还没发送的数据 继续发送给客户端.
        }
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        group.submit(new Callable<Object>(){
            @Override
            public Object call() throws Exception {
                while(true) {
                    if(ctx.channel().isWritable()) {
                        ctx.writeAndFlush("假设这是11兆数据");//1.假设一直无限写11兆的数据发送
                        //2.那么在第二次执行此方法时,ctx.channel().isWritable()会返回false,就无法进入了
                        //客户端读取速度假设是每秒1兆,我们的高水位设置了10兆,发送的是11兆
                        //那么要恢复到低水位,需要8秒的时间.在此我们无法发送数据,都会执行else的输出语句
                        //3.channelWritabilityChanged回调事件.
                        //水位一旦改变就会执行channelWritabilityChanged
                        //4.while不能再nio线程中使用,不然回调的函数执行线程就在nio,会导致低水位回调无法执行.
                        //所以我们在group中执行. 毕竟把回调线程阻塞了肯定就不行
                    }else {
                        System.out.println("触发了高水位");
                        //5.记录下来还有什么数据没发送,在channelWritabilityChanged变为低水位的时候继续发送.
                    }
                }
            }
        });
    }
}
