package com.xuxin.Controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xuxin.domain.Items;
import com.xuxin.service.ItemsService;

@Controller
@RequestMapping("/items")
public class ItemsController {
	@Resource
	private ItemsService itemsService;
	
	@RequestMapping("list")
	public String list(Model model){
		
		List<Items> list = itemsService.findAll();
		
		model.addAttribute("itemsList", list);
		return "itemsList";
		
	}
	@RequestMapping("deleteById")
	public String deleteById(Integer id){
		
		itemsService.deleteByPrimaryKey(id);
		
		
		return "redirect:list.do";
		
	}
	@RequestMapping("edit")
	public String edit(Model model,Integer id){
		
		Items items=itemsService.findById(id);
		model.addAttribute("item", items);
		return "editItem";	
	}
	
	@RequestMapping("saveOrUpdate")
	public String saveOrUpdate(Items items){
		
		itemsService.saveOrUpdate(items);
		
		return "redirect:list.do";

		
	}
	
	@RequestMapping("deleteByIds")
	public String deleteByIds(Integer[] id){
		if(id!=null){
			for (Integer integer : id) {
				itemsService.deleteByPrimaryKey(integer);
			}
		}

		return "redirect:list.do";

		
	}

}
