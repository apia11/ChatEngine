package dev.alofi11.minecraft.servers.plugins.chatengine.addons.updates;

import org.jetbrains.annotations.NotNull;

interface UpdateListener {

  void onUpdateRelease(@NotNull UpdateInformation updateInformation);

}
