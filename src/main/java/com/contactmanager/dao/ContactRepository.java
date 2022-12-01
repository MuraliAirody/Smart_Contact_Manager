package com.contactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactmanager.entities.Contact;

public interface ContactRepository  extends JpaRepository<Contact,Integer>{

	
	@Query(nativeQuery = true,value = "select * from contact where user_id=:id")
//	public List<Contact> getContactsByUserId(@Param("id")int userId);
	public Page<Contact> getContactsByUserId(@Param("id") int userId,Pageable pageable);

	
}
