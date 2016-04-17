package net.sasha.main;

import org.bukkit.inventory.ItemStack;

public class MultiInventory {
  private final ItemStack[] armor;
  private final ItemStack[] content;
  
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
}
