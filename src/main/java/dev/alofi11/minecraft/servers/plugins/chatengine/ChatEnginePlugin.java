/*
 * ChatEngine - a paper plugin
 * Copyright (C) alofi11, 2020.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package dev.alofi11.minecraft.servers.plugins.chatengine;

import dev.alofi11.minecraft.servers.plugins.chatengine.addons.ChatColorsAddon;
import dev.alofi11.minecraft.servers.plugins.chatengine.addons.updates.UpdateAutoDownloader;
import dev.alofi11.minecraft.servers.plugins.chatengine.addons.updates.UpdateNotifier;
import dev.alofi11.minecraft.servers.plugins.chatengine.commands.maincommand.MainCommand;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.ComponentsList;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.DisableSupport;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.chatmenus.MenusManager;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.commands.CommandsManager;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.MainConfigWrapper;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.MessengerConfigWrapper;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.MainMessenger;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.permissions.PermissionsManager;
import dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class ChatEnginePlugin extends JavaPlugin {

  private final ComponentsList componentsList = new ComponentsList();
  private final MainConfigWrapper config = new MainConfigWrapper(this);

  @Override
  public void onEnable() {
    if (isEnabledInConfig()) {
      if (config.isActualVersion()) {
        setupColorsUtils();
        initializeComponents();
        initializeAddons();
        initializeCommands();

        return;
      }
    }

    setEnabled(false);
  }

  @Override
  public void onDisable() {
    disable(componentsList.get(MenusManager.class));
    disable(componentsList.get(PermissionsManager.class));
    disable(componentsList.get(CommandsManager.class));
  }

  private void disable(@Nullable DisableSupport target) {
    if (target != null) {
      target.onDisable();
    }
  }

  private boolean isEnabledInConfig() {
    boolean isEnabledInConfig = config.getBool("plugin.enabled");
    if (!isEnabledInConfig) {
      getLogger().warning("Plugin is disabled in config.");
    }

    return isEnabledInConfig;
  }

  public void setupColorsUtils() {
    ColorsUtils.setSupported(
        config.getBool("colors-support.legacy"),
        config.getBool("colors-support.hex"),
        config.getBool("colors-support.gradients")
    );
  }

  private void initializeComponents() {
    componentsList.add(this);
    componentsList.add(config);
    componentsList.initializeFromListResourcesToList(PermissionsManager.class, 0);
    componentsList.initializeFromListResourcesToList(MessengerConfigWrapper.class, 0);
    componentsList.initializeFromListResourcesToList(MainMessenger.class, 0);
    componentsList.initializeFromListResourcesToList(CommandsManager.class, 0);
    componentsList.initializeFromListResourcesToList(MenusManager.class, 0);
  }

  private void initializeCommands() {
    CommandsManager commandsManager = componentsList.getNN(CommandsManager.class);
    MainCommand mainCommand = new MainCommand(componentsList);
    commandsManager.registerCommand(mainCommand);
    componentsList.add(mainCommand);
  }

  private void initializeAddons() {
    if (config.getBool("addons.chat-colors")) {
      componentsList.initializeFromListResourcesToList(ChatColorsAddon.class, 0);
    }
    if (config.getBool("addons.update-notifier")) {
      componentsList.initializeFromListResourcesToList(UpdateNotifier.class, 0);

      if (config.getBool("addons.update-auto-downloader")) {
        componentsList.initializeFromListResourcesToList(UpdateAutoDownloader.class, 0);
      }
    }
  }

}
