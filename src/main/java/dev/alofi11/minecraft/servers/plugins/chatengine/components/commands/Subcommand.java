package dev.alofi11.minecraft.servers.plugins.chatengine.components.commands;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.MainMessenger;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public abstract class Subcommand {

  protected final MainMessenger messenger;
  private final String name;
  private final Permission permission;

  protected Subcommand(@NotNull final ComponentsList componentsList, @NotNull String name,
      @NotNull String permissionName) {
    this.name = name;
    this.permission = componentsList.getNN(PermissionsManager.class)
        .registerPluginPermission(permissionName);
    this.messenger = componentsList.getNN(MainMessenger.class);
  }

  @NotNull
  String getName() {
    return name;
  }

  @NotNull
  Permission getPermission() {
    return permission;
  }

  protected abstract void execute(@NotNull CommandSender sender, @NotNull String[] args);

  @NotNull
  Map<Integer, List<String>> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
    return getEmptyPromptsMap();
  }

  @NotNull
  protected final Map<Integer, List<String>> getEmptyPromptsMap() {
    return new HashMap<>();
  }

  protected final void addPromptToPromptsMap(@NotNull final Map<Integer, List<String>> promptsMap,
      int index, @NotNull String prompt) {
    List<String> indexPrompts = promptsMap.getOrDefault(index, new ArrayList<>());
    indexPrompts.add(prompt);
    promptsMap.put(index, indexPrompts);
  }

}