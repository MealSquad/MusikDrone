package com.github.mealsquad;

import com.github.mealsquad.utility.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Bot {

    private static final Logger log = LogManager.getFormatterLogger();

    private void start() {
        log.info("Starting MusikDrone");

        DiscordApi api = new DiscordApiBuilder().setToken(ConfigReader.readProperties("token")).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });
    }

    public static void main(String[] args) {
        Bot me = new Bot();
        me.start();
    }
}
