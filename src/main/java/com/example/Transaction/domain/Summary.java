package com.example.Transaction.domain;

public class Summary {

	private int creditAmount;
	private int debitAmount;
	private String owing;
	private int balance;
	private int transferredAmont;
	public int getTransferredAmont() {
		return transferredAmont;
	}
	public void setTransferredAmont(int transferredAmont) {
		this.transferredAmont = transferredAmont;
	}
	public int getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(int creditAmount) {
		this.creditAmount = creditAmount;
	}
	public int getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(int debitAmount) {
		this.debitAmount = debitAmount;
	}
	public String getOwing() {
		return owing;
	}
	public void setOwing(String owing) {
		this.owing = owing;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
}
