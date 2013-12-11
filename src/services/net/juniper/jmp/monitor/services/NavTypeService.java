package net.juniper.jmp.monitor.services;

import java.util.List;

import net.juniper.jmp.monitor.jpa.NavGroupEntity;
import net.juniper.jmp.monitor.jpa.NavItemEntity;
import net.juniper.jmp.monitor.mo.home.NavType;
public interface NavTypeService {
    public List<NavGroupEntity> getNavGroups();
    public List<NavItemEntity> getNavItems();
	public List<NavType> getNavList();
}
