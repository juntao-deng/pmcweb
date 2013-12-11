package net.juniper.jmp.tags.model;

import java.io.Serializable;

public class TagMO implements Serializable {
	private static final long serialVersionUID = 8324924401193518671L;
	private Integer id;
	private String tagname;
	private Integer pid;
	private boolean folder;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTagname() {
		return tagname;
	}
	public void setTagname(String tagname) {
		this.tagname = tagname;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public boolean isFolder() {
		return folder;
	}
	public void setFolder(boolean folder) {
		this.folder = folder;
	}
}
