package net.sasha.file;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;

public class SyncConfigWrapper {
  private final FileConfiguration fileConfig;
  
  private long lastModified;
  private long lastSaved;

  public SyncConfigWrapper(FileConfiguration config) {
    fileConfig = config;
    
    lastModified = System.currentTimeMillis();
    lastSaved = 0;
  }

  @SuppressWarnings("unchecked")
  public synchronized <T> List<T> getList(String path) {
    return (List<T>) fileConfig.getList(path);
  }

  /* The returned set is designed to just be read */
  public synchronized Set<String> getConfigSectionKeys(String path) {
    Set<String> keys =  fileConfig.getConfigurationSection(path).getKeys(false);

    return new LinkedHashSet<String>(keys);
  }

  public synchronized void set(String path, Object value) {
    fileConfig.set(path, value);
    lastModified = System.currentTimeMillis();
  }

  public synchronized void save(File fileToSave) throws IOException {
    if(modifiedSinceLastSave()) {
      fileConfig.save(fileToSave);
      lastSaved = System.currentTimeMillis();
    }
  }
  
  private boolean modifiedSinceLastSave() {
    return lastModified - lastSaved > 0;
  }
}
