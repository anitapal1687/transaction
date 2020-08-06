package com.example.Transaction.service;

import java.util.List;

import javax.security.auth.login.LoginException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Transaction.domain.AccountTransaction;
import com.example.Transaction.domain.Client;
import com.example.Transaction.domain.Summary;
import com.example.Transaction.repository.AccountRepository;
import com.example.Transaction.repository.ClientRepository;
import com.example.Transaction.repository.SummaryResponse;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private AccountRepository accountRepository;

	public Client loadUserByClient(String clientname) {
		Client c = clientRepository.findByName(clientname);
		if (c == null) {
			Client newUser = new Client();
			newUser.setName(clientname);
			newUser.setBalance(0);
			return clientRepository.save(newUser);
		}
		
		return c;

	}
	
	public List<SummaryResponse> getCreditDebitAmmount(String clientName){
		
		List<SummaryResponse> response = accountRepository.sumAmount(clientName);
		
		return response;
	}

	@Transactional
	public Summary  topup(String name, int amount) throws LoginException {
		int balance = 0;
		Summary s = new Summary();
		Client c = clientRepository.findByName(name);
		int amountCredit=0;
		int balanceAmount=0;
		int transferredamt=0;
		boolean success=false;
		List<SummaryResponse> response = accountRepository.sumAmount(name);
		if (c != null) {
			if (response != null && !response.isEmpty()) {
				for(int i=0;i<response.size();i++){
					if(response.get(i).getCreditAmount()<0){
					     amountCredit=response.get(i).getCreditAmount()<amount?amount-Math.abs(response.get(i).getCreditAmount()):Math.abs(response.get(i).getCreditAmount());
					     balanceAmount=amountCredit<0? 0: amountCredit;
					     balance = clientRepository.topup(balanceAmount,name);
						
						Client sender = clientRepository.findByName(response.get(i).getOwing());
						 transferredamt+=amountCredit>0? amount-amountCredit :Math.abs(response.get(i).getCreditAmount())-Math.abs(amountCredit);
						balance = clientRepository.topup(sender.getBalance()+transferredamt, response.get(i).getOwing());
						accountRepository.balanceAmount(amountCredit>0? 0: amountCredit, response.get(i).getOwing(),  name);
						accountRepository.balanceAmount(Math.abs(response.get(i).getCreditAmount()+transferredamt),name, response.get(i).getOwing());
						s.setOwing(response.get(i).getOwing());
						success=true;
					}
					
				}
			}if(!success){

				balanceAmount = c.getBalance() + amount;
				balance = clientRepository.topup(amount, name);
			}
		} else {
			throw new LoginException("User is not logged in");
		}
		
	
		s.setBalance(balanceAmount);
		s.setTransferredAmont(transferredamt);
        
		return s;

	}

	
	@Transactional
	public Summary pay(String sendername, int amount, String receiver) {
		Summary s = new Summary();
		int balance = 0;
		int amountSender = 0;
		int amountReceiver = 0;
		int amountCredit =0;
		int debitAmount=0;
		int amountTran=0;
		Client sender = clientRepository.findByName(sendername);
		Client receiverInfo = clientRepository.findByName(receiver);
		if (sender != null && receiverInfo != null) {

			try{
				amountCredit = accountRepository.getCreditAmount(sendername, receiver);
			}catch(Exception e){
				amountCredit =0;
			}
			try{
			 debitAmount = accountRepository.getDebitAmount(sendername, receiver);
			} catch(Exception e){
				debitAmount =0;
			}
			if(sender.getBalance()> amount && amountCredit==0 && debitAmount==0){
				amountSender = sender.getBalance() - amount;
				amountReceiver = receiverInfo.getBalance() + amount;
				amountTran=amount;
				balance = clientRepository.topup(amountSender, sendername);
				balance = clientRepository.topup(amountReceiver, receiver);
				int i=accountRepository.balanceAmount(amountTran,receiver, sendername);
				if(i==0){
					saveaccrepo( receiver,  sendername,  amountTran);
				} 
				i=accountRepository.balanceAmount(Math.abs(amountCredit),sendername, receiver);
				saveaccrepo(sendername, receiver, Math.abs(amountTran));
				
			}
			else if (sender.getBalance()< amount) {
				 amountCredit = sender.getBalance()- amount;
				amountSender=sender.getBalance()<amount? 0: sender.getBalance();
				amountTran=sender.getBalance();
				amountReceiver=receiverInfo.getBalance()+ sender.getBalance();
					balance = clientRepository.topup( amountSender, sendername);
					
					balance = clientRepository.topup(amountReceiver, receiver);
					int i=accountRepository.balanceAmount(amountCredit, receiver,  sendername);
					if(i==0){
						/*AccountTransaction account = new AccountTransaction();
						account.setSender(sendername);
						account.setReceiver(receiver);
						account.setTransferAmount(amountCredit);
						accountRepository.save(account);*/
						saveaccrepo( receiver,  sendername,  amountCredit);
					}
					 i=accountRepository.balanceAmount(Math.abs(amountCredit), sendername, receiver);
					if(i==0){
					/*	AccountTransaction account = new AccountTransaction();
						account.setSender(receiver);
						account.setReceiver(sendername);
						account.setTransferAmount(Math.abs(amountCredit));
						accountRepository.save(account);*/
						saveaccrepo(sendername, receiver, Math.abs(amountCredit));
					}
			} else if(debitAmount!=0){
				if(debitAmount<amount){
				 amountCredit=amount-Math.abs(debitAmount);
				amountSender= sender.getBalance();
				balance = clientRepository.topup(amountSender, sendername);
				amountReceiver=receiverInfo.getBalance();
				balance = clientRepository.topup(amountReceiver, receiver);
				int i = accountRepository.balanceAmount(amountCredit, sendername, receiver);
				if(i==0){
					/*AccountTransaction account = new AccountTransaction();
					account.setSender(receiver);
					account.setReceiver(sendername);
					account.setTransferAmount(amountCredit);
					accountRepository.save(account);*/
					saveaccrepo(sendername, receiver, amountCredit);
				}
				 i=accountRepository.balanceAmount(Math.abs(amountCredit),receiver, sendername);
				if(i==0){
					/*AccountTransaction account = new AccountTransaction();
					account.setSender(sendername);
					account.setReceiver(receiver);
					account.setTransferAmount(Math.abs(amountCredit));
					accountRepository.save(account);*/
					saveaccrepo( receiver,  sendername,  Math.abs(amountCredit));
				}
				} else {
					int amm= amount-debitAmount;
					int amountcredit= sender.getBalance()>amm? amm: sender.getBalance()+debitAmount;
					amountSender=sender.getBalance()>amm? sender.getBalance()-amm:0;
					balance = clientRepository.topup(amountSender , sendername);
					amountReceiver=amountcredit;
					balance = clientRepository.topup(amountReceiver, receiver);
					accountRepository.balanceAmount(amountcredit, receiver,  sendername);
				}
				
			} 
			   
			/*	int i=accountRepository.balanceAmount(amountTran,receiver, sendername);
				if(i==0){
				AccountTransaction account = new AccountTransaction();
				account.setSender(sendername);
				account.setReceiver(receiver);
				account.setTransferAmount(amountTran);
				accountRepository.save(account);
				}
				
				 i=accountRepository.balanceAmount(Math.abs(amountCredit),sendername, receiver);
				 if(i==0){
				AccountTransaction accoun = new AccountTransaction();
				accoun.setSender( receiver);
				accoun.setReceiver(sendername);
				accoun.setTransferAmount(Math.abs(amountTran));
				accountRepository.save(accoun);
			  }*/
			//}
			List<SummaryResponse> res = accountRepository.sumAmount(sendername);
			if(res!=null && !res.isEmpty())
			if(res.get(0).getCreditAmount()<0)	{
			s.setCreditAmount(res.get(0).getCreditAmount());
			s.setOwing(res.get(0).getOwing());
			s.setTransferredAmont(amountTran);
			} else if(res.get(0).getCreditAmount()>0){
			s.setDebitAmount(res.get(0).getCreditAmount());
			s.setOwing(res.get(0).getOwing());
			s.setTransferredAmont(amountTran);
			} else {
				s.setCreditAmount(amountCredit);
				s.setOwing(receiver);
				s.setTransferredAmont(amountTran);
			}
			
			s.setBalance(amountSender);
		}

		return s;

	}

private void saveaccrepo(String receiver, String sendername, int amountTran){
	
	AccountTransaction accoun = new AccountTransaction();
	accoun.setSender( sendername);
	accoun.setReceiver(receiver);
	accoun.setTransferAmount(amountTran);
	accountRepository.save(accoun);
	
}
}
