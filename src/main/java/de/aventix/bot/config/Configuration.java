package de.aventix.bot.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* This annotation was created by @Aventix on 05.06.2022 at 23:59. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
  Class<? extends ConfigurationType> type() default JsonConfigurationType.class;

  String path() default "./config";

  String filename();
}
