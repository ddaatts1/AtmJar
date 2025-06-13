package com.mitec.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("app")
public class AppProperties {
	private String attachmentMiniPath;
	private String attachmentPath;
	private String attachmentContextPath;
	private String attachmentAntMatchers;
}
