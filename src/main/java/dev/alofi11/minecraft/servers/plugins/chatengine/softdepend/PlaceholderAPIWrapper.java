package dev.alofi11.minecraft.servers.plugins.chatengine.softdepend;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderAPIWrapper {

  private static Boolean installed = null;

  @NotNull
  public static String setPlaceholders(@NotNull CommandSender target, @NotNull String input) {
    if (installed() && isPlayer(target)) {
      input = PlaceholderAPI.setPlaceholders((Player) target, input);
    }

    return input;
  }

  private static boolean isPlayer(@NotNull CommandSender target) {
    return target instanceof Player;
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
