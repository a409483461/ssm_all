package com.xuxin.service.Impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;



import com.xuxin.dao.ItemsMapper;
import com.xuxin.domain.Items;
import com.xuxin.service.ItemsService;
@Service
public class ItemsServiceImpl implements ItemsService{
	@Resource
	private ItemsMapper itemsMapper;
	@Override
	public List<Items> findAll() {
		List<Items> list = itemsMapper.findAll();

		return list;
	}
	@Override
	public void deleteByPrimaryKey(Integer id) {
		itemsMapper.deleteByPrimaryKey(id);
		
	}
	@Override
	public Items findById(Integer id) {
		Items item=itemsMapper.selectByPrimaryKey(id);
		return item;
	}
	@Override
	public void saveOrUpdate(Items items) {
		itemsMapper.updateByPrimaryKey(items);
	}
	
}
