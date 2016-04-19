package net.sasha.file;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerInvFile {
  private final File playerFile;
  private final SyncConfigWrapper syncConfig;

  public PlayerInvFile(File someFile) {
    playerFile = someFile;

    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(someFile);
    syncConfig = new SyncConfigWrapper(playerConfig);
  }

  public void saveFile() {
    try {
      syncConfig.save(playerFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public FileConfiguration getConfig() {
    return syncConfig;
  }
}
