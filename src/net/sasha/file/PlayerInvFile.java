package net.sasha.file;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerInvFile {
  private final File playerFile;
  private final FileConfiguration playerConfig;
  
  public PlayerInvFile(File someFile) {
    playerFile = someFile;
    playerConfig = YamlConfiguration.loadConfiguration(someFile);
  }
  
  public void saveFile() {
    synchronized (playerConfig) {
      try {
        playerConfig.save(playerFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
    

  public FileConfiguration getConfig() {
    return playerConfig;
  }
}
