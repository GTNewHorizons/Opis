package mcp.mobius.opis.helpers;

import cpw.mods.fml.relauncher.Side;

public class Helpers {

    public static Side getEffectiveSide() {
        Thread thr = Thread.currentThread();

        if ((thr.getName().equals("Server thread")) || (thr.getName().contains("Netty IO"))
                || (thr.getName().startsWith("nioEventLoopGroup"))) {
            return Side.SERVER;
        }

        return Side.CLIENT;

        /*
         * if ((thr instanceof ThreadMinecraftServer) || (thr instanceof ServerListenThread) || (thr instanceof
         * TcpWriterThread) || (thr instanceof TcpReaderThread)) { return Side.SERVER; } return Side.CLIENT;
         */
    }
}
