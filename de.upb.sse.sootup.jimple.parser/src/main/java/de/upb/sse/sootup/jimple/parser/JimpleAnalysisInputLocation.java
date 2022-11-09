package de.upb.sse.sootup.jimple.parser;

import de.upb.sse.sootup.core.IdentifierFactory;
import de.upb.sse.sootup.core.frontend.AbstractClassSource;
import de.upb.sse.sootup.core.frontend.ClassProvider;
import de.upb.sse.sootup.core.frontend.SootClassSource;
import de.upb.sse.sootup.core.inputlocation.AnalysisInputLocation;
import de.upb.sse.sootup.core.inputlocation.FileType;
import de.upb.sse.sootup.core.model.AbstractClass;
import de.upb.sse.sootup.core.model.SootClass;
import de.upb.sse.sootup.core.model.SourceType;
import de.upb.sse.sootup.core.types.ClassType;
import de.upb.sse.sootup.core.util.PathUtils;
import de.upb.sse.sootup.core.util.StreamUtils;
import de.upb.sse.sootup.core.views.View;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** @author Markus Schmidt */
public class JimpleAnalysisInputLocation<T extends SootClass<? extends SootClassSource<T>>>
    implements AnalysisInputLocation<T> {
  final Path path;

  /** Variable to track if user has specified the SourceType. By default, it will be set to null. */
  private SourceType srcType = null;

  // TODO: allow pointing to a single file
  public JimpleAnalysisInputLocation(@Nonnull Path path) {
    if (!Files.exists(path)) {
      throw new IllegalArgumentException(
          "The configured path '"
              + path
              + "' pointing to '"
              + path.toAbsolutePath()
              + "' does not exist.");
    }
    this.path = path;
  }

  public JimpleAnalysisInputLocation(@Nonnull Path path, @Nullable SourceType srcType) {
    if (!Files.exists(path)) {
      throw new IllegalArgumentException(
          "The configured path '"
              + path
              + "' pointing to '"
              + path.toAbsolutePath()
              + "' does not exist.");
    }
    this.path = path;
    setSpecifiedAsBuiltInByUser(srcType);
  }

  /**
   * The method sets the value of the variable srcType.
   *
   * @param srcType the source type for the path can be Library, Application, Phantom.
   */
  public void setSpecifiedAsBuiltInByUser(@Nullable SourceType srcType) {
    this.srcType = srcType;
  }

  @Override
  public SourceType getSourceType() {
    return srcType;
  }

  @Nonnull
  List<AbstractClassSource<? extends AbstractClass<?>>> walkDirectory(
      @Nonnull Path dirPath,
      @Nonnull IdentifierFactory factory,
      @Nonnull ClassProvider<? extends SootClass<?>> classProvider) {
    try {
      final FileType handledFileType = classProvider.getHandledFileType();
      return Files.walk(dirPath)
          .filter(filePath -> PathUtils.hasExtension(filePath, handledFileType))
          .flatMap(
              p ->
                  StreamUtils.optionalToStream(
                      Optional.of(
                          classProvider.createClassSource(this, p, factory.fromPath(dirPath, p)))))
          .collect(Collectors.toList());

    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  @Nonnull
  public Collection<? extends SootClassSource<T>> getClassSources(@Nonnull View<?> view) {
    return walkDirectory(
        path, view.getIdentifierFactory(), new JimpleClassProvider(view.getBodyInterceptors()));
  }

  @Override
  @Nonnull
  public Optional<? extends SootClassSource<T>> getClassSource(
      @Nonnull ClassType type, @Nonnull View<?> view) {
    final JimpleClassProvider<T> classProvider =
        new JimpleClassProvider<>(view.getBodyInterceptors());

    final String ext = classProvider.getHandledFileType().toString().toLowerCase();

    // is file under path:  with name package.subpackage.class.jimple
    Path pathToClass = path.resolve(type.getFullyQualifiedName() + "." + ext);
    if (!Files.exists(pathToClass)) {
      // is file under path with dir structure: package/subpackage/className.jimple
      pathToClass =
          path.resolve(
              type.getPackageName().toString().replace('.', File.separatorChar)
                  + File.separator
                  + type.getClassName()
                  + "."
                  + ext);
      if (!Files.exists(pathToClass)) {
        return Optional.empty();
      }
    }

    return Optional.of(classProvider.createClassSource(this, pathToClass, type));
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof JimpleAnalysisInputLocation)) {
      return false;
    }
    return path.equals(((JimpleAnalysisInputLocation<?>) o).path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path);
  }
}