package dev.alofi11.minecraft.servers.plugins.chatengine.commands.maincommand;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.commands.BaseExpandableCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MainCommand extends BaseExpandableCommand {

  public MainCommand(@NotNull final ComponentsList componentsList) {
    super(componentsList, "chatengine", "Main command of ChatEngine plugin.",
        "chatengine.maincommand.help", "ce");
  }

  @Override
  protected void help(@NotNull CommandSender sender) {
    messenger.sendMessage(sender, "main-command-help");
  }

  @Override
  protected void initializeSubcommands() {
    registerSubcommand(new ReloadSubcommand(componentsList));
  }

}