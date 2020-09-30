/*
 * 路由攻击数字大屏监控数据
 */
package edu.nuaa.nettop.common.response.ospf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoRouterAttackScreenStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8771539410857417074L;

	public BoRouterAttackScreenStatus() {
		// TODO Auto-generated constructor stub
	}
	
	private String wlid;
	
	private String victim;   //受害者路由器名称
	
	private List<BoVictimRouterItem> routers = new ArrayList<BoVictimRouterItem>();  //指定受害者路由器路由表信息
	
	private String attacker;  //攻击路由器名称
	
	//攻击路由器的攻击报文
	private List<BoOSPFAttackPack> attackpacks = new ArrayList<BoOSPFAttackPack>();
	
	//所有网络报文	
	private List<BoOSPFPackage> packs = new ArrayList<BoOSPFPackage>();

	public String getWlid() {
		return wlid;
	}

	public void setWlid(String wlid) {
		this.wlid = wlid;
	}

	
	public String getVictim() {
		return victim;
	}

	public void setVictim(String victim) {
		this.victim = victim;
	}

	public List<BoVictimRouterItem> getRouters() {
		return routers;
	}

	public void setRouters(List<BoVictimRouterItem> routers) {
		this.routers = routers;
	}

	public String getAttacker() {
		return attacker;
	}

	public void setAttacker(String attacker) {
		this.attacker = attacker;
	}

	public List<BoOSPFAttackPack> getAttackpacks() {
		return attackpacks;
	}

	public void setAttackpacks(List<BoOSPFAttackPack> attackpacks) {
		this.attackpacks = attackpacks;
	}

	public List<BoOSPFPackage> getPacks() {
		return packs;
	}

	public void setPacks(List<BoOSPFPackage> packs) {
		this.packs = packs;
	}

}
