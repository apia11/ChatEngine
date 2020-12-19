package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.MessengerConfigWrapper;
import org.jetbrains.annotations.NotNull;

public final class MainMessenger extends Messenger {

  public MainMessenger(@NotNull final MessengerConfigWrapper config) {
    super(config);
    config.isActualVersion();
  }

  public void reload() {
    ((MessengerConfigWrapper) config).reload();
  }

}