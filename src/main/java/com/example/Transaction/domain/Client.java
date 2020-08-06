package com.example.Transaction.domain;

import javax.persistence.*;


@Entity
@Table(name="client")
public class Client {

	@Id
    @GeneratedValue
    private Long id;
	private String name;
	
	private int balance;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
	
}
