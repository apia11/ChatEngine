package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.tags;

import static org.apache.commons.lang.StringUtils.lowerCase;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

final class ClickTag extends BaseTag {

  static final String CLICK_TAG_PATTERN_CONTENT = "<click (run|copy|open_url|suggest)>.*.</click>";
  private static final String NULL_POINTER = "null";
  private final String tag;

  ClickTag(@NotNull String tag) {
    super(tag);
    this.tag = tag;
  }

  @Override
  void apply(@NotNull final TextComponent input) {
    if (NULL_POINTER.equalsIgnoreCase(content)) {
      input.setClickEvent(null);
    } else {
      input.setClickEvent(new ClickEvent(getType(), content));
    }
  }

  @NotNull
  private ClickEvent.Action getType() {
    String typeStr = lowerCase(tag.substring(tag.indexOf(" ") + 1, tag.indexOf(">")));
    switch (typeStr) {
      case "run": {
        return Action.RUN_COMMAND;
      }

      case "copy": {
        return Action.COPY_TO_CLIPBOARD;
      }

      case "open_url": {
        return Action.OPEN_URL;
      }

      case "suggest": {
        return Action.SUGGEST_COMMAND;
      }

      default: {
        throw new IllegalArgumentException("Type of click event can't be " + typeStr + ".");
      }
    }
  }

}
