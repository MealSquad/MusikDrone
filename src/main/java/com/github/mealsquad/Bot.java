package com.github.mealsquad;

import com.github.mealsquad.listeners.PingListener;
import com.github.mealsquad.listeners.ReadyListener;
import com.github.mealsquad.utility.ConfigReader;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;

public class Bot {

    private static final Logger log = LogManager.getFormatterLogger();

    private void start() {
        log.info("Starting MusikDrone");

        try {
            JDA jda = new JDABuilder(ConfigReader.readProperties("token"))
                    .addEventListener(new ReadyListener(), new PingListener())
                    .build();
            
            jda.awaitReady();
        } catch (LoginException le) {
            log.fatal("Failure to login");
        } catch (InterruptedException ie){
            log.info("Interrupted Exception");
        }
    }

    public static void main(String[] args) {
        Bot me = new Bot();
        me.start();
    }
}
