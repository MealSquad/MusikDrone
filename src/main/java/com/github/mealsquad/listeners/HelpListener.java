package com.github.mealsquad.listeners;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class HelpListener extends ListenerAdapter {

    private static final String WEBSITE_URL = "s3bucket.aws.website.location";
    private static final String GITHUB_URL = "https://github.com/MealSquad/MusikDrone";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(";help")) {
            event.getTextChannel().sendMessage(String.format(
                    "**MusikDrone Commands:**\n\n" +
                            "```" +
                            ";play <url>\n" +
                            ";skip\n" +
                            ";stop" +
                            "```\n\n" +
                            "**Github:** %s",
                    GITHUB_URL
            )).queue();
        }
    }
}
