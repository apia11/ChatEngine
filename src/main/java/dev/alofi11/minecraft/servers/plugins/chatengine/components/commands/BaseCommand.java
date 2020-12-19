package dev.alofi11.minecraft.servers.plugins.chatengine.components.commands;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.MainMessenger;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.permissions.PermissionsManager;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCommand extends Command {

  protected final MainMessenger messenger;
  protected final Permission permission;

  protected BaseCommand(@NotNull final ComponentsList componentsList, @NotNull String name,
      @NotNull String description, @NotNull String permissionName, @NotNull String... aliases) {
    super(name, description, "", Arrays.asList(aliases));
    this.messenger = componentsList.getNN(MainMessenger.class);
    this.permission = componentsList.getNN(PermissionsManager.class).register(permissionName);
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String label,
      @NotNull String[] args) {
    if (hasPermission(sender, permission)) {
      execute(sender, args);
    }

    return true;
  }

  protected abstract void execute(@NotNull CommandSender sender, @NotNull String[] args);

  protected boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission) {
    boolean hasPermission = sender.hasPermission(permission);
    if (!hasPermission) {
      messenger.sendMessage(sender, "no-perms", "permission", permission.getName());
    }

    return hasPermission;
  }

}