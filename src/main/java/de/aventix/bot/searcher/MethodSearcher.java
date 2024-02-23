package de.aventix.bot.searcher;

import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.aventix.bot.filter.MethodFilter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* This class was created by @Aventix on 05.06.2022 at 23:56. */
@Singleton
public class MethodSearcher {
  private final ClassLoader classLoader;

  private ClassPath classPath;

  @Inject
  public MethodSearcher(ClassLoader classLoader) {
    this.classLoader = classLoader;

    try {
      this.classPath = ClassPath.from(this.classLoader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Stream<Method> filter(String[] packageNames, MethodFilter... methodFilters) {
    return (packageNames.length == 0
            ? this.classPath.getAllClasses()
            : Arrays.stream(packageNames)
                .flatMap(
                    packageName -> this.classPath.getTopLevelClassesRecursive(packageName).stream())
                .collect(Collectors.toList()))
        .stream()
            .map(
                classInfo -> {
                  try {
                    return classInfo.load();
                  } catch (Throwable ignored) {
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
            .filter(
                method ->
                    Arrays.stream(methodFilters)
                        .allMatch(methodFilter -> methodFilter.matches(method)));
  }
}
