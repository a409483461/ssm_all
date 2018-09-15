package com.xuxin.service;

import java.util.List;

import com.xuxin.domain.Items;

public interface ItemsService {
	List<Items> findAll();



	void deleteByPrimaryKey(Integer id);

	Items findById(Integer id);



	void saveOrUpdate(Items items);
}
