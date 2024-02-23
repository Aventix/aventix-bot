package de.aventix.bot.config;

import com.google.inject.Injector;

import javax.inject.Inject;

/* This class was created by @Aventix on 05.06.2022 at 23:59. */
public class Config {
  @Inject private transient Injector injector;

  public void save() {
    Configuration annotation = this.getClass().getAnnotation(Configuration.class);
    ConfigurationType type = this.injector.getInstance(annotation.type());
    type.onSave(this.getClass());
  }
}
