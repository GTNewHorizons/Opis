package mcp.mobius.opis.data.profilers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Clock {

    public interface IClock {
        void start();

        void stop();

        long getDelta();
    }

    public static ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();
    // public static boolean canThreadCPU = threadMX.isCurrentThreadCpuTimeSupported();
    public static boolean canThreadCPU = false;

    public class ClockMX implements IClock {
        public long startTime = 0;
        public long timeDelta = 0;

        @Override
        public void start() {
            this.startTime = threadMX.getCurrentThreadCpuTime();
        }

        @Override
        public void stop() {
            this.timeDelta = threadMX.getCurrentThreadCpuTime() - this.startTime;
        }

        @Override
        public long getDelta() {
            return this.timeDelta;
        }
    }

    public class ClockNano implements IClock {
        public long startTime = 0;
        public long timeDelta = 0;

        @Override
        public void start() {
            this.startTime = System.nanoTime();
        }

        @Override
        public void stop() {
            this.timeDelta = System.nanoTime() - this.startTime;
        }

        @Override
        public long getDelta() {
            return this.timeDelta;
        }
    }

    public static IClock getNewClock() {
        if (Clock.canThreadCPU) return new Clock().new ClockMX();
        else return new Clock().new ClockNano();
    }
}
