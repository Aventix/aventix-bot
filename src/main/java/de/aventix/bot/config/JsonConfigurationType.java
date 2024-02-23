package de.aventix.bot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.io.*;
import java.lang.reflect.Field;

/* This class was created by @Aventix on 06.06.2022 at 00:01. */
@Singleton
public class JsonConfigurationType implements ConfigurationType {
  private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final Injector injector;

  @Inject
  public JsonConfigurationType(Injector injector) {
    this.injector = injector;
  }

  public void onLoad(Class<? extends Config> clazz) {
    Configuration annotation = clazz.getAnnotation(Configuration.class);
    File file = new File(annotation.path(), annotation.filename() + "." + this.fileExtension());

    try {
      if (!file.exists()) {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
          System.out.println("Can't create config file!");
        }
        if (!file.createNewFile()) {
          System.out.println("Can't create config file!");
        } else {
          System.out.println("File doesnt exist, save new file...");
          this.onSave(clazz);
          return;
        }
      }

      Config config = GSON.fromJson(new BufferedReader(new FileReader(file)), clazz);
      Config instance = this.injector.getInstance(clazz);
      for (Field declaredField : clazz.getDeclaredFields()) {
        declaredField.setAccessible(true);
        if (instance != null && config != null && declaredField.get(config) != null)
          declaredField.set(instance, declaredField.get(config));
      }
    } catch (Exception e) {
      System.out.println("Exception when try to load the config");
      e.printStackTrace();
    }
  }

  public void onSave(Class<? extends Config> clazz) {
    Configuration annotation = clazz.getAnnotation(Configuration.class);
    File path = new File(annotation.path());
    File file = new File(path, annotation.filename() + "." + this.fileExtension());

    if (!path.exists() && !path.mkdirs()) {
      System.out.println("Can't create folder!");
      return;
    }

    try (Writer writer = new FileWriter(file)) {
      GSON.toJson(injector.getInstance(clazz), writer);
    } catch (IOException e) {
      System.out.println("Can't save config file: " + file.getName());
      e.printStackTrace();
    }
  }

  public String fileExtension() {
    return "json";
  }
}
