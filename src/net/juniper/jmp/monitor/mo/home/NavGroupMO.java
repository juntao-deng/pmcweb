package net.juniper.jmp.monitor.mo.home;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "navgroup")  
@XmlType(propOrder = {"id", "name", "icon", "url"})  
@XmlAccessorType(XmlAccessType.PROPERTY)  
public class NavGroupMO extends NavType {
	@Id
	private String id;
	@NotNull
	private String code;
	@NotNull
	private String name;
	@NotNull
	private String icon;
	@NotNull
	private String url;
	
	private String collapse;

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCollapse() {
		return collapse;
	}
	public void setCollapse(String collapse) {
		this.collapse = collapse;
	}
}
