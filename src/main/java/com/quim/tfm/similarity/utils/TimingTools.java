package com.quim.tfm.similarity.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;

public class TimingTools {

    private static final Logger logger = LoggerFactory.getLogger(TimingTools.class);

    private static HashMap<String, Long> actions;

    public static void startTimer(String name) {
        if (actions == null) {
            actions = new HashMap<>();
        }
        long startMillis = Calendar.getInstance().getTimeInMillis();
        actions.put(name, startMillis);

        logger.info(name + " action started");
    }

    public static void endTimer(String name) {
        long endMillis = Calendar.getInstance().getTimeInMillis();
        if (!actions.containsKey(name)) {
            logger.error("There is no action " + name + " started. Ignoring endTimer request");
        } else {
            long startMillis = actions.get(name);
            logger.info(name + " action ended. Execution time:\t" + (endMillis - startMillis) + "ms");
            actions.remove(name);
        }
    }

    public static void pauseTimer(String name) {
        long pauseMillis = Calendar.getInstance().getTimeInMillis();
        if (!actions.containsKey(name)) {
            logger.error("There is no action " + name + " started. Ignoring pauseTimer request");
        } else {
            long startMillis = actions.get(name);
            long accMillis = pauseMillis - startMillis;
            actions.put(name, accMillis);
            //logger.info(name + " action paused. Accumulated time:\t" + accMillis + "ms");
        }
    }

    public static void restartTimer(String name) {
        long restartMillis = Calendar.getInstance().getTimeInMillis();
        if (!actions.containsKey(name)) {
            logger.error("There is no action " + name + " started. Ignoring restartTimer request");
        } else {
            long startMillis = actions.get(name);
            long newStartTime = (restartMillis - startMillis);
            actions.put(name, newStartTime);
            //logger.info(name + " action restarted. New execution time:\t" + newStartTime + "ms");
        }
    }

}
