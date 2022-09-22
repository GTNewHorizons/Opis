package mcp.mobius.opis.network.rcon.nexus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import mcp.mobius.opis.events.EventTimer;
import mcp.mobius.opis.network.enums.Message;

public class EventTimerRing {
    Map<Message, EventTimer> ring = new HashMap<Message, EventTimer>();
    Properties prop;

    public EventTimerRing() {
        ring.put(Message.NEXUS_UUID, new EventTimer(0));
    }

    public EventTimerRing(String config) {
        this.prop = readConfig(config);

        for (Object o : prop.keySet()) {
            String msg = (String) o;
            ring.put(Message.valueOf(msg), new EventTimer(Long.valueOf(prop.getProperty(msg))));
        }
    }

    public EventTimerRing(Properties prop) {
        this.prop = prop;

        for (Object o : prop.keySet()) {
            String msg = (String) o;
            ring.put(Message.valueOf(msg), new EventTimer(Long.valueOf(prop.getProperty(msg))));
        }
    }

    public boolean isDone(Message msg) {
        if (!ring.containsKey(msg)) return false;
        return ring.get(msg).isDone();
    }

    public Properties getProp() {
        return this.prop;
    }

    private Properties readConfig(String filename) {
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

        return prop;
    }
}
