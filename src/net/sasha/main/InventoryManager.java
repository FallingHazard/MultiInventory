package net.sasha.main;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.sasha.file.MultiInvFileSystem;

public class InventoryManager extends BukkitRunnable{
  private final Table<UUID, UUID, MultiInventory> playerWorldInvTable;
  
  private final MultiInvPlugin plugin;
  
  public InventoryManager(MultiInvPlugin multiInvPlugin) {
    plugin = multiInvPlugin;
    playerWorldInvTable = HashBasedTable.create();
  }

  public void savePlayersWorldInv(UUID playerUUID, 
                                  UUID worldUID, PlayerInventory inventory) {
    savePlayersWorldInv(playerUUID, 
                        worldUID, 
                        new MultiInventory(inventory.getArmorContents().clone(), 
                                           inventory.getContents().clone()));
  }
  
  public void savePlayersWorldInv(UUID playersUUID, 
                                  UUID worldUID, MultiInventory inventory) {
    playerWorldInvTable.put(playersUUID, worldUID, inventory);
  }

  public MultiInventory getPlayersWorldInv(UUID playersUID, UUID worldUID) {
    return playerWorldInvTable.get(playersUID, worldUID);
  }

  @Override
  public void run() {
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      
      @Override
      public void run() {
        plugin.getServer().broadcastMessage(ChatColor.RED
                                             + (ChatColor.BOLD 
                                             + "Saving Inventory Data..."));
      }
    }, 0L);
    
    saveInventories();
  }

  private void saveInventories() {
    MultiInvFileSystem fileSystem = plugin.getFileSystem();
    
    synchronized (playerWorldInvTable) {
      for(Entry<UUID, Map<UUID, MultiInventory>> playerWorldInvEntry 
          : playerWorldInvTable.rowMap().entrySet()) {
        
        UUID playerUUID = playerWorldInvEntry.getKey();
        FileConfiguration playerConfig = fileSystem.getPlayerFileConfig(playerUUID);
        
        Map<UUID, MultiInventory> worldInvMap = playerWorldInvEntry.getValue();
        
        for(Entry<UUID, MultiInventory> worldInvEntry : worldInvMap.entrySet()) {
          UUID worldUID = worldInvEntry.getKey();
          MultiInventory armorAndContents = worldInvEntry.getValue();
          
          playerConfig.set(worldUID.toString()+".contents", armorAndContents.getContent());
          playerConfig.set(worldUID.toString()+".armor", armorAndContents.getArmor());
        }
        
        fileSystem.savePlayeInvFile(playerUUID);
      }
    }
  }

  public void saveAllInvsToFile() {
    saveInventories();
  }
  
}
