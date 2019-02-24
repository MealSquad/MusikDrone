package com.github.mealsquad.listeners;


import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PingListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals(";ping")) {
            event.getTextChannel().sendMessage("Pong!").queue();
        }
    }
}
