package net.juniper.jmp.monitor.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table (name="wtf_navgroup")
public class NavGroupEntity{
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
