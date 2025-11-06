package com.formulagrid.FormulaGrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FormulaGridApplication {

	public static void main(String[] args) {
		SpringApplication.run(FormulaGridApplication.class, args);
	}

}
