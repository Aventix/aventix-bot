package de.aventix.bot.filter;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/* This interface was created by @Aventix on 05.06.2022 at 23:41. */
public interface TypeFilter {
  boolean matches(Class<?> clazz);

  static TypeFilter subClassOf(Class<?> superClass) {
    return clazz -> !superClass.equals(clazz) && superClass.isAssignableFrom(clazz);
  }

  static TypeFilter annotatedWith(Class<? extends Annotation> annotation) {
    return clazz -> clazz.isAnnotationPresent(annotation);
  }

  static TypeFilter matchesAnyMethod(MethodFilter... methodFilters) {
    return clazz ->
        Arrays.stream(clazz.getDeclaredMethods())
            .anyMatch(
                method ->
                    Arrays.stream(methodFilters)
                        .allMatch(methodFilter -> methodFilter.matches(method)));
  }
}
