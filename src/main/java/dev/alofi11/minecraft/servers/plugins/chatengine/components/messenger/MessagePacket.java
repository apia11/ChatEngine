package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.lang.reflect.InvocationTargetException;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class MessagePacket {

  private final ProtocolManager protocolManager;
  private final PacketContainer packetContainer;

  public MessagePacket(@NotNull TextComponent textComponent) {
    protocolManager = ProtocolLibrary.getProtocolManager();
    packetContainer = toPacketContainer(textComponent);
  }

  public MessagePacket(@NotNull PacketContainer packetContainer) {
    protocolManager = ProtocolLibrary.getProtocolManager();
    this.packetContainer = packetContainer;
  }

  @NotNull
  private String toJson(@NotNull TextComponent messageTextComponent) {
    return ComponentSerializer.toString(messageTextComponent);
  }

  @NotNull
  private WrappedChatComponent toWrappedChatComponent(@NotNull TextComponent textComponent) {
    return WrappedChatComponent.fromJson(toJson(textComponent));
  }

  @NotNull
  private PacketContainer toPacketContainer(@NotNull TextComponent textComponent) {
    PacketContainer packetContainer = protocolManager.createPacket(Server.CHAT);
    packetContainer.getChatComponents().write(0, toWrappedChatComponent(textComponent));

    return packetContainer;
  }

  public void setMeta() {
    packetContainer.setMeta("plugin", "ChatEngine");
  }

  public void remMeta() {
    packetContainer.removeMeta("plugin");
  }

  @NotNull
  public String getMeta() {
    return (String) packetContainer.getMeta("plugin").orElse("none");
  }

  public boolean isMessengerPacket() {
    return getMeta().equals("ChatEngine");
  }

  public void sendPacket(@NotNull Player player) {
    try {
      protocolManager.sendServerPacket(player, packetContainer);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @NotNull
  public PacketContainer getPacket() {
    return packetContainer;
  }

}
