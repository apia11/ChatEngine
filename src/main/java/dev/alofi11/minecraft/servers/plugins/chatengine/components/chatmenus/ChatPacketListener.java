package dev.alofi11.minecraft.servers.plugins.chatengine.components.chatmenus;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import dev.alofi11.minecraft.servers.plugins.chatengine.ChatEnginePlugin;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.MessagePacket;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class ChatPacketListener implements PacketListener {

  private final ChatBuffer chatBuffer;
  private final ChatEnginePlugin plugin;

  ChatPacketListener(@NotNull final ChatBuffer chatBuffer, @NotNull final ChatEnginePlugin plugin) {
    this.chatBuffer = chatBuffer;
    this.plugin = plugin;
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    Player player = event.getPlayer();
    if (chatBuffer.isClient(player)) {
      if (!new MessagePacket(event.getPacket()).isMessengerPacket()) {
        chatBuffer.buffer(player, event.getPacket());
        event.setCancelled(true);
      }
    }
  }

  @Override
  public void onPacketReceiving(PacketEvent packetEvent) {
  }

  @Override
  public ListeningWhitelist getSendingWhitelist() {
    return ListeningWhitelist.newBuilder().types(Server.CHAT).build();
  }

  @Override
  public ListeningWhitelist getReceivingWhitelist() {
    return ListeningWhitelist.EMPTY_WHITELIST;
  }

  @Override
  public Plugin getPlugin() {
    return plugin;
  }

}
