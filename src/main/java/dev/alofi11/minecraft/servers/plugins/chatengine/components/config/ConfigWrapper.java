package dev.alofi11.minecraft.servers.plugins.chatengine.components.config;

import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigWrapper {

  @Nullable
  String getStr(@NotNull String path);

  @NotNull
  String getNNStr(@NotNull String path);

  int getInt(@NotNull String path);

  boolean getBool(@NotNull String path);

  @NotNull
  List<String> getStrList(@NotNull String path);

  @Nullable
  ConfigurationSection getSection(@NotNull String path);

  @NotNull
  ConfigurationSection getNNSection(@NotNull String path);

  @NotNull
  Set<String> getKeys(boolean deep);

  boolean contains(@NotNull String path);

}
