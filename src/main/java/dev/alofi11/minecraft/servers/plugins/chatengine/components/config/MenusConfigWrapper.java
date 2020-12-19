package dev.alofi11.minecraft.servers.plugins.chatengine.components.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MenusConfigWrapper extends BaseConfigWrapper {

  public MenusConfigWrapper(@NotNull final JavaPlugin plugin) {
    super("menus.yml", "1.0", plugin);
  }

  @Override
  protected void onConfigIsNotActual() {
    plugin.getLogger().warning("menus.yml updated, new version: " + version + ".");
  }

}
