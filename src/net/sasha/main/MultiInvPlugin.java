package net.sasha.main;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.sasha.file.MultiInvFileSystem;
import net.sasha.file.PlayerInvFile;

public class MultiInvPlugin extends JavaPlugin{
  private InventoryManager invManager;
  private MultiInvFileSystem fileSystem;

  @Override
  public void onDisable() {
    getServer().getScheduler().cancelTasks(this);
    
    invManager.saveAllInvsToFile();
    fileSystem.saveAllPlayerFiles();
    super.onDisable();
  }

  @Override
  public void onEnable() {
    invManager = new InventoryManager(this);
    fileSystem = new MultiInvFileSystem(this);
    
    loadPlayerInventories();
    
    getServer().getPluginManager().registerEvents(new EventListener(this), this);
    
    /* Short time for testing ! */
    invManager.runTaskTimer(this, 0L, 36000L);
    super.onEnable();
  }
  
  public InventoryManager getInvManager() {
    return invManager;
  }
  
  public MultiInvFileSystem getFileSystem() {
    return fileSystem;
  }
  
  /* In this version ALL files are loaded into memory */
  @SuppressWarnings("unchecked")
  private void loadPlayerInventories() {
    Map<UUID, PlayerInvFile> playerInvData = fileSystem.getPlayerData();
    
    for(Entry<UUID, PlayerInvFile> playerInvEntry : playerInvData.entrySet()) {
      UUID playerUUID = playerInvEntry.getKey();
      
      FileConfiguration playerInvConfig = playerInvEntry.getValue().getConfig();
      
      for(String key 
          : playerInvConfig.getConfigurationSection("").getKeys(false)) {
        UUID worldUID = UUID.fromString(key);
        
        List<ItemStack> invContent = (List<ItemStack>) playerInvConfig.getList(key+".contents");
        List<ItemStack> armorContent = (List<ItemStack>) playerInvConfig.getList(key+".armor");
        
        ItemStack[] invArray = invContent.toArray(new ItemStack[invContent.size()]);
        ItemStack[] armorArray = armorContent.toArray(new ItemStack[armorContent.size()]);
        
        invManager.savePlayersWorldInv(playerUUID, 
                                       worldUID, 
                                       new MultiInventory(armorArray, invArray));
      }
    }
  }
  
}
