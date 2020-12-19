package dev.alofi11.minecraft.servers.plugins.chatengine.components.commands;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.DisableSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;

public final class CommandsManager implements DisableSupport {

  private static final String FALLBACK_PREFIX = "ChatEngine";
  private final List<BaseCommand> registeredCommands = new ArrayList<>();
  private final SimpleCommandMap commandMap;

  public CommandsManager() {
    this.commandMap = (SimpleCommandMap) getCommandMap();
  }

  public void registerCommand(@NotNull BaseCommand command) {
    commandMap.register(FALLBACK_PREFIX, command);
    syncCommands();
    registeredCommands.add(command);
  }

  public void unregisterCommand(@NotNull BaseCommand registeredCommand) {
    unregisterCommand(registeredCommand.getName(), registeredCommand.getAliases());
    syncCommands();
    registeredCommands.remove(registeredCommand);
  }

  public void unregisterAllCommands() {
    registeredCommands.forEach((registeredCommand) -> unregisterCommand(registeredCommand.getName(),
        registeredCommand.getAliases()));
    syncCommands();
    registeredCommands.clear();
  }

  private void syncCommands() {
    String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
    try {
      Class<?> server = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer");
      Method syncCommands = server.getDeclaredMethod("syncCommands");
      syncCommands.setAccessible(true);
      syncCommands.invoke(Bukkit.getServer());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @NotNull
  private CommandMap getCommandMap() {
    CommandMap commandMap = null;

    try {
      final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
      commandMapField.setAccessible(true);
      commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
    } catch (IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }

    assert commandMap != null;
    return commandMap;
  }

  private void unregisterCommand(@NotNull String commandName, @NotNull List<String> aliases) {
    final Map<String, Command> knownCommands = commandMap.getKnownCommands();
    knownCommands.remove(commandName);
    aliases.forEach(knownCommands::remove);
  }

  @Override
  public void onDisable() {
    unregisterAllCommands();
  }

}