package com.github.mealsquad;

import com.github.mealsquad.listeners.CommandListener;
import com.github.mealsquad.listeners.HelpListener;
import com.github.mealsquad.listeners.PingListener;
import com.github.mealsquad.listeners.ReadyListener;
import com.github.mealsquad.utility.ConfigReader;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;

public class Bot {

    private static final Logger log = LogManager.getFormatterLogger();

    private void start() {
        log.info("Starting MusikDrone");
        try {
            new JDABuilder(AccountType.BOT)
                    .setToken(ConfigReader.readProperties("token"))
                    .addEventListener(
                            new ReadyListener(),
                            new PingListener(),
                            new HelpListener(),
                            new CommandListener())
                    .build()
                    .awaitReady();
        } catch (LoginException le) {
            log.fatal("JDA failed to login - check token validity");
            System.exit(1);
        } catch (InterruptedException ie) {
            log.fatal("JDA was interrupted while waiting");
            System.exit(1);
        }
    }

    public static void main(String[] args){
        new Bot().start();
    }


}
