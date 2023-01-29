package mcp.mobius.opis.data.profilers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import net.minecraft.server.MinecraftServer;

public class DeadManSwitch implements Runnable {

    public static DeadManSwitch instance;
    private Thread serverThread = null;
    private MinecraftServer server = null;
    private AtomicLong timer = new AtomicLong();
    private AtomicLong timerPrev = new AtomicLong();
    private Long lastTested = 0L;
    private long nderps = 0L;
    private BlockingQueue<Long> queue = new ArrayBlockingQueue<Long>(2);

    public DeadManSwitch(MinecraftServer server, Thread serverThread) {
        this.server = server;
        this.serverThread = serverThread;
        DeadManSwitch.instance = this;
    }

    public void setTimer(long time) {
        this.timerPrev.set(this.timer.get());
        this.timer.set(time);
    }

    public long getTimerDelta() {
        return this.timer.get() - this.timerPrev.get();
    }

    @Override
    public void run() {

        System.out.printf("Starting Dead Man Switch\n");

        // while (server.isServerRunning()){
        while (true) {

            try {
                if (this.lastTested != this.timer.get()) {

                    // if (this.getTimerDelta() / 1000. / 1000. > 1000.){
                    if (this.getTimerDelta() / 1000. / 1000. > 300.) {
                        System.out.printf(
                                "==== Main thread is staled ! %.3f ms [ %d ]====\n",
                                this.getTimerDelta() / 1000. / 1000.,
                                this.nderps);

                        this.nderps += 1;

                        if (this.nderps > 10) {
                            /*
                             * this.nderps = 0; this.serverThread.interrupt(); System.out.printf("Restarting\n");
                             * Thread.sleep(1000); MinecraftServer.main(MinecraftServer.arguments);
                             * System.out.printf("Restarted\n"); return;
                             */
                            for (Thread thread : Thread.getAllStackTraces().keySet()) {
                                if (thread != Thread.currentThread()) {
                                    try {
                                        thread.interrupt();
                                    } catch (Exception e) {
                                        System.out.printf("%s\n", e);
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
                this.lastTested = this.timer.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            LockSupport.parkNanos(1L * 1000L * 1000L);
        }
    }

    public static DeadManSwitch startDeadManSwitch(MinecraftServer server) {
        DeadManSwitch deadManSwitch = new DeadManSwitch(server, Thread.currentThread());
        Thread deadManSwitchThrd = new Thread(deadManSwitch);
        deadManSwitchThrd.setName("Dead Man Switch");
        deadManSwitchThrd.setPriority(Thread.MAX_PRIORITY);
        // deadManSwitchThrd.setDaemon(false);
        deadManSwitchThrd.start();
        return deadManSwitch;
    }
}
