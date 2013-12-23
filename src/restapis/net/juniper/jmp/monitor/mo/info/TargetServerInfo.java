package net.juniper.jmp.monitor.mo.info;

import java.io.Serializable;
/**
 * 
 * @author juntaod
 *
 */
public class TargetServerInfo implements Serializable {
	private static final long serialVersionUID = 1565337623763993206L;
	private Integer id;
	private String address;
	private String port;
	private boolean alive = false;
	private boolean nodeAlive = false;
	private int failCount = 0;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	public boolean isNodeAlive() {
		return nodeAlive;
	}
	public void setNodeAlive(boolean nodeAlive) {
		this.nodeAlive = nodeAlive;
	}
	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	
}
