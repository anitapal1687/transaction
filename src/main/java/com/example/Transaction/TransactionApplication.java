package com.example.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.example.Transaction.service.ClientService;

@SpringBootApplication(scanBasePackages = {"com.example.Transaction.*"})
public class TransactionApplication /*implements CommandLineRunner*/ {


	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}

/*	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Entering");
		
		service.loadUserByClient("anita");
		
	}
*/
}
