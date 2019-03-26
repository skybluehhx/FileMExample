package com.lin;

import com.lin.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * listener 执行器
 *
 * @author jianglinzou
 * @date 2019/3/21 上午11:41
 */
public class ListenerActuator {

    public static Logger logger = LoggerFactory.getLogger(ListenerActuator.class);

    static final ListenerActuator actuator = new ListenerActuator();

    public static ListenerActuator getInstance() {
        return actuator;
    }

    private ListenerActuator() {

    }

    public boolean run(List<FileListener> listeners, Event event) {
        boolean result = false;
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i) != null && listeners.get(i).accept()) { //如果可以接受
                Object runResult = runListener(listeners.get(i), event);
                if (runResult != null && runResult instanceof Boolean) {
                    result |= ((Boolean) result);
                }
            }
        }
        return result;
    }

    private Object runListener(FileListener fileListener, Event event) {

        EventType eventType = event.getEventType();
        try {
            switch (eventType) {
                case DELETE:
                    fileListener.onFileDelete(event.getOrigin());
                    logger.info("fileListener:{} has been execute successfully,the event:{}", fileListener.name(), event);
                    break;
                case CREATE:
                    fileListener.onFileCreate(event.getOrigin());
                    logger.info("fileListener:{} has been execute successfully,the event:{}", fileListener.name(), event);
                    break;
                case CHANGE:
                    fileListener.onFileChange(event.getOrigin());
                    logger.info("fileListener:{} has been execute successfully,the event:{}", fileListener.name(), event);
                    break;
            }
            return true;
        } catch (Throwable e) {
            logger.warn("fail to run fileListener:{} for event:{}", fileListener.name(), event);
            return false;
        }
    }


}
