package dev.alofi11.minecraft.servers.plugins.chatengine.components.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class MainConfigWrapper extends BaseConfigWrapper {

  public MainConfigWrapper(@NotNull final JavaPlugin plugin) {
    super("config.yml", "1.0", plugin);
  }

  @Override
  protected void onConfigIsNotActual() {
    plugin.getLogger().warning("config.yml updated, new version: " + version + ".");
  }

}
