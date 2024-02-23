package de.aventix.bot.searcher;

import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.aventix.bot.filter.TypeFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* This class was created by @Aventix on 05.06.2022 at 23:47. */
@Singleton
public class TypeSearcher {
  private final ClassLoader classLoader;

  private ClassPath classPath;

  @Inject
  public TypeSearcher(ClassLoader classLoader) {
    this.classLoader = classLoader;

    try {
      this.classPath = ClassPath.from(this.classLoader);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  public Stream<? extends Class<?>> filter(String[] packageNames, TypeFilter... filters) {
    Preconditions.checkNotNull(packageNames);
    Preconditions.checkNotNull(filters);

    return (packageNames.length == 0
            ? this.classPath.getAllClasses()
            : Arrays.stream(packageNames)
                .flatMap(name -> this.classPath.getTopLevelClassesRecursive(name).stream())
                .collect(Collectors.toList()))
        .stream()
            .map(
                classInfo -> {
                  try {
                    return classInfo.load();
                  } catch (Throwable exception) {
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .filter(clazz -> Arrays.stream(filters).allMatch(filter -> filter.matches(clazz)));
  }
}
