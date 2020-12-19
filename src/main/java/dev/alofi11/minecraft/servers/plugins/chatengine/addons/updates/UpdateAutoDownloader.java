package dev.alofi11.minecraft.servers.plugins.chatengine.addons.updates;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class UpdateAutoDownloader {

  private static final String DOWNLOAD_LINK_SHELL = "https://github.com/alofi11/chatengine/releases/download/%new_version%/ChatEngine-%new_version%.jar";
  private static final String FILE_NAME_SHELL = "ChatEngine-%new_version%.jar";
  private final File versionsDir;
  private final JavaPlugin plugin;

  public UpdateAutoDownloader(@NotNull final JavaPlugin plugin,
      @NotNull final UpdateNotifier updateNotifier) {
    this.plugin = plugin;
    updateNotifier.registerListener(this::onUpdateRelease);
    versionsDir = new File(plugin.getDataFolder(), "versions");
  }

  void onUpdateRelease(@NotNull UpdateInformation updateInformation) {
    String newVersion = updateInformation.getNewVersion();

    if (!isAlreadyDownloaded(newVersion)) {
      download(newVersion);
    }
  }

  void download(@NotNull String newVersion) {
    plugin.getLogger().info("Start download new version...");
    try (BufferedInputStream input = new BufferedInputStream(
        getDownloadURL(newVersion).openStream())) {
      FileOutputStream fileOutputStream = new FileOutputStream(getFile(newVersion));

      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = input.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
    } catch (IOException ignored) {
    }
    plugin.getLogger().info("New version downloaded.");
  }

  private boolean isAlreadyDownloaded(@NotNull String newVersion) {
    return getFile(newVersion).exists();
  }

  private File getFile(@NotNull String newVersion) {
    versionsDir.mkdirs();
    return new File(versionsDir, FILE_NAME_SHELL.replace("%new_version%", newVersion));
  }

  private URL getDownloadURL(@NotNull String newVersion) throws MalformedURLException {
    return new URL(DOWNLOAD_LINK_SHELL.replace("%new_version%", newVersion));
  }

}
