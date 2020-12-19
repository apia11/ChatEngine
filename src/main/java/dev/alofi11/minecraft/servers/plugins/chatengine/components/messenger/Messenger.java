package dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger;

import static dev.alofi11.minecraft.servers.plugins.chatengine.utils.ColorsUtils.applySupported;

import dev.alofi11.minecraft.servers.plugins.chatengine.components.config.ConfigWrapper;
import dev.alofi11.minecraft.servers.plugins.chatengine.components.messenger.tags.TagsParser;
import dev.alofi11.minecraft.servers.plugins.chatengine.softdepend.PlaceholderAPIWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Messenger {

  private static final char PLACEHOLDER_BOUND_CHAR = '%';
  private final static Pattern SPACE_EXTEND_PATTERN = Pattern.compile("<extend/[0-9]{1,2}>");
  private final static String LINES_EXTEND_STRING = "<extend/>";
  private final static int CHAT_LINES = 20;
  protected final ConfigWrapper config;

  public Messenger(@NotNull final ConfigWrapper config) {
    this.config = config;
  }

  public void sendMessage(@NotNull CommandSender target, @NotNull String messagePath,
      @NotNull String... placeholders) {
    prepareMessage(target, messagePath, placeholders).forEach(target::sendMessage);
  }

  @NotNull
  protected List<TextComponent> prepareMessage(@NotNull CommandSender target,
      @NotNull String messageKey, @NotNull String... placeholders) {
    List<String> message = applyExtends(
        setPlaceholders(target, getMessage(messageKey), placeholders));
    List<TextComponent> preparedLines = new ArrayList<>();
    for (String line : message) {
      TagsParser tagsParser = new TagsParser(line);
      tagsParser.applyTags();
      TextComponent lineTextComponent = tagsParser.getOutput();
      applySupported(lineTextComponent);
      preparedLines.add(lineTextComponent);
    }

    return preparedLines;
  }

  @NotNull
  private List<String> getMessage(@NotNull String messageKey) {
    return config.getStrList(messageKey);
  }

  private List<String> setPlaceholders(@NotNull CommandSender target,
      @NotNull final List<String> messageContent, @NotNull String... placeholders) {
    List<String> allPlaceholders = new ArrayList<>(Arrays.asList(placeholders));
    allPlaceholders.addAll(getBasePlaceholders(target));
    allPlaceholders.addAll(getConfigPlaceholders());

    List<String> preparedMessageContent = new ArrayList<>();
    messageContent
        .forEach(
            (line) -> preparedMessageContent.add(setPlaceholders(target, line, allPlaceholders)));

    return preparedMessageContent;
  }

  @NotNull
  private List<String> getConfigPlaceholders() {
    ConfigurationSection placeholdersSection = config.getSection("placeholders");
    if (placeholdersSection != null) {
      List<String> placeholders = new ArrayList<>();
      Set<String> keys = placeholdersSection.getKeys(false);
      keys.forEach((key) -> {
        placeholders.add(key);
        placeholders.add(placeholdersSection.getString(key));
      });

      return placeholders;
    }

    return Collections.emptyList();
  }

  @NotNull
  private List<String> getBasePlaceholders(@NotNull CommandSender target) {
    return Arrays
        .asList("target", target.getName());
  }

  private String setPlaceholders(@NotNull CommandSender target, @NotNull String input,
      @NotNull List<String> placeholders) {
    final int placeholdersSize = placeholders.size();
    if (placeholdersSize % 2 != 0) {
      throw new IllegalArgumentException(
          "Each placeholder must have a pair with its name in front of it.");
    }

    for (int i = 0; i < placeholdersSize; i++) {
      input = input.replace(PLACEHOLDER_BOUND_CHAR + placeholders.get(i) + PLACEHOLDER_BOUND_CHAR,
          placeholders.get(i + 1));
      i++;
    }

    return PlaceholderAPIWrapper.setPlaceholders((Player) target, input);
  }

  private List<String> applyExtends(@NotNull List<String> list) {
    List<Integer> extendsIndexes = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      String line = list.get(i);

      if (line.equalsIgnoreCase(LINES_EXTEND_STRING)) {
        extendsIndexes.add(i);
      } else {
        list.set(i, applySpaceExtends(line));
      }
    }

    final int extendsCount = extendsIndexes.size();
    int step = CHAT_LINES - (list.size() - extendsCount);
    step = extendsCount > 0 ? step / extendsCount : 0;

    int added = 0;
    for (int currentExtend = 0; currentExtend < extendsCount; currentExtend++) {
      int index = extendsIndexes.get(currentExtend) + added;
      index = index > 0 ? index - currentExtend : index;

      list.remove(index);
      for (int currentStep = 0; currentStep < step; currentStep++) {
        list.add(index, "");
      }

      added += step;
    }

    return list;
  }

  @NotNull
  private String applySpaceExtends(@NotNull String input) {
    Matcher matcher = SPACE_EXTEND_PATTERN.matcher(input);
    while (matcher.find()) {
      String extendTag = matcher.group();
      int spacesCount = Integer
          .parseInt(extendTag.substring(extendTag.indexOf("/") + 1, extendTag.indexOf(">")));

      input = input.replace(extendTag, getSpaces(spacesCount));
    }

    return input;
  }

  @NotNull
  private String getSpaces(int count) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < count; i++) {
      stringBuilder.append(" ");
    }

    return stringBuilder.toString();
  }

}
