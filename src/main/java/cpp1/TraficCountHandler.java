package cpp1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class TraficCountHandler extends ChannelDuplexHandler {

    static final AtomicLong writeBytes = new AtomicLong(0);
    static final AtomicLong readBytes = new AtomicLong(0);
/*
    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("流量监控: ");
                    System.out.println("total read: " + ((double) readBytes.longValue() / 1024 / 1024) + " KB");
                    System.out.println("total write: " + ((double) writeBytes.longValue() / 1034 / 1024) + " KB");
                }
            }
        }).start();
    }*/

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long bytes = calculateSize(msg);
        if (bytes > 0) {
            readBytes.addAndGet(bytes);
        }

        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        long bytes = calculateSize(msg);
        if (bytes > 0) {
            writeBytes.addAndGet(bytes);
        }
        ctx.write(msg, promise);
    }

    private long calculateSize(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).readableBytes();
        }
        if (msg instanceof ByteBufHolder) {
            return ((ByteBufHolder) msg).content().readableBytes();
        }
        return ((byte[]) msg).length;
    }
}
