package dev.alofi11.minecraft.servers.plugins.chatengine.components.config;

import org.bukkit.plugin.java.JavaPlugin;

public class MessengerConfigWrapper extends BaseConfigWrapper {

  public MessengerConfigWrapper(final JavaPlugin plugin) {
    super("messages.yml", "1.0", plugin);
  }

  @Override
  protected void onConfigIsNotActual() {
    plugin.getLogger().warning("messages.yml updated, new version: " + version + ".");
  }

}
