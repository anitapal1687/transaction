package com.example.Transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.Transaction.domain.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long>{

	Client findByName(String username);
	
	  @Modifying
	    @Query("UPDATE Client c SET c.balance = :balance WHERE c.name = :name")
	    int topup(@Param("balance") int balance, @Param("name") String name);
	  
	  

}
