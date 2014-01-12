package net.juniper.jmp.monitor.services.impl;

import java.util.ArrayList;
import java.util.List;

import net.juniper.jmp.monitor.dao.NavGroupRepository;
import net.juniper.jmp.monitor.dao.NavItemRepository;
import net.juniper.jmp.monitor.jpa.NavGroupEntity;
import net.juniper.jmp.monitor.jpa.NavItemEntity;
import net.juniper.jmp.monitor.mo.home.NavGroupMO;
import net.juniper.jmp.monitor.mo.home.NavItemMO;
import net.juniper.jmp.monitor.mo.home.NavType;
import net.juniper.jmp.monitor.services.NavTypeService;
import net.juniper.jmp.utils.IMoEntityConvertor;
import net.juniper.jmp.utils.MoEntityConvertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
@Service(value="net.juniper.jmp.monitor.services.NavTypeService")
public class NavTypeServiceImpl implements NavTypeService{
    @Autowired
    private NavGroupRepository navGroupRepository;
    @Autowired
    private NavItemRepository navItemRepository;
    
    private IMoEntityConvertor<NavItemMO, NavItemEntity> itemConvertor;
    private IMoEntityConvertor<NavGroupMO, NavGroupEntity> groupConvertor;
    
    public List<NavGroupEntity> getNavGroups(){
    	Order order = new Order("id");
    	Sort sort = new Sort(order);
    	return (List<NavGroupEntity>) navGroupRepository.findAll(sort);
    }
    
    public List<NavItemEntity> getNavItems() {
    	Order order = new Order("id");
    	Sort sort = new Sort(order);
    	return (List<NavItemEntity>) navItemRepository.findAll(sort);
    }

	@Override
	public List<NavType> getNavList() {
		List<NavType> allList = new ArrayList<NavType>();
		List<NavGroupEntity> groupList = getNavGroups();
		List<NavItemEntity> itemList = getNavItems();
		List<NavGroupMO> groupMoList = convertGroupEntitys(groupList);
		List<NavItemMO> itemMoList = convertItemEntitys(itemList);
		allList.addAll(groupMoList);
		allList.addAll(itemMoList);
		return allList;
	}
	
	private List<NavGroupMO> convertGroupEntitys(List<NavGroupEntity> groups){
		return getGroupConvertor().convertFromEntity2Mo(groups, NavGroupMO.class);
	}
	
	private List<NavItemMO> convertItemEntitys(List<NavItemEntity> items){
		return getMoEntityConvertor().convertFromEntity2Mo(items, NavItemMO.class);
	}
	
	private IMoEntityConvertor<NavItemMO, NavItemEntity> getMoEntityConvertor(){
		if(itemConvertor == null){
			itemConvertor = new MoEntityConvertor<NavItemMO, NavItemEntity>(){
				@Override
				public List<NavItemEntity> convertFromMo2Entity(List<NavItemMO> moList, Class<NavItemEntity> clazz) {
					List<NavItemEntity> items = super.convertFromMo2Entity(moList, clazz);
					//further process
					return items;
				}
				@Override
				public List<NavItemMO> convertFromEntity2Mo(List<NavItemEntity> entityList, Class<NavItemMO> clazz) {
					List<NavItemMO> items = super.convertFromEntity2Mo(entityList, clazz);
					//further process
					return items;
				}
			};
		}
		return itemConvertor;
	}
	
	private IMoEntityConvertor<NavGroupMO, NavGroupEntity> getGroupConvertor(){
		if(groupConvertor == null){
			groupConvertor = new MoEntityConvertor<NavGroupMO, NavGroupEntity>(){
				@Override
				public List<NavGroupEntity> convertFromMo2Entity(List<NavGroupMO> moList, Class<NavGroupEntity> clazz) {
					List<NavGroupEntity> items = super.convertFromMo2Entity(moList, clazz);
					//further process
					return items;
				}
				@Override
				public List<NavGroupMO> convertFromEntity2Mo(List<NavGroupEntity> entityList, Class<NavGroupMO> clazz) {
					List<NavGroupMO> items = super.convertFromEntity2Mo(entityList, clazz);
					//further process
					return items;
				}
			};
		}
		return groupConvertor;
	}
}
