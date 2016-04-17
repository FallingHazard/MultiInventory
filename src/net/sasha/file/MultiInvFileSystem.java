package net.sasha.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MultiInvFileSystem {
  private final Plugin plugin;
  private final Server server;
  private File pluginFolder;
  private File coreData;
  private FileConfiguration coreDataConfig;
  
  private File playerFolder;
  private Map<UUID, PlayerInvFile> playerFileConfigMap;
  
  public MultiInvFileSystem(Plugin plugin) {
    this.plugin = plugin;
    this.server = plugin.getServer();
    
    playerFileConfigMap = new HashMap<UUID, PlayerInvFile>();
    
    init();
  }
  
  private void init() {
    pluginFolder = plugin.getDataFolder();
    
    if (!pluginFolder.exists()) {
      pluginFolder.mkdir();
      server.getLogger().info("Plugin folder created!");
    }
    
    try {
      
      coreData = new File(pluginFolder.getAbsolutePath()+File.separator+"CoreData.yml");
      
      if(!coreData.exists())
      {
        coreData.createNewFile();
        server.getLogger().info("Core data file created!");  
      }
      
      coreDataConfig = YamlConfiguration.loadConfiguration(coreData);
      
      playerFolder = new File(pluginFolder, "PlayerData");
      if(!playerFolder.exists())
        playerFolder.mkdir();
      
      File[] playerFiles = playerFolder.listFiles();
      
      for (File playerFile : playerFiles) {
        String name = playerFile.getName().replace(".yml", "");
        
        playerFileConfigMap.put(UUID.fromString(name), new PlayerInvFile(playerFile));
      }
      
    } catch (IOException e) 
    {
      server.getLogger().info("Failed to load make new config! Shutting down.");
      server.shutdown();
    }
    catch(Exception e)
    {
      server.getLogger().info("Something unexpected went wrong!");
      server.getLogger().info(e.toString());
      server.shutdown();
    }
    
  }

  public void saveGame()
  {
    try {
      coreDataConfig.save(coreData);
    } catch (IOException e) {
      server.getLogger().severe("Error Saving Config File!");
      server.getLogger().severe("Disabling plugin");
      server.getPluginManager().disablePlugin(plugin);
      
    }
  }
  
  public void savePlayeInvFile(UUID playerUID) {
    PlayerInvFile invFile = playerFileConfigMap.get(playerUID);
    
    if(invFile != null) {
      invFile.saveFile();
    }
  }
  
  public FileConfiguration getPlayerFileConfig(UUID playerUID) {
   PlayerInvFile playerInvFile =  playerFileConfigMap.get(playerUID);
   
   if (playerInvFile == null) {
     File playerFile = new File(playerFolder, playerUID.toString()+".yml");
     
     if (!playerFile.exists()) {
       try {
        playerFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
     }
     
     playerInvFile = new PlayerInvFile(playerFile);
   }
   
   return playerInvFile.getConfig();
  }

  
  public FileConfiguration getConfig() {
    return coreDataConfig;
  }
  
  public Map<UUID, PlayerInvFile> getPlayerData() {
    return playerFileConfigMap;
  }

}
