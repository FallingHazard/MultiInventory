package net.sasha.main;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EventListener implements Listener {
  private final MultiInvPlugin plugin;
  
  public EventListener(MultiInvPlugin main) {
    plugin = main;
  }

  /* This method is probably not necessary */
  @EventHandler(priority=EventPriority.MONITOR)
  public void onLeave(PlayerQuitEvent event) {
    InventoryManager invManager = plugin.getInvManager();
    
    Player quitter = event.getPlayer();
    
    UUID worldUID = quitter.getLocation().getWorld().getUID();
    UUID playerUID = quitter.getUniqueId();
    
    invManager.savePlayersWorldInv(playerUID, worldUID, quitter.getInventory());
  }
  
  /* 
   * This runs on the assumption that every world change
   * results in some sort of teleport event, hopefully...
   */
  @EventHandler(priority=EventPriority.MONITOR)
  public void onTeleport(PlayerTeleportEvent event) {   
    /* Make sure the event was not cancelled */
    if(!event.isCancelled()) {
      UUID prevWorldUID = event.getFrom().getWorld().getUID();
      UUID newWorldUID = event.getTo().getWorld().getUID();
      
      /* If they changed World */
      if(!prevWorldUID.equals(newWorldUID)) {
        InventoryManager invManager = plugin.getInvManager();
        
        Player teleporter = event.getPlayer();
        UUID teleportersUID = teleporter.getUniqueId();
        PlayerInventory teleporterInv = teleporter.getInventory();
        
        invManager.savePlayersWorldInv(teleportersUID, 
                                       prevWorldUID, teleporterInv);
        
        MultiInventory newInvStuff = invManager.getPlayersWorldInv(teleportersUID, 
                                                                   newWorldUID);
       
        if(newInvStuff != null) {
          teleporterInv.setContents(newInvStuff.getContent());
          teleporterInv.setArmorContents(newInvStuff.getArmor());
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void onDeath(PlayerDeathEvent event) {
    Player dier = event.getEntity();

    UUID worldUID = dier.getLocation().getWorld().getUID();
    UUID dierUID = dier.getUniqueId();
    
    InventoryManager invManager = plugin.getInvManager();
    invManager.savePlayersWorldInv(dierUID, worldUID, 
                                   new MultiInventory(new ItemStack[4], 
                                                      new ItemStack[36])); 
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void onRespawn(PlayerRespawnEvent event) {
    Player respawner = event.getPlayer();
    
    UUID respawnerUID = respawner.getUniqueId();
    UUID worldUID = event.getRespawnLocation().getWorld().getUID();
    
    InventoryManager invManager = plugin.getInvManager();
    
    MultiInventory newRespawnerInv = invManager.getPlayersWorldInv(respawnerUID,
                                                                   worldUID);
    
    respawner.getInventory().setArmorContents(newRespawnerInv.getArmor());
    respawner.getInventory().setContents(newRespawnerInv.getContent());
  }
  
}
