package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.SimpleConfigWrapper;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MenuMessenger extends Messenger {

  public MenuMessenger(@NotNull final SimpleConfigWrapper config) {
    super(config);
  }

  public void sendPage(@NotNull CommandSender target, @NotNull String pagePath,
      @NotNull String... placeholders) {
    sendMessage(target, pagePath, placeholders);
  }

  public void sendMainPage(@NotNull CommandSender target, @NotNull String... placeholders) {
    sendPage(target, getPages().get(0), placeholders);
  }

  @NotNull
  private List<String> getPages() {
    return new ArrayList<>(config.getKeys(false));
  }

  @Override
  public void sendMessage(@NotNull CommandSender target, @NotNull String messagePath,
      @NotNull String... placeholders) {
    prepareMessage(target, messagePath, placeholders).forEach((lineTextComponent) -> {
      MessagePacket linePacket = new MessagePacket(lineTextComponent);
      linePacket.setMeta();
      linePacket.sendPacket((Player) target);
    });
  }

  public boolean existsPage(@NotNull String name) {
    return config.contains(name);
  }

}