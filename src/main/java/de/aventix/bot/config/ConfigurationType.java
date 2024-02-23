package de.aventix.bot.config;

/* This interface was created by @Aventix on 06.06.2022 at 00:00. */
public interface ConfigurationType {
  void onLoad(Class<? extends Config> clazz);

  void onSave(Class<? extends Config> clazz);

  String fileExtension();
}
