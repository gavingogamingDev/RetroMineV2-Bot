package com.klashdevelopment.retrominev2;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class RemboBot extends ListenerAdapter {

    JDA bot;

    public static void main(String[] args) {
        new RemboBot(args[0]);
    }

    public RemboBot(String token) {
        bot = JDABuilder.createLight(token).addEventListeners(this).build();
        bot.updateCommands()
                .addCommands(
                        Commands.slash("status", "Update the RM Status.").addOption(OptionType.STRING, "status", "the status")
                )
    }

}
