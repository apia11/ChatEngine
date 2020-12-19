package dev.alofi11.minecraft.servers.plugins.chatengine.addons.updates;

final class UpdateInformation {

  private static final String RELEASE_LINK_SHELL = "https://github.com/alofi11/chatengine/releases/tag/";
  private final String releaseLink;
  private final String newVersion;
  private final boolean configUpdated;

  UpdateInformation(String newVersion, boolean configUpdated) {
    this.newVersion = newVersion;
    this.configUpdated = configUpdated;
    this.releaseLink = RELEASE_LINK_SHELL + newVersion;
  }


  public String getReleaseLink() {
    return releaseLink;
  }

  public String getNewVersion() {
    return newVersion;
  }

  public boolean isConfigUpdated() {
    return configUpdated;
  }

}
