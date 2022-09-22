package mcp.mobius.opis.network.rcon.nexus;

import io.nettyopis.bootstrap.Bootstrap;
import io.nettyopis.channel.*;
import io.nettyopis.channel.nio.NioEventLoopGroup;
import io.nettyopis.channel.socket.SocketChannel;
import io.nettyopis.channel.socket.nio.NioSocketChannel;
import io.nettyopis.handler.ssl.SslContext;
import io.nettyopis.handler.ssl.util.InsecureTrustManagerFactory;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.util.Properties;
import javax.net.ssl.SSLException;
import mcp.mobius.opis.modOpis;

public class NexusClient implements Runnable {

    class ChannelInit extends ChannelInitializer<SocketChannel> {
        private final SslContext sslCtx;

        public ChannelInit(SslContext sslCtx) {
            this.sslCtx = sslCtx;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(), NexusClient.instance.host, NexusClient.instance.port));
            // ch.pipeline().addLast(new ReadTimeoutHandler(10));
            ch.pipeline().addLast(new NexusHandshakeDecoder());
            ch.pipeline().addLast(new NexusHandshakeHandler());
            // ch.pipeline().addLast(new JdkZlibDecoder());
            // ch.pipeline().addLast(new JdkZlibEncoder());
            // ch.pipeline().addLast(new WriteTimeoutHandler(5));

            // ch.pipeline().addLast(new NexusMsgDecoder());
            // ch.pipeline().addLast(new NexusInboundHandler());
        }
    }

    String reverseprop = "opis.properties";

    String host = "localhost";
    String uuid = "";
    String pass = "";
    Integer port = 8013;
    Boolean active = false;
    public boolean reconnect = false;
    public boolean shouldRetry = true;
    public WeakReference<ChannelHandlerContext> ctx;

    public static final NexusClient instance = new NexusClient();

    private NexusClient() {}

    @Override
    public void run() {
        this.readConfig(this.reverseprop);
        if (!this.active) return;
        if (this.uuid.equals("")) {
            modOpis.log.error("UUID not set properly.");
            return;
        }

        while (this.connect()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ;
    }

    private boolean connect() {
        if (this.reconnect) modOpis.log.info(String.format("Reconnecting to HydraOpis %s:%s", this.host, this.port));
        else modOpis.log.info(String.format("Connecting to HydraOpis %s:%s", this.host, this.port));

        SslContext sslCtx = null;
        try {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } catch (SSLException e) {
            this.handleException(e);
        }

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInit(sslCtx));
            ChannelFuture f = b.connect(this.host, this.port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            this.handleException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }

        return this.shouldRetry;
    }

    private void handleException(Throwable cause) {
        if (cause instanceof ConnectException && cause.getMessage().contains("Connection refused")) {
            modOpis.log.warn("HydraOpis : Connection refused by remote server. Server is down ?");
        } else {
            modOpis.log.error("Error while connecting to Nexus Server");
            cause.printStackTrace();
        }
    }

    private void readConfig(String filename) {
        if (!new File(filename).exists()) writeConfig(filename);

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(filename);
            prop.load(input);
            input.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.host = prop.getProperty("host");
        this.port = Integer.valueOf(prop.getProperty("port"));
        this.active = Boolean.valueOf(prop.getProperty("active"));
        this.uuid = prop.getProperty("uuid");
        this.pass = prop.getProperty("passphrase");
    }

    private void writeConfig(String filename) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream(filename);

            prop.setProperty("host", host);
            prop.setProperty("port", this.port.toString());
            prop.setProperty("active", this.active.toString());
            prop.setProperty("uuid", this.uuid);
            prop.setProperty("passphrase", this.pass);
            prop.store(output, null);

            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
