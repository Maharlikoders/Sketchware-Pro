package mod.elfilibustero.sketch.lib.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class NewFileUtil {

	public static long getSize(String path) throws IOException {
		Path paths = Paths.get(path);

		if (Files.isDirectory(paths)) {
			return getFolderSize(path);
		} else if (Files.isRegularFile(paths)) {
			return Files.size(paths);
		} else {
			return 0;
		}
	}

	public static long getFolderSize(String path) throws IOException {
		Path folder = Paths.get(path);
		long size = 0;

		if (Files.isDirectory(folder)) {
			EnumSet<FileVisitOption> options = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
			Files.walk(folder, options, Integer.MAX_VALUE)
				.filter(Files::isRegularFile)
				.forEach(file -> {
					try {
						size += Files.size(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
		}

		return size;
	}

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