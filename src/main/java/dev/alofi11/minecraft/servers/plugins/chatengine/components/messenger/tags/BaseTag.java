package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.tags;

import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

abstract class BaseTag {

  protected final String content;

  BaseTag(@NotNull String tag) {
    this.content = getContent(tag);
  }

  BaseTag() {
    this.content = null;
  }

  abstract void apply(@NotNull final TextComponent input);

  @NotNull
  private String getContent(@NotNull String tag) {
    return tag.substring(tag.indexOf('>') + 1, tag.lastIndexOf('<'));
  }

}
