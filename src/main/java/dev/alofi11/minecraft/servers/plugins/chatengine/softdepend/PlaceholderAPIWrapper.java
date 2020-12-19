package dev.alofi11.minecraft.servers.plugins.chatengine.softdepend;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderAPIWrapper {

  private static Boolean installed = null;

  @NotNull
  public static String setPlaceholders(@NotNull Player player, @NotNull String input) {
    if (installed()) {
      input = PlaceholderAPI.setPlaceholders(player, input);
    }

    return input;
  }

  private static boolean installed() {
    if (installed == null) {
      installed = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    return installed;
  }

  public static void reload() {
    installed = null;
  }

}
