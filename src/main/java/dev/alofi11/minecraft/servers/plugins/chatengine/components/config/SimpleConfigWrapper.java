package dev.alofi11.minecraft.servers.plugins.chatengine.components.config;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimpleConfigWrapper implements ConfigWrapper {

  private ConfigurationSection config;

  public SimpleConfigWrapper(@NotNull final ConfigurationSection config) {
    this.config = config;
  }

  public void setConfig(@NotNull final ConfigurationSection config) {
    this.config = config;
  }

  @Override
  public @Nullable String getStr(@NotNull String path) {
    return config.getString(path);
  }

  @Override
  public @NotNull String getNNStr(@NotNull String path) {
    return Objects.requireNonNull(getStr(path));
  }

  @Override
  public int getInt(@NotNull String path) {
    return config.getInt(path);
  }

  @Override
  public boolean getBool(@NotNull String path) {
    return config.getBoolean(path);
  }

  @Override
  public @NotNull List<String> getStrList(@NotNull String path) {
    return config.getStringList(path);
  }

  @Override
  public final @Nullable ConfigurationSection getSection(@NotNull String path) {
    return config.getConfigurationSection(path);
  }

  @Override
  public @NotNull ConfigurationSection getNNSection(@NotNull String path) {
    return Objects.requireNonNull(getSection(path));
  }

  @Override
  public @NotNull Set<String> getKeys(boolean deep) {
    return config.getKeys(deep);
  }

  @Override
  public boolean contains(@NotNull String path) {
    return config.contains(path);
  }

}
