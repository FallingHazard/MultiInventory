public class SyncConfigWrapper {
  private final FileConfiguration fileConfig;

  public SyncConfigWrapper(FileConfiguration config) {
    fileConfig = config;
  }

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
