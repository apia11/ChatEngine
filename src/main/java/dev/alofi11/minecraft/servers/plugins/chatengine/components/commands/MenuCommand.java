package dev.alofi11.minecraft.servers.plugins.chatengine.components.commands;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.chatmenus.Menu;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.permissions.PermissionsManager;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public final class MenuCommand extends BaseCommand {

  private final Menu menu;
  private final PermissionsManager permissionsManager;

  public MenuCommand(@NotNull final Menu menu, @NotNull final ComponentsList componentsList,
      @NotNull String commandName, @NotNull String permissionName, @NotNull List<String> aliases) {
    super(componentsList, commandName, "", permissionName, aliases.toArray(new String[0]));
    this.menu = menu;
    this.permissionsManager = componentsList.getNN(PermissionsManager.class);
  }

  @Override
  protected void execute(@NotNull CommandSender sender, @NotNull String[] args) {
    if (isPlayer(sender)) {
      menu.open((Player) sender);
    }
  }

  public boolean isPlayer(@NotNull CommandSender sender) {
    return sender instanceof Player;
  }

  public void onDisable() {
    permissionsManager.unregister(permission);
  }

  @NotNull
  public Permission getOpenPermission() {
    return permission;
  }

}