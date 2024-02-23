package de.aventix.bot.config;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.aventix.bot.filter.TypeFilter;
import de.aventix.bot.searcher.TypeSearcher;

import java.util.Arrays;

/* This class was created by @Aventix on 05.06.2022 at 23:38. */
@Singleton
public class ConfigRegistrationProcedure {
  private final TypeSearcher typeSearcher;
  private final Injector injector;

  @Inject
  public ConfigRegistrationProcedure(TypeSearcher typeSearcher, Injector injector) {
    this.typeSearcher = typeSearcher;
    this.injector = injector;
  }

  public Injector load(Object... args) {
    Preconditions.checkNotNull(args);
    this.typeSearcher
        .filter(
            Arrays.stream(args).toArray(String[]::new),
            TypeFilter.subClassOf(Config.class),
            TypeFilter.annotatedWith(Configuration.class))
        .forEach(
            clazz -> {
              Configuration annotation = clazz.getAnnotation(Configuration.class);
              ConfigurationType type = this.injector.getInstance(annotation.type());
              Config config = (Config) this.injector.getInstance(clazz);
              type.onLoad(config.getClass());
              System.out.println(
                  "Successfully loaded configuration "
                      + annotation.filename()
                      + "."
                      + type.fileExtension());
            });
    return injector;
  }
}
