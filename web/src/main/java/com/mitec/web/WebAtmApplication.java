package com.mitec.web;

import com.mitec.business.service.DocxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;



@ComponentScan(basePackages = {"com.mitec.business", "com.mitec.web"})
@SpringBootApplication
public class WebAtmApplication  {

	@Autowired
	private DocxService docxService;
	public static void main(String[] args) {
		SpringApplication.run(WebAtmApplication.class, args);

	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
//	@Override
//	public void run(String... args) throws Exception {
//		// Define file paths and strings
//		String inputFilePath = "C:\\Users\\Admin\\Desktop\\Bao tri dinh ky.docx";
//		String outputFilePath = "C:\\Users\\Admin\\Desktop\\Bao tri dinh ky_updated.docx";
//		String searchString = "(ký, ghi rõ họ tên)";
////		String newText = " [Your additional text here,Your additional text hereYour additional text here,Your additional text here]";
//		String name = " \n\n\n\nĐỗ Tiến Đạt";
//		String address="RH9R+7H4, Vĩnh Tường, Vị Thuỷ, Hậu Giang, Việt Nam";
//		String  serialNumber = "67htttfhd7";
//
//		try {
//			// Execute the DOCX modification
//			docxService.addTextAfterString(inputFilePath, outputFilePath);
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.out.println("Error updating document.");
//		}
//	}

}
