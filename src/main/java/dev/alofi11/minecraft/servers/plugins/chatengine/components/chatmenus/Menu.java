package dev.alofi11.minecraft.servers.plugins.chatengine.components.chatmenus;

import static org.apache.commons.lang.StringUtils.lowerCase;

import dev.alofi11.minecraft.servers.plugins.chatengine.ChatEnginePlugin;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.commands.CommandsManager;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.commands.MenuCommand;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.SimpleConfigWrapper;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.MenuMessenger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Menu implements Listener {

  private static final Pattern CHANGE_PAGE_ACTION_PATTERN = Pattern
      .compile("^chp\\([a-zA-Z0-9_-]{1,30}\\)$");
  private static final Pattern CHANGE_MENU_ACTION_PATTERN = Pattern
      .compile("^chm\\([a-zA-Z0-9_-]{1,30}\\)$");
  private static final String CLOSE_MENU_ACTION_STRING = "close";
  private final String name;
  private final List<Player> clients = new ArrayList<>();
  private final SimpleConfigWrapper config;
  private final ChatBuffer chatBuffer;
  private final CommandsManager commandsManager;
  private final MenusManager menusManager;
  private final boolean successfulInitialized;
  private MenuCommand command;
  private MenuMessenger messenger;

  Menu(@NotNull String name, @NotNull final SimpleConfigWrapper config,
      @NotNull final ChatBuffer chatBuffer, @NotNull final ComponentsList componentsList,
      @NotNull final MenusManager menusManager) {
    this.name = name;
    this.config = config;
    this.chatBuffer = chatBuffer;
    this.commandsManager = componentsList.getNN(CommandsManager.class);
    this.menusManager = menusManager;
    final ChatEnginePlugin plugin = componentsList.getNN(ChatEnginePlugin.class);
    Logger logger = plugin.getLogger();

    if (hasPages()) {
      command = buildCommand(componentsList);
      if (command != null) {
        registerCommand();
        messenger = new MenuMessenger(getPagesSectionWrapper());
        Bukkit.getPluginManager().registerEvents(this, plugin);
        successfulInitialized = true;
        logger.finest("Menu " + name + " successful initialized.");
        return;
      } else {
        logger.warning("Menu " + name + " command isn't configured.");
      }
    } else {
      logger.warning("Menu " + name + " hasn't pages.");
    }

    successfulInitialized = false;
  }

  private void registerCommand() {
    commandsManager.registerCommand(command);
  }

  private void unregisterCommand() {
    commandsManager.unregisterCommand(command);
    command.onDisable();
  }

  private void unregisterListener() {
    HandlerList.unregisterAll(this);
  }

  void onDisable() {
    unregisterCommand();
    unregisterListener();
  }

  @Nullable
  private MenuCommand buildCommand(@NotNull final ComponentsList componentsList) {
    ConfigurationSection section = config.getSection("command");
    if (section != null) {
      String commandName = section.getString("name");
      if (commandName != null) {
        List<String> aliases = section.getStringList("aliases");
        String permissionName = section.getString("permission");
        if (permissionName == null) {
          permissionName = commandName + ".use";
        }

        return new MenuCommand(this, componentsList, commandName, permissionName, aliases);
      }
    }

    return null;
  }

  boolean isSuccessfulInitialized() {
    return successfulInitialized;
  }

  private boolean hasPages() {
    ConfigurationSection pagesSection = config.getSection("pages");
    if (pagesSection != null) {
      return pagesSection.getKeys(false).size() > 0;
    }

    return false;
  }

  private SimpleConfigWrapper getPagesSectionWrapper() {
    ConfigurationSection section = config.getSection("pages");
    assert section != null;

    return new SimpleConfigWrapper(section);
  }

  private void sendPage(@NotNull Player client, @NotNull String pageName) {
    if (messenger.existsPage(pageName)) {
      messenger.sendPage(client, pageName);
    }
  }

  private void sendMainPage(@NotNull Player client) {
    messenger.sendMainPage(client);
  }

  private void addClient(@NotNull Player client) {
    clients.add(client);
    chatBuffer.addClient(client);
  }

  private void remClient(@NotNull Player client) {
    clients.remove(client);
    chatBuffer.remClient(client);
  }

  public void open(@NotNull Player client) {
    addClient(client);
    sendMainPage(client);
  }

  void transfer(@NotNull Player client) {
    clients.add(client);
    sendMainPage(client);
  }

  private void close(@NotNull Player client, boolean sendBuffered) {
    remClient(client);
    chatBuffer.pushOut(client, sendBuffered);
  }

  @EventHandler
  private void onChatMessage(@NotNull AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();
    if (clients.contains(player)) {
      event.setCancelled(parseAction(player, lowerCase(event.getMessage())));
    }
  }

  private boolean parseAction(@NotNull Player client, @NotNull String input) {
    if (input.equals(CLOSE_MENU_ACTION_STRING)) {
      close(client, true);
      return true;
    } else {
      Matcher matcher = CHANGE_PAGE_ACTION_PATTERN.matcher(input);
      if (matcher.find()) {
        sendPage(client, getActionContent(input));
        return true;
      } else {
        matcher = CHANGE_MENU_ACTION_PATTERN.matcher(input);
        if (matcher.find()) {
          Menu menu = menusManager.getMenu(getActionContent(input));
          if (menu != null) {
            clients.remove(client);
            menu.transfer(client);
            return true;
          }
        }
      }
    }

    return false;
  }

  @NotNull
  private String getActionContent(@NotNull String actionStr) {
    return actionStr.substring(actionStr.indexOf('(') + 1, actionStr.indexOf(')'));
  }

  @EventHandler
  private void onPlayerQuit(@NotNull PlayerQuitEvent event) {
    Player player = event.getPlayer();
    if (clients.contains(player)) {
      close(player, false);
    }
  }

}