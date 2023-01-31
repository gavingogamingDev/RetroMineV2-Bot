package com.klashdevelopment.retrominev2;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemboBot extends ListenerAdapter {

    JDA bot;

    public static void main(String[] args) {
        new RemboBot(args[0]);
    }

    public RemboBot(String token) {
        bot = JDABuilder.createLight(token).addEventListeners(this).build();
        bot.updateCommands()
                .addCommands(
                        Commands.context(Command.Type.USER, "Get user avatar"),
                        Commands.slash("status", "Update the RM Status.").addOption(OptionType.STRING, "current", "the status", true, true)
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER)),
                        Commands.slash("announce", "Announce something to the server.")
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))
                )
                .queue();
    }
    private String[] words = new String[]{"Green - All systems online :green_circle:", "Yellow - Partially offline :yellow_circle:", "Red - Fully offline :red_circle:"};

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("status") && event.getFocusedOption().getName().equals("current")) {
            List<Command.Choice> options = Stream.of(words)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        if (event.getName().equals("Get user avatar")) {
            event.reply("Avatar: " + event.getTarget().getEffectiveAvatarUrl()).setEphemeral(true).queue();
        }
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        if (event.getModalId().equals("announcer")) {
            String title = event.getValue("title").getAsString();
            String content = event.getValue("content").getAsString();

            event.getGuild().getTextChannelById(1068069681824550912L).sendMessage(new MessageCreateBuilder()
                    .setContent("<@&1068407554989887578>")
                    .addEmbeds(new EmbedBuilder()
                            .setTitle(title)
                            .setDescription(content)
                            .setFooter("This announcement was sent by " + event.getUser().getAsTag())
                            .build())
                    .build()).queue();

            event.reply("Announcement added!").setEphemeral(true).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("announce")) {
            TextInput title = TextInput.create("title", "Title", TextInputStyle.SHORT)
                    .setRequired(true)
                    .setPlaceholder("A title for your announcement")
                    .build();

            TextInput body = TextInput.create("content", "Content", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("The content of the announcement.")
                    .build();

            Modal modal = Modal.create("announcer", "Announcement")
                    .addActionRows(ActionRow.of(title), ActionRow.of(body))
                    .build();

            event.replyModal(modal).queue();
        }
        if(event.getName().equalsIgnoreCase("status")) {
            event.getGuild().getTextChannelById("1068746709741600839").sendMessageEmbeds(new EmbedBuilder()
                            .setTitle("Status")
                            .setDescription("Here is the current status of RetroMine:\n"+
                                            (event.getOption("current").getAsString().equals(words[0]) ? ":green_circle: - All systems online."
                                                    : (event.getOption("current").getAsString().equals(words[1]) ? ":yellow_circle: - Atleast one system is down."
                                            : ":red_circle: - Uh oh! All of RetroMine is offline."))
                                    )
                    .build()).queue();
            event.reply("Completed").setEphemeral(true).queue();
        }
    }
}
