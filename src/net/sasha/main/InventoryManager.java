package net.sasha.main;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.sasha.file.MultiInvFileSystem;
import net.sasha.file.SyncConfigWrapper;

public class InventoryManager extends BukkitRunnable{
  private final Table<UUID, UUID, MultiInventory> playerWorldInvTable;
  
  private final MultiInvPlugin plugin;
  
  public InventoryManager(MultiInvPlugin multiInvPlugin) {
    plugin = multiInvPlugin;
    playerWorldInvTable = HashBasedTable.create();
  }

  public void setPlayersWorldInv(UUID playerUUID, 
                                 UUID worldUID, PlayerInventory inventory) {
    setPlayersWorldInv(playerUUID, 
                       worldUID, 
                       new MultiInventory(inventory.getArmorContents(), 
                                          inventory.getContents()));
  }
  
  public void setPlayersWorldInv(UUID playersUUID, 
                                 UUID worldUID, MultiInventory inventory) {
    playerWorldInvTable.put(playersUUID, worldUID, inventory);
  }

  public MultiInventory getPlayersWorldInv(UUID playersUID, UUID worldUID) {
    MultiInventory playersInv =  playerWorldInvTable.get(playersUID, worldUID);
    
    if(playersInv == null) {
      playersInv = new MultiInventory(new ItemStack[4], new ItemStack[36]);
      playerWorldInvTable.put(playersUID, worldUID, playersInv);
    }
    
    return playersInv;
  }

  @Override
  public void run() {
    saveInventories();
  }

  @SuppressWarnings("deprecation")
  private void saveInventories() {
    MultiInvFileSystem fileSystem = plugin.getFileSystem();
    
    for(Entry<UUID, Map<UUID, MultiInventory>> playerWorldInvEntry 
        : playerWorldInvTable.rowMap().entrySet()) {
      
      UUID playerUUID = playerWorldInvEntry.getKey();
      SyncConfigWrapper playerConfig = fileSystem.getPlayerFileConfig(playerUUID);
      
      Map<UUID, MultiInventory> worldInvMap = playerWorldInvEntry.getValue();
      
      for(Entry<UUID, MultiInventory> worldInvEntry : worldInvMap.entrySet()) {
        UUID worldUID = worldInvEntry.getKey();
        MultiInventory someInventory = worldInvEntry.getValue();
        
        if(!someInventory.hasBeenSaved()) {
          playerConfig.set(worldUID.toString()+".contents", someInventory.getContent());
          playerConfig.set(worldUID.toString()+".armor", someInventory.getArmor());
          
          someInventory.setSaved();
        }
      }
    }
    
    if(plugin.isEnabled())
      plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
                                                                 new Runnable() {
        @Override
        public void run() {
          fileSystem.saveAllPlayerFiles();
        }
      }, 5L);
  }

  public void saveAllInvsToFile() {
    saveInventories();
  }
  
}
