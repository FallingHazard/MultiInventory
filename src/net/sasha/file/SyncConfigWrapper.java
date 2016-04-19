package net.sasha.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.configuration.file.FileConfiguration;

public class SyncConfigWrapper {
  private final FileConfiguration fileConfig;

  public SyncConfigWrapper(FileConfiguration config) {
    fileConfig = config;
  }

  @SuppressWarnings("unchecked")
  public synchronized <T> List<T> getList(String path) {
    return (List<T>) fileConfig.getList(path);
  }

  /* The returned set is designed to just be read */
  public synchronized Set<String> getConfigSectionKeys(String path) {
    Set<String> keys =  fileConfig.getConfigurationSection(path).getKeys(false);

    return new TreeSet<String>(keys);
  }

  public synchronized void set(String path, Object value) {
    fileConfig.set(path, value);
  }

  public synchronized void save(File fileToSave) throws IOException {
    fileConfig.save(fileToSave);
  }

}
