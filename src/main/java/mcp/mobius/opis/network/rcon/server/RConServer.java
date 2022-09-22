package mcp.mobius.opis.network.rcon.server;

import io.nettyopis.bootstrap.ServerBootstrap;
import io.nettyopis.channel.ChannelFuture;
import io.nettyopis.channel.ChannelInitializer;
import io.nettyopis.channel.ChannelOption;
import io.nettyopis.channel.EventLoopGroup;
import io.nettyopis.channel.nio.NioEventLoopGroup;
import io.nettyopis.channel.socket.SocketChannel;
import io.nettyopis.channel.socket.nio.NioServerSocketChannel;
import io.nettyopis.handler.ssl.SslContext;
import io.nettyopis.handler.ssl.util.SelfSignedCertificate;
import io.nettyopis.handler.timeout.ReadTimeoutHandler;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;
import mcp.mobius.opis.modOpis;

public class RConServer implements Runnable {

    class ChannelInit extends ChannelInitializer<SocketChannel> {
        private final SslContext sslCtx;

        public ChannelInit(SslContext sslCtx) {
            this.sslCtx = sslCtx;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {

            ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()));
            ch.pipeline().addLast(new ReadTimeoutHandler(10));
            ch.pipeline().addLast(new RConHandshakeDecoder());
            ch.pipeline().addLast(new RConHandshakeHandler());
            // ch.pipeline().addLast(new JdkZlibDecoder());
            // ch.pipeline().addLast(new JdkZlibEncoder());
            // ch.pipeline().addLast(new RconMsgDecoder());
            // ch.pipeline().addLast(new RConInboundHandler());

        }
    }

    public static final RConServer instance = new RConServer();

    private int port = -1;

    private RConServer() {
        this.port = modOpis.rconport;
    }

    @Override
    public void run() {
        if (!modOpis.rconactive) return;

        modOpis.log.info("Starting Opis remote control server.");

        SelfSignedCertificate ssc = null;
        SslContext sslCtx = null;
        try {
            ssc = new SelfSignedCertificate();
            sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        } catch (CertificateException e1) {
            e1.printStackTrace();
        } catch (SSLException e1) {
            e1.printStackTrace();
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInit(sslCtx))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync(); // (7)

            f.channel().closeFuture().sync();

        } catch (Exception e) {
            modOpis.log.error("Error in Opis remote control server.");
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
