package dev.alofi11.minecraft.servers.plugins.chatengine.addons.updates;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.permissions.PermissionsManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public final class UpdateNotifier implements Listener {

  private static final String UPDATE_NOTIFY_MESSAGE = "§c[!] §eNew version of ChatEngine available: §f%new_version%§e.\n§c[!] §eMore: §f%release_link%";
  private static final String CONFIG_UPDATED_NOTIFY = "§c[!] Config updated.";
  private static final String CHECK_UPDATE_URL = "https://api.github.com/repos/alofi11/chatengine/releases";
  private final Permission notifyPermission;
  private final String currentVersion;
  private final JavaPlugin plugin;
  private final List<UpdateListener> updateListeners = new ArrayList<>();
  private boolean updateAvailable = false;
  private UpdateInformation updateInformation;
  private BukkitTask checkUpdateTask;

  public UpdateNotifier(@NotNull final JavaPlugin plugin,
      @NotNull final PermissionsManager permissionsManager) {
    this.plugin = plugin;
    this.currentVersion = plugin.getDescription().getVersion();
    this.notifyPermission = permissionsManager.registerPluginPermission("notify");

    Bukkit.getPluginManager().registerEvents(this, plugin);
    startCheckUpdateTask();
  }

  private void startCheckUpdateTask() {
    checkUpdateTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkUpdate, 0, 3600000);
  }

  private void stopCheckUpdateTask() {
    if (checkUpdateTask != null) {
      checkUpdateTask.cancel();
    }
  }

  @NotNull
  private InputStream getGitStream() throws IOException {
    return new URL(CHECK_UPDATE_URL).openStream();
  }

  @NotNull
  private String buildJson(@NotNull Scanner scanner) {
    StringBuilder stringBuilder = new StringBuilder();
    while (scanner.hasNext()) {
      stringBuilder.append(scanner.nextLine());
    }

    return stringBuilder.toString();
  }

  @NotNull
  private Scanner getGitScanner() throws IOException {
    return new Scanner(getGitStream());
  }

  private void checkUpdate() {
    try {
      Scanner scanner = getGitScanner();
      JsonObject jsonObject = jsonStringToJsonArray(buildJson(scanner)).get(0)
          .getAsJsonObject();
      String version = getVersion(jsonObject);

      if (!version.equals(currentVersion)) {
        stopCheckUpdateTask();
        updateAvailable = true;
        updateInformation = new UpdateInformation(version,
            isConfigUpdated(jsonObject));
        updateListeners.forEach((listener) -> listener.onUpdateRelease(updateInformation));
      }
    } catch (IOException ignored) {
    }
  }

  private JsonArray jsonStringToJsonArray(@NotNull String json) {
    JsonParser jsonParser = new JsonParser();

    return jsonParser.parse(json).getAsJsonArray();
  }

  private String getVersion(@NotNull JsonObject jsonObject) {
    return jsonObject.get("tag_name").getAsString();
  }

  private boolean isConfigUpdated(@NotNull JsonObject jsonObject) {
    return jsonObject.get("body").getAsString().contains("[Config updated]");
  }

  void registerListener(@NotNull UpdateListener updateListener) {
    updateListeners.add(updateListener);
  }

  private String prepareUpdateNotifyMessage() {
    String newVersion = updateInformation.getNewVersion();
    String releaseLink = updateInformation.getReleaseLink();
    return UPDATE_NOTIFY_MESSAGE.replace("%new_version%", newVersion)
        .replace("%release_link%", releaseLink);
  }

  @EventHandler
  private void adminJoinListener(@NotNull PlayerJoinEvent event) {
    if (updateAvailable && event.getPlayer().hasPermission(notifyPermission)) {
      Player target = event.getPlayer();

      target.sendMessage(prepareUpdateNotifyMessage());
      if (updateInformation.isConfigUpdated()) {
        target.sendMessage(CONFIG_UPDATED_NOTIFY);
      }
    }
  }

  @EventHandler
  private void onDisable(@NotNull PluginDisableEvent event) {
    if (event.getPlugin().equals(plugin)) {
      stopCheckUpdateTask();
    }
  }

}
