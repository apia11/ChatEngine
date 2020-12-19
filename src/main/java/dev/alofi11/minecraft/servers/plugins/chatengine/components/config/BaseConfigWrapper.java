package dev.alofi11.minecraft.servers.plugins.chatengine.components.config;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseConfigWrapper implements ConfigWrapper {

  protected final String name;
  protected final String version;
  protected final JavaPlugin plugin;
  protected FileConfiguration config;
  protected File configFile;

  public BaseConfigWrapper(final String name, final String version, final JavaPlugin plugin) {
    this.name = name;
    this.version = version;
    this.plugin = plugin;
  }

  @NotNull
  protected final FileConfiguration getConfig() {
    if (config == null) {
      config = YamlConfiguration.loadConfiguration(getConfigFile());
    }

    return config;
  }

  @NotNull
  private File getConfigFile() {
    if (configFile == null) {
      configFile = new File(plugin.getDataFolder(), name);
      if (!configFile.exists()) {
        createFile();
      }
    }

    return configFile;
  }

  private void createFile() {
    plugin.saveResource(name, false);
  }

  public final void reload() {
    config = null;
    configFile = null;
  }

  public final boolean isActualVersion() {
    boolean isActualVersion = version.equals(getStr("version"));
    if (!isActualVersion) {
      onConfigIsNotActual();
    }

    return isActualVersion;
  }

  protected abstract void onConfigIsNotActual();

  @Override
  public final @Nullable String getStr(@NotNull String path) {
    return getConfig().getString(path);
  }

  @Override
  public final @NotNull String getNNStr(@NotNull String path) {
    return Objects.requireNonNull(getStr(path));
  }

  @Override
  public final int getInt(@NotNull String path) {
    return getConfig().getInt(path);
  }

  @Override
  public final boolean getBool(@NotNull String path) {
    return getConfig().getBoolean(path);
  }

  @Override
  public final @NotNull List<String> getStrList(@NotNull String path) {
    return getConfig().getStringList(path);
  }

  @Override
  public final @Nullable ConfigurationSection getSection(@NotNull String path) {
    return getConfig().getConfigurationSection(path);
  }

  @Override
  public final @NotNull ConfigurationSection getNNSection(@NotNull String path) {
    return Objects.requireNonNull(getSection(path));
  }

  @Override
  public final @NotNull Set<String> getKeys(boolean deep) {
    return config.getKeys(deep);
  }

  @Override
  public final boolean contains(@NotNull String path) {
    return getConfig().contains(path);
  }

}
