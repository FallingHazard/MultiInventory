package net.sasha.main;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.sasha.file.MultiInvFileSystem;
import net.sasha.file.PlayerInvFile;
import net.sasha.file.SyncConfigWrapper;

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
    invManager.runTaskTimer(this, 0L, 10L);
    super.onEnable();
  }

  public InventoryManager getInvManager() {
    return invManager;
  }

  public MultiInvFileSystem getFileSystem() {
    return fileSystem;
  }

  /* In this version ALL files are loaded into memory */
  private void loadPlayerInventories() {
    Map<UUID, PlayerInvFile> playerInvData = fileSystem.getPlayerData();

    for(Entry<UUID, PlayerInvFile> playerInvEntry : playerInvData.entrySet()) {
      UUID playerUUID = playerInvEntry.getKey();

      SyncConfigWrapper playerInvConfig = playerInvEntry.getValue().getConfig();

      for(String key
          : playerInvConfig.getConfigSectionKeys("")) {
        UUID worldUID = UUID.fromString(key);

        List<ItemStack> invContent = playerInvConfig.getList(key+".contents");
        List<ItemStack> armorContent = playerInvConfig.getList(key+".armor");

        ItemStack[] invArray = invContent.toArray(new ItemStack[invContent.size()]);
        ItemStack[] armorArray = armorContent.toArray(new ItemStack[armorContent.size()]);

        invManager.setPlayersWorldInv(playerUUID,
                                       worldUID,
                                       new MultiInventory(armorArray, invArray));
      }
    }
  }

}
