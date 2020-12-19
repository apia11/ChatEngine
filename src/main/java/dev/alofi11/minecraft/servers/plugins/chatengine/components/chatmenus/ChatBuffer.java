package dev.alofi11.minecraft.servers.plugins.chatengine.components.chatmenus;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import dev.alofi11.minecraft.servers.plugins.chatengine.ChatEnginePlugin;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.MessagePacket;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class ChatBuffer {

  private static final int MAX_BUFFER_SIZE = 100;
  private static final int MIN_BUFFER_SIZE = 20;
  private final PacketContainer emptyLinePacket;
  private final List<Player> clients = new ArrayList<>();
  private final Map<Player, List<PacketContainer>> bufferMap = new HashMap<>();
  private final ProtocolManager protocolManager;
  private final ChatPacketListener listener;

  ChatBuffer(@NotNull final ChatEnginePlugin plugin) {
    this.protocolManager = ProtocolLibrary.getProtocolManager();
    this.listener = new ChatPacketListener(this, plugin);
    this.emptyLinePacket = new MessagePacket(new TextComponent()).getPacket();

    registerListener();
  }

  private void registerListener() {
    protocolManager.addPacketListener(listener);
  }

  private void unregisterListener() {
    protocolManager.removePacketListener(listener);
  }

  void onDisable() {
    unregisterListener();
  }

  boolean isClient(@NotNull Player player) {
    return clients.contains(player);
  }

  private List<PacketContainer> getClientBuffer(@NotNull Player client) {
    return bufferMap.getOrDefault(client, new ArrayList<>());
  }

  void buffer(@NotNull Player client, @NotNull PacketContainer packet) {
    final List<PacketContainer> clientBuffer = getClientBuffer(client);
    if (clientBuffer.size() == MAX_BUFFER_SIZE) {
      clientBuffer.remove(0);
    }

    clientBuffer.add(packet);
    bufferMap.put(client, clientBuffer);
  }

  void pushOut(@NotNull Player client, boolean sendBuffered) {
    if (sendBuffered) {
      List<PacketContainer> clientBuffer = getClientBuffer(client);
      while (clientBuffer.size() < MIN_BUFFER_SIZE) {
        clientBuffer.add(emptyLinePacket);
      }

      clientBuffer.forEach((packet) -> {
        try {
          protocolManager.sendServerPacket(client, packet);
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      });
    }

    bufferMap.remove(client);
  }

  void addClient(@NotNull Player client) {
    clients.add(client);
  }

  void remClient(@NotNull Player client) {
    clients.remove(client);
  }

}