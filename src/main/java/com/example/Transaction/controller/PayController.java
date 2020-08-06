package com.example.Transaction.controller;

import java.sql.SQLException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.Transaction.domain.Client;
import com.example.Transaction.domain.Summary;
import com.example.Transaction.repository.ClientRepository;
import com.example.Transaction.repository.SummaryResponse;
import com.example.Transaction.service.ClientService;

@RestController
@RequestMapping(value = "/transaction")
public class PayController {

	@Autowired
	ClientService service;

	@RequestMapping( value = "/login/{name}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.CREATED)
	public List<SummaryResponse> createNewClient(@PathVariable("name")  String name) {

		Client c= service.loadUserByClient(name);
		String s= "Hello "+c.getName()+"\nYour Balance is "+ c.getBalance();
		List<SummaryResponse>  summayList = service.getCreditDebitAmmount(name);
		
			for(SummaryResponse resp : summayList){
				if(resp.getCreditAmount()<0)
				{
					s=s.concat("\nOwing "+Math.abs(resp.getCreditAmount())+" to "+ resp.getOwing());
				} else if(resp.getCreditAmount()>0) {
					s=s.concat("\nOwing "+Math.abs(resp.getCreditAmount())+" from "+ resp.getOwing());
				}
			}
		
		return summayList;
	}
	
	@RequestMapping( value = "/{name}/topup {amount}", method = RequestMethod.GET)
	public String  topup(@PathVariable("name")  String name, @PathVariable("amount") int amount) throws LoginException {
		String s="";
		
		Summary bal= service.topup(name, amount);
		  s= "Your Balance is "+ bal.getBalance();
		 if(bal.getTransferredAmont()!=0){
			  s=s.concat("\nAmount Transferred "+ bal.getTransferredAmont() +" to "+bal.getOwing());
		 }
		       
			List<SummaryResponse>  summayList = service.getCreditDebitAmmount(name);
			
				for(SummaryResponse resp : summayList){
					if(resp.getCreditAmount()<0)
					{
						s=s.concat("\nOwing "+Math.abs(resp.getCreditAmount())+" to "+ resp.getOwing());
					} else if(resp.getCreditAmount()>0){
						s=s.concat("\nOwing "+Math.abs(resp.getCreditAmount())+" from "+ resp.getOwing());
					}
				}

				
			
		return s;
	}
	
	@RequestMapping( value = "/{name}/pay {receiver} {amount}", method = RequestMethod.GET)
	public Summary pay(@PathVariable("name")  String name, @PathVariable("amount") int amount, @PathVariable("receiver") String receiver) {
	
		Summary s = service.pay(name, amount, receiver);
		String str= "Your Balance is "+ s.getBalance();
		if(s.getCreditAmount()!=0)
		{
			str=str.concat("\nOwing "+Math.abs(s.getCreditAmount())+" to "+ s.getOwing());
		}
		else if(s.getDebitAmount()!=0)
		{
			str=str.concat("\nOwing "+Math.abs(s.getDebitAmount())+" from "+ s.getOwing());
		}
		if(s.getTransferredAmont()!=0){
		str= str.concat("\nAmount Transferred "+s.getTransferredAmont()+" to "+ s.getOwing());
		}
		return s;
	}
	
	
	  @ExceptionHandler({ LoginException.class })
	    public String handleException(final Exception ex) {
	        final String error = "User is not Logged in";
	        return error;
	      
	    }
}
