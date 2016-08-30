package com.kdgz.uwifi.backoffice.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单
 * 
 * @author lanbo
 * 
 */
public class Menu {

	private String title;

	private String icon;

	private String action;

	private String links;

	private List<SubMenu> subMenu = new ArrayList<SubMenu>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
	}

	public List<SubMenu> getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(List<SubMenu> subMenu) {
		this.subMenu = subMenu;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
