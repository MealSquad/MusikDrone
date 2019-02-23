package com.github.mealsquad.listeners;


import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadyListener implements EventListener {

    private static final Logger log = LogManager.getFormatterLogger();

    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            log.info("API is ready!");
        }
    }
}
