package gov.loc.repository.bagger.ui.handlers;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FindDirectoriesVisitor extends SimpleFileVisitor<Path> {
  private final List<Path> directories = new ArrayList<>();
  
  @Override
  public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
    directories.add(dir);
    
    return FileVisitResult.CONTINUE;
  }

  public List<Path> getDirectories() {
    return directories;
  }
}
