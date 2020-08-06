package com.example.Transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Transaction.domain.AccountTransaction;
import com.example.Transaction.domain.Client;
import com.example.Transaction.domain.Summary;

@Repository
public interface AccountRepository extends CrudRepository<AccountTransaction, Long>{
	

	    @Query(value="select nvl(sum(c.transfer_Amount),0) as creditAmount, c.receiver as owing from Account_Transaction c WHERE c.sender = :name group by c.receiver ", nativeQuery=true)
	    List<SummaryResponse> sumAmount( @Param("name") String name);
	    
	    @Query(value="select nvl(c.transfer_Amount,0)  from Account_Transaction c WHERE c.sender = :sender and c.receiver= :receiver", nativeQuery=true)
	    Integer getCreditAmount(@Param("sender") String sender, @Param("receiver") String receiver);
	    
	    
	    @Query(value="select nvl(c.transfer_Amount,0)  from Account_Transaction c WHERE c.sender = :receiver and c.receiver= :sender", nativeQuery=true)
	    Integer getDebitAmount(@Param("sender") String sender, @Param("receiver") String receiver);
	    
	    @Modifying
	    @Query(value="UPDATE Account_Transaction c SET c.transfer_Amount = :transferAmount  WHERE c.sender = :sender and c.receiver= :receiver", nativeQuery=true)
	    int balanceAmount(@Param("transferAmount") int transferAmount,  @Param("receiver") String receiver, @Param("sender") String sender);
	  
	  

}
