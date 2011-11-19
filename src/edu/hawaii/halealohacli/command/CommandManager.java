package edu.hawaii.halealohacli.command;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.wattdepot.client.WattDepotClient;
import java.lang.reflect.Constructor;

/**
 * return a list of instances of all the Command classes.
 * 
 * @author Toy Lim
 */
public class CommandManager {
  private List<Command> commandList;

  /**
   * Default constructor.
   * 
   * @param client WattDepotClient client for data query
   */
  public CommandManager(WattDepotClient client) {
    commandList = new ArrayList<Command>();

    List<Class<?>> classes = getClassesForPackage(this.getClass().getPackage());
    for (Class<?> classObject : classes) {
      if (classObject.isInterface()) {
        continue;
      }
      Class<?>[] interfaces = classObject.getInterfaces();
      Boolean isCommand = false;
      for (Class<?> interfaceObject : interfaces) {
        if ("edu.hawaii.halealohacli.command.Command".equals(interfaceObject.getName())) {
          isCommand = true;
        }
      }
      if (isCommand) {
        try {
          Object[] wattDepotClientArg = new Object[] { client };
          Class<?>[] wattDepotClientArgClass = new Class<?>[] { client.getClass() };
          Constructor<?> commandConstructor = classObject.getConstructor(wattDepotClientArgClass);
          Command newCommand = (Command) commandConstructor.newInstance(wattDepotClientArg);
          commandList.add(newCommand);
        }
        catch (Throwable e) {
          System.err.println(e);
          System.out.format("Encounter error while trying to dynamically load commands:\n\t%s\n",
              e.getMessage());
        }
      }
    }
    /*
     * commandList = Arrays.asList(new CurrentPower(client), new DailyEnergy(client), new
     * EnergySince(client), new RankTowers(client), new Help(client));
     */
  }

  /**
   * Return a list of instances of all the Command classes.
   * 
   * @return an array of instances of all the Command classes
   */
  public Command[] getCommands() {
    return commandList.toArray(new Command[commandList.size()]);
  }

  /**
   * Search for classes with a package. Taken directly from the web:
   * http://stackoverflow.com/questions
   * /176527/how-can-i-enumerate-all-classes-in-a-package-and-add-them-to-a-list
   * 
   * @param pkg Package to be searched
   * @return List of classes found in the package
   */
  private List<Class<?>> getClassesForPackage(Package pkg) {
    String pkgname = pkg.getName();
    List<Class<?>> classes = new ArrayList<Class<?>>();
    // Get a File object for the package
    File directory = null;
    String fullPath;
    String relPath = pkgname.replace('.', '/');
    // System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
    URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
    // System.out.println("ClassDiscovery: Resource = " + resource);
    if (resource == null) {
      throw new RuntimeException("No resource for " + relPath);
    }
    fullPath = resource.getFile();
    // System.out.println("ClassDiscovery: FullPath = " + resource);

    try {
      directory = new File(resource.toURI());
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(pkgname + " (" + resource
          + ") does not appear to be a valid URL / URI."
          + "  Strange, since we got it from the system...", e);
    }
    catch (IllegalArgumentException e) {
      directory = null;
    }
    // System.out.println("ClassDiscovery: Directory = " + directory);

    if (directory != null && directory.exists()) {
      // Get the list of the files contained in the package
      String[] files = directory.list();
      for (int i = 0; i < files.length; i++) {
        // we are only interested in .class files
        if (files[i].endsWith(".class")) {
          // removes the .class extension
          String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
          // System.out.println("ClassDiscovery: className = " + className);
          try {
            classes.add(Class.forName(className));
          }
          catch (ClassNotFoundException e) {
            throw new RuntimeException("ClassNotFoundException loading " + className, e);
          }
        }
      }
    }
    else {
      try {
        String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          String entryName = entry.getName();
          if (entryName.startsWith(relPath)
              && entryName.length() > (relPath.length() + "/".length())) {
            // System.out.println("ClassDiscovery: JarEntry: " + entryName);
            if (!entryName.endsWith(".class")) {
              continue;
            }
            String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            // System.out.println("ClassDiscovery: className = " + className);
            try {
              classes.add(Class.forName(className));
            }
            catch (ClassNotFoundException e) {
              throw new RuntimeException("ClassNotFoundException loading " + className, e);
            }
          }
        }
      }
      catch (IOException e) {
        throw new RuntimeException(pkgname + " (" + directory
            + ") does not appear to be a valid package", e);
      }
    }
    return classes;
  }
}
