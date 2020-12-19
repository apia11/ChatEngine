package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.tags;

import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.applySupported;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;

final class HoverTag extends BaseTag {

  static final String HOVER_TAG_PATTERN_CONTENT = "<hover>.*.</hover>";
  private static final String NULL_POINTER = "null";

  HoverTag(@NotNull String content) {
    super(content);
  }

  @Override
  void apply(@NotNull final TextComponent input) {
    if (NULL_POINTER.equalsIgnoreCase(content)) {
      input.setHoverEvent(null);
    } else {
      assert content != null;
      input.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text(applySupported(content))));
    }
  }

}
