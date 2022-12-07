package com.contactmanager.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;

public interface ContactRepository  extends JpaRepository<Contact,Integer>{

	
	@Query(nativeQuery = true,value = "select * from contact where user_id=:id")
//	public List<Contact> getContactsByUserId(@Param("id")int userId);
	public Page<Contact> getContactsByUserId(@Param("id") int userId,Pageable pageable);
	


	@Modifying
	@Transactional
	@Query("delete from Contact c where c.cid =:id")
    public void deleteContactById(@Param("id") int id);
	
	//search
//	@Query("select c from Contact c where c.name like %:name%  and c.user like %:user%")
//	public List<Contact> findByNameContainingAndUser(@Param("name") String name,@Param("user") User user);
	
	public List<Contact> findByNameContainingAndUser(String name,User user);


	
}
