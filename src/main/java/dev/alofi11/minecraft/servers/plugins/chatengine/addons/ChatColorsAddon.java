package dev.alofi11.minecraft.servers.plugins.chatengine.addons;

import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.LEGACY_COLORS_PATTERN_CONTENT;
import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.applyGradients;
import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.applyHexColors;
import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.applyLegacyColors;
import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.gradientsSupported;
import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.hexSupported;
import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.legacySupported;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.permissions.PermissionsManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ChatColorsAddon implements Listener {

  private static final Pattern LEGACY_COLORS_PATTERN = Pattern
      .compile(LEGACY_COLORS_PATTERN_CONTENT);
  private final Permission legacyColorsPermission;
  private final Permission hexColorsPermission;
  private final Permission gradientsColorsPermission;

  public ChatColorsAddon(@NotNull final JavaPlugin plugin,
      @NotNull final PermissionsManager permissionsManager) {
    Bukkit.getPluginManager().registerEvents(this, plugin);

    if (legacySupported) {
      legacyColorsPermission = permissionsManager.registerPluginPermission("colors.legacy");
    } else {
      legacyColorsPermission = null;
    }

    if (hexSupported) {
      hexColorsPermission = permissionsManager.registerPluginPermission("colors.hex");
    } else {
      hexColorsPermission = null;
    }

    if (gradientsSupported) {
      gradientsColorsPermission = permissionsManager.registerPluginPermission("colors.gradients");
    } else {
      gradientsColorsPermission = null;
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  private void asyncChatListener(@NotNull AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();
    String message = event.getMessage();

    if (canUse(player, legacyColorsPermission)) {
      message = applyLegacyColors(message);
    }
    if (canUse(player, hexColorsPermission)) {
      message = applyHexColors(message);
    }
    if (canUse(player, gradientsColorsPermission)) {
      message = applyGradients(message);
    }

    String messageVisibleContent = stripColors(message);
    System.out.println("\"" + messageVisibleContent + "\"");
    if (StringUtils.isNotEmpty(messageVisibleContent)) {
      event.setMessage(message);
    }
  }

  private boolean canUse(@NotNull Player player, @Nullable Permission permission) {
    return permission != null && player.hasPermission(permission);
  }

  private String stripColors(@NotNull String input) {
    Matcher matcher = LEGACY_COLORS_PATTERN.matcher(input);
    while (matcher.find()) {
      input = input.replace(matcher.group(), "");
    }

    return input;
  }

}
