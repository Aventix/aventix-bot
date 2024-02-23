package de.aventix.bot.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/* This interface was created by @Aventix on 05.06.2022 at 23:44. */
public interface MethodFilter {
  boolean matches(Method method);

  static MethodFilter annotatedWith(Class<? extends Annotation> annotation) {
    return method -> method.isAnnotationPresent(annotation);
  }
}
