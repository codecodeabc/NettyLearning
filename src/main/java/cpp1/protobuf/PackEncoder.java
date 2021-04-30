package cpp1.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义编码器
 * @author linsh
 *
 */
public class PackEncoder extends MessageToByteEncoder<SmartCarProtocol>{

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          SmartCarProtocol smartCarProtocol, ByteBuf byteBuf) throws Exception {
        System.out.println("SmartCarEncoder#encode");
        byteBuf.writeInt(smartCarProtocol.getHeaderStartFlag());
        byteBuf.writeInt(smartCarProtocol.getLength());
        byteBuf.writeBytes(smartCarProtocol.getContent());
    }
}