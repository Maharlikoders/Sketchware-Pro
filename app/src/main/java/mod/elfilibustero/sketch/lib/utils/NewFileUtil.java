package mod.elfilibustero.sketch.lib.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class NewFileUtil {

	public static void copyFiles(String source, String destination, Set<String> exclusions) throws IOException {
		Path sourceDir = Paths.get(source);
		Path destDir = Paths.get(destination);

		if (!Files.exists(sourceDir)) {
			return;
		}

		Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (!exclusions.contains(file.getFileName().toString())) {
					Path relativePath = sourceDir.relativize(file);
					Path destFile = destDir.resolve(relativePath);
					Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.SKIP_SUBTREE;
			}
		});
	}

	public static void copyDir(String source, String destination) throws IOException {
		Path sourceDir = Paths.get(source);
		Path destDir = Paths.get(destination);

		if (!Files.exists(sourceDir)) {
			return;
		}

		Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Path relativePath = sourceDir.relativize(file);
				Path destFile = destDir.resolve(relativePath);
				Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path relativePath = sourceDir.relativize(dir);
				Path destinationDir = destDir.resolve(relativePath);
				Files.createDirectories(destinationDir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}