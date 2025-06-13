package com.mitec.business.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SpringUploadUtil {
	
	public static final String FILE_SEP = "/";

	public static void uploadFiles(List<UploadedFile> uploadedFiles) throws IOException {
		if (uploadedFiles != null && !uploadedFiles.isEmpty()) {
			for (UploadedFile uploadedFile : uploadedFiles) {
				SpringUploadUtil.uploadFile(uploadedFile);
			}
		}
	}

	public static void uploadFile(UploadedFile uploadedFile) throws IOException {
		if (uploadedFile != null) {
			Path uploadedFolder = Paths.get(uploadedFile.getUploadedPath());
			if (!uploadedFolder.toFile().exists()) {
				Files.createDirectories(uploadedFolder);
			}
			
			Path file = Paths.get(uploadedFile.getUploadedPath() + FILE_SEP + uploadedFile.getUploadedFileName());
			if (file.toFile().exists()) {
				Files.delete(file);
			}
			
			Files.write(file, uploadedFile.getUploadedFileContent());
		}
	}
	
	public static void deleteFile(String path, String filePath) throws IOException {
		Path file = Paths.get(path + FILE_SEP + filePath);
		if (file.toFile().exists()) {
			Files.delete(file);
		}
	}
}
