package dev.alofi11.minecraft.servers.plugins.chatengine.utils;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public final class ColorsUtils {

  public static final String LEGACY_COLORS_PATTERN_CONTENT = "§[0-9a-fA-FkKlLmMnNoOxX]";
  public static final String HEX_PATTERN_CONTENT = "<#[a-fA-F0-9]{6}>";
  public static final String HEX_GRADIENT_PATTERN_CONTENT = "<#[a-fA-F0-9]{6}:#[a-fA-F0-9]{6}>";
  private static final String SPLIT_CHAR_SEQUENCE = "҇";
  private static final Pattern HEX_PATTERN = Pattern.compile(HEX_PATTERN_CONTENT);
  private static final Pattern HEX_GRADIENT_PATTERN = Pattern.compile(HEX_GRADIENT_PATTERN_CONTENT);
  private static final int GRADIENT_CODE_LENGTH = 17;
  public static boolean legacySupported = true;
  public static boolean hexSupported = false;
  public static boolean gradientsSupported = false;

  private ColorsUtils() {
  }

  public static void setSupported(boolean legacy, boolean hex, boolean gradients) {
    legacySupported = legacy;
    hexSupported = hex;
    gradientsSupported = gradients;
  }

  @NotNull
  public static String applySupported(@NotNull String content) {
    if (legacySupported) {
      content = applyLegacyColors(content);
    }

    try {
      if (hexSupported) {
        content = applyHexColors(content);
      }
      if (gradientsSupported) {
        content = applyGradients(content);
      }
    } catch (NoSuchMethodError ignored) {
    }

    return content;
  }

  public static void applySupported(@NotNull final TextComponent contentTextComponent) {
    if (legacySupported) {
      apply(contentTextComponent, ColorsUtils::applyLegacyColors);
    }

    try {
      if (hexSupported) {
        apply(contentTextComponent, ColorsUtils::applyHexColors);
      }
      if (gradientsSupported) {
        apply(contentTextComponent, ColorsUtils::applyGradients);
      }
    } catch (NoSuchMethodError ignored) {
    }
  }

  // If who's know better method for do this, i be happy see it here.
  private static void apply(@NotNull TextComponent textComponent,
      Consumer<TextComponent> consumer) {
    List<BaseComponent> extras = textComponent.getExtra();
    if (extras != null) {
      extras.forEach((baseComponent) -> {
        TextComponent textComponent2 = (TextComponent) baseComponent;
        consumer.accept(textComponent2);
        List<BaseComponent> extras2 = textComponent2.getExtra();
        if (extras2 != null) {
          extras2.forEach((baseComponent2) -> {
            TextComponent textComponent3 = (TextComponent) baseComponent2;
            consumer.accept(textComponent3);
            List<BaseComponent> extras3 = textComponent3.getExtra();
            if (extras3 != null) {
              extras3.forEach((baseComponent4) -> consumer.accept((TextComponent) baseComponent4));
            }
          });
        }
      });
    }
  }

  public static void applyGradients(@NotNull final TextComponent contentTextComponent) {
    String content = contentTextComponent.getText();

    Matcher hexGradientPatternMatcher = HEX_GRADIENT_PATTERN.matcher(content);
    List<String> gradientsCodes = new ArrayList<>();

    while (hexGradientPatternMatcher.find()) {
      gradientsCodes.add(hexGradientPatternMatcher.group());
    }

    if (gradientsCodes.size() > 0) {
      contentTextComponent.setText("");
      String nextGradientCode = null;
      String gradientCode;

      for (int i = 0; i < gradientsCodes.size(); i++) {
        gradientCode = isEmpty(nextGradientCode) ? gradientsCodes.get(i) : nextGradientCode;
        int start = content.indexOf(gradientCode);
        int end;

        content = content.replace(gradientCode, "");

        if (start > 0) {
          contentTextComponent.addExtra(content.substring(0, start));
          content = content.substring(start);
        }

        String preparedContent = content.substring(start);
        if (gradientsCodes.size() > (i + 1)) {
          nextGradientCode = gradientsCodes.get(i + 1);
          end = preparedContent.indexOf(nextGradientCode) + start;
          int indexOfColorCode = preparedContent.indexOf('§');
          if (indexOfColorCode < end && indexOfColorCode != -1) {
            end = start + indexOfColorCode;
          }
        } else {
          end = preparedContent.contains("§") ? (start + preparedContent.indexOf('§') - 1)
              : content.length();
        }

        contentTextComponent.addExtra(
            applyGradientToTextComponentFromGradientCode(content, gradientCode, end)
        );

        content = content.substring(end);

        if (gradientsCodes.size() <= (i + 1)) {
          contentTextComponent.addExtra(content);
        }
      }
    }
  }

  @NotNull
  private static TextComponent applyGradientToTextComponentFromGradientCode(@NotNull String content,
      @NotNull String gradientCode, int end) {
    gradientCode = gradientCode.substring(1, gradientCode.length() - 1);
    Color[] colors = parseColorsFromGradientCode(gradientCode);

    return applyGradientToTextComponentFromColors(content.substring(0, end), colors[0], colors[1]);
  }

  @NotNull
  private static TextComponent applyGradientToTextComponentFromColors(@NotNull String content,
      @NotNull Color startColor, @NotNull Color endColor) {
    TextComponent textComponent = new TextComponent();
    Color[] colors = getGradientColors(startColor, endColor, content.replace(" ", "").length());
    char[] contentCharsArray = content.toCharArray();

    int colorIndex = 0;
    for (char ch : contentCharsArray) {
      TextComponent charComponent = new TextComponent(String.valueOf(ch));

      if (ch != ' ') {
        charComponent.setColor(ChatColor.of(colors[colorIndex]));
        colorIndex++;
      }

      textComponent.addExtra(charComponent);
    }

    return textComponent;
  }

  @NotNull
  public static String applyGradients(@NotNull String content) {
    Matcher hexGradientPatternMatcher = HEX_GRADIENT_PATTERN.matcher(content);
    List<String> gradientsCodes = new ArrayList<>();

    while (hexGradientPatternMatcher.find()) {
      gradientsCodes.add(hexGradientPatternMatcher.group());
    }

    if (gradientsCodes.size() > 0) {
      String nextGradientCode = null;
      String gradientCode;

      for (int i = 0; i < gradientsCodes.size(); i++) {
        gradientCode = isEmpty(nextGradientCode) ? gradientsCodes.get(i) : nextGradientCode;
        int start = (content.indexOf(gradientCode) + GRADIENT_CODE_LENGTH);
        int end;

        String preparedContent = content.substring(start);
        if (gradientsCodes.size() > (i + 1)) {
          nextGradientCode = gradientsCodes.get(i + 1);
          end = preparedContent.indexOf(nextGradientCode) + start;
          int indexOfColorCode = preparedContent.indexOf('§');
          if (indexOfColorCode < end && indexOfColorCode != -1) {
            end = start + indexOfColorCode;
          }
        } else {
          end = preparedContent.contains("§") ? start + preparedContent.indexOf('§')
              : content.length();
        }

        content = applyGradientToTextFromGradientCode(content, gradientCode, start, end);
      }
    }

    return content;
  }

  @NotNull
  private static String applyGradientToTextFromGradientCode(@NotNull String content,
      @NotNull String gradientCode, int gradientStart, int gradientEnd) {
    Color[] colors = parseColorsFromGradientCode(
        gradientCode.substring(1, gradientCode.length() - 1));
    String part = content.substring(gradientStart, gradientEnd);
    part = applyGradientToTextFromColors(part, colors[0], colors[1]);

    return (content.substring(0, gradientStart) + part + content.substring(gradientEnd))
        .replace(gradientCode, "");
  }

  @NotNull
  private static Color[] parseColorsFromGradientCode(@NotNull String gradientCode) {
    Color[] colors = new Color[2];
    colors[0] = Color.decode(gradientCode.substring(0, 7));
    colors[1] = Color.decode(gradientCode.substring(8));

    return colors;
  }

  @NotNull
  private static String applyGradientToTextFromColors(@NotNull String content,
      @NotNull Color startColor, @NotNull Color endColor) {
    char[] contentCharsArray = content.toCharArray();
    StringBuilder outputBuilder = new StringBuilder();
    Color[] colors = getGradientColors(startColor, endColor, content.replace(" ", "").length());

    int colorIndex = 0;
    for (char ch : contentCharsArray) {
      if (ch != ' ') {
        outputBuilder.append(ChatColor.of(colors[colorIndex]).toString()).append(ch);
        colorIndex++;
      } else {
        outputBuilder.append(ch);
      }
    }

    return outputBuilder.toString();
  }

  @NotNull
  private static Color[] getGradientColors(@NotNull Color startColor, @NotNull Color endColor,
      int steps) {
    Color[] colors = new Color[steps];
    float[] rValues = calculateGradient(startColor.getRed(), endColor.getRed(), steps);
    float[] gValues = calculateGradient(startColor.getGreen(), endColor.getGreen(), steps);
    float[] bValues = calculateGradient(startColor.getBlue(), endColor.getBlue(), steps);

    for (int i = 0; i < colors.length; i++) {
      colors[i] = new Color(Math.round(rValues[i]), Math.round(gValues[i]), Math.round(bValues[i]));
    }

    return colors;
  }

  private static float[] calculateGradient(float startNum, float endNum, int steps) {
    float[] stepsValues = new float[steps];
    if (stepsValues.length > 0) {
      stepsValues[0] = startNum;
      stepsValues[stepsValues.length - 1] = endNum;
      float stepValue = Math.abs(startNum - endNum) / steps;

      for (int i = 1; i < (stepsValues.length - 1); i++) {
        stepsValues[i] =
            startNum > endNum ? stepsValues[i - 1] - stepValue : stepsValues[i - 1] + stepValue;
      }
    }

    return stepsValues;
  }

  @NotNull
  public static String applyLegacyColors(@NotNull String content) {
    return ChatColor.translateAlternateColorCodes('&', content);
  }

  public static void applyLegacyColors(@NotNull final TextComponent contentTextComponent) {
    contentTextComponent.setText(applyLegacyColors(contentTextComponent.getText()));
  }

  @NotNull
  public static String applyHexColors(@NotNull String content) {
    Matcher matcher = HEX_PATTERN.matcher(content);
    while (matcher.find()) {
      String hexCode = matcher.group();
      content = content
          .replace(hexCode, ChatColor.of(hexCode.substring(1, hexCode.length() - 1)).toString());
    }

    return content;
  }

  public static void applyHexColors(@NotNull final TextComponent contentTextComponent) {
    String content = contentTextComponent.getText();

    List<String> hexCodes = new ArrayList<>();
    Matcher hexPatternMatcher = HEX_PATTERN.matcher(content);

    while (hexPatternMatcher.find()) {
      String hexCode = hexPatternMatcher.group();
      content = content.replace(hexCode, SPLIT_CHAR_SEQUENCE);
      hexCodes.add(hexCode.substring(1, hexCode.length() - 1));
    }

    if (hexCodes.size() > 0) {
      contentTextComponent.setText("");
      String[] contentParts = content.split(SPLIT_CHAR_SEQUENCE);
      int hexCodeIndex = 0;
      for (int i = 0; i < contentParts.length; i++) {
        String contentPart = contentParts[i];
        TextComponent contentPartTextComponent = new TextComponent();

        if (i > 0) {
          contentPartTextComponent.setColor(ChatColor.of(hexCodes.get(hexCodeIndex)));
          hexCodeIndex++;
        }

        if (contentPart.length() > 0) {
          contentPartTextComponent.setText(contentPart);
          contentTextComponent.addExtra(contentPartTextComponent);
        }
      }
    }
  }

}
