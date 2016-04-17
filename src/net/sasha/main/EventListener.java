package net.sasha.main;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class EventListener implements Listener {
  private final MultiInvPlugin plugin;
  
  public EventListener(MultiInvPlugin main) {
    plugin = main;
  }

  /* This method is probably not necessary */
  @EventHandler
  public void onLeave(PlayerQuitEvent event) {
    InventoryManager invManager = plugin.getInvManager();
    
    Player quitter = event.getPlayer();
    
    UUID worldUID = quitter.getLocation().getWorld().getUID();
    UUID playerUID = quitter.getUniqueId();
    
    invManager.savePlayersWorldInv(playerUID, worldUID, quitter.getInventory());
  }
  
  /* This runs on the assumption that every world change
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
        
        invManager.savePlayersWorldInv(teleportersUID, 
                                       prevWorldUID, teleporter.getInventory());
        
        PlayerInventory teleporterInv = teleporter.getInventory();
        MultiInventory newInvStuff = invManager.getPlayersWorldInv(teleportersUID, 
                                                                     newWorldUID);
       
        teleporterInv.clear();
        
        if(newInvStuff != null) {
          teleporterInv.setContents(newInvStuff.getContent().clone());
          teleporterInv.setArmorContents(newInvStuff.getArmor().clone());
        }
      }
    }
  }
}
