package com.mitec.business.utils;

import lombok.Data;

@Data
public class UploadedFile {
	private String uploadedPath;
	private String uploadedFileName;
	private byte[] uploadedFileContent;
}
