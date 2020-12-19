package dev.alofi11.minecraft.servers.plugins.chatengine.commands.maincommand;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.chatmenus.MenusManager;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.commands.Subcommand;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.MainConfigWrapper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

final class ReloadSubcommand extends Subcommand {

  private final MainConfigWrapper config;
  private final ComponentsList componentsList;

  ReloadSubcommand(@NotNull final ComponentsList componentsList) {
    super(componentsList, "reload", "reload");
    this.componentsList = componentsList;
    this.config = componentsList.getNN(MainConfigWrapper.class);
  }

  @Override
  protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
    config.reload();
    reinitializeMenusManager();
    messenger.reload();
    messenger.sendMessage(sender, "reloaded");
  }

  private void reinitializeMenusManager() {
    MenusManager menusManager = componentsList.getNN(MenusManager.class);
    menusManager.onDisable();
    componentsList.remove(menusManager);
    menusManager = new MenusManager(componentsList);
    componentsList.add(menusManager);
  }

}