package net.sasha.main;

import org.bukkit.inventory.ItemStack;

/* Multi Inventories are immutable
 * Updating a players multi inv means creating a new one.
 * 
 * Once a multi Inv has been saved there is no point
 * in saving it again due to this immutability.
 */
public class MultiInventory {
  private final ItemStack[] armor;
  private final ItemStack[] content;
  
  private boolean saved = false;
  
  public MultiInventory(ItemStack[] someArmor, ItemStack[] someContent) {
    armor = someArmor;
    content = someContent;
  }
  
  public ItemStack[] getArmor() {
    return armor;
  }
  
  public ItemStack[] getContent() {
    return content;
  }
  
  public void setSaved() {
    saved = true;
  }
  
  public boolean hasBeenSaved() {
    return saved;
  }
  
}
