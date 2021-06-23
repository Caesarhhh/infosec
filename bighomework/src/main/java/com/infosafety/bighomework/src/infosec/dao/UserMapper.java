package com.infosafety.bighomework.src.infosec.dao;

import com.infosafety.bighomework.src.infosec.model.User;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

	public List<User> listUsers();
	public User getUser(@Param(value="id")String id);
	public User getUserbyName(@Param(value="name")String name);

}
