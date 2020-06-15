package com.ljp.mychat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ljp.mychat.mapper")
public class MychatApplication {

	public static void main(String[] args) {
		SpringApplication.run(MychatApplication.class, args);
	}

}
