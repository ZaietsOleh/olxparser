package com.olehzaiets.olxparser;

import com.olehzaiets.olxparser.common.WebDriverManagerWrapper;
import com.olehzaiets.olxparser.parser.OlxParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class OlxparserApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlxparserApplication.class, args);
		WebDriverManagerWrapper.setup();
		OlxParser.proceedStartArguments(args);
	}

}
