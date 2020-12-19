package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public final class TagsParser {

  protected static final String SPLIT_CHAR_SEQUENCE = "҇";
  private final TextComponent output;

  public TagsParser(@NotNull String input) {
    this.output = new TextComponent(input);
  }

  @NotNull
  public TextComponent getOutput() {
    return output;
  }

  public void applyTags() {
    applyClickTags(output);
    final List<BaseComponent> extras = output.getExtra();
    for (BaseComponent extra : extras) {
      applyHoverTags((TextComponent) extra);
    }
  }

  private void applyClickTags(@NotNull final TextComponent input) {
    String content = input.getText();
    input.setText("");

    List<String> tags = getTags(ClickTag.CLICK_TAG_PATTERN_CONTENT, content);
    String[] parts = splitByTags(content, tags);

    int tagIndex = 0;
    for (int i = 0; i < parts.length; i++) {
      TextComponent partComponent = new TextComponent(parts[i]);

      if (i > 0) {
        ClickTag clickTag = new ClickTag(tags.get(tagIndex));
        clickTag.apply(partComponent);
        tagIndex++;
      }

      input.addExtra(partComponent);
    }
  }

  private void applyHoverTags(@NotNull final TextComponent input) {
    String content = input.getText();
    input.setText("");

    List<String> tags = getTags(HoverTag.HOVER_TAG_PATTERN_CONTENT, content);
    String[] parts = splitByTags(content, tags);

    int tagIndex = 0;
    for (int i = 0; i < parts.length; i++) {
      TextComponent partComponent = new TextComponent(parts[i]);

      if (i > 0) {
        HoverTag hoverTag = new HoverTag(tags.get(tagIndex));
        hoverTag.apply(partComponent);
        tagIndex++;
      }

      input.addExtra(partComponent);
    }
  }

  @NotNull
  private List<String> getTags(@NotNull final String tagPatternContent, @NotNull String input) {
    List<String> tags = new ArrayList<>();
    String tagEnd = getTagEnd(tagPatternContent);
    Pattern tagPattern = Pattern.compile(tagPatternContent);

    Matcher matcher = tagPattern.matcher(input.replace(tagEnd, tagEnd + "\n"));
    while (matcher.find()) {
      tags.add(matcher.group());
    }

    return tags;
  }

  private String getTagEnd(@NotNull String tagPatternContent) {
    return tagPatternContent
        .substring(tagPatternContent.lastIndexOf("<"), tagPatternContent.lastIndexOf(">") + 1);
  }

  @NotNull
  private String[] splitByTags(@NotNull String input, @NotNull List<String> tags) {
    for (String tag : tags) {
      input = input.replace(tag, SPLIT_CHAR_SEQUENCE);
    }

    return input.split(SPLIT_CHAR_SEQUENCE);
  }

}
