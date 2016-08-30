package com.kdgz.uwifi.backoffice.interceptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.kdgz.uwifi.backoffice.bean.Breadcrumb;
import com.kdgz.uwifi.backoffice.bean.Menu;
import com.kdgz.uwifi.backoffice.bean.SubMenu;
import com.kdgz.uwifi.backoffice.model.Menuinfo;
import com.kdgz.uwifi.backoffice.model.Roleinfo;

/**
 * 面包屑及菜单拦截器
 * 
 * @author lanbo
 * 
 */
public class BreadcrumbAndMenuInterceptor implements Interceptor {

	@Override
	public void intercept(ActionInvocation ai) {

		// 设置共通登录昵称
		String loginName = ai.getController().getSessionAttr("loginName");
		Integer userid = ai.getController().getSessionAttr("userid");
		ai.getController().setAttr("loginName", loginName);
		
		String curUser = ai.getController().getSessionAttr("curUser");
		ai.getController().setAttr("curUser", curUser);
		
		String flage = ai.getController().getSessionAttr("pageJump");
		ai.getController().setAttr("pageJump", flage);
		
		//获取菜单
		List<Menuinfo> menuinfoList = getMenuinfo(userid);

		// 生成面包屑
		List<Breadcrumb> breadcrumbList = new ArrayList<Breadcrumb>();
		
		// 生成侧边栏
		List<Menu> menuList = new ArrayList<Menu>();

		String action = ai.getControllerKey().substring(1);
		String method = ai.getMethodName();

		String currentUri = action + "/" + method;
		
		ai.getController().setAttr("action", action);
		ai.getController().setAttr("currentUri", currentUri);

		Document doc = CacheKit.get("propertyCache", "menuDocument",
				new IDataLoader() {

					@Override
					public Object load() {
						// 默认的解析器
						SAXBuilder builder = new SAXBuilder();
						Document menuDoc = null;
						try {
							menuDoc = builder.build(PathKit.getWebRootPath()
									+ File.separator + "WEB-INF"
									+ File.separator + "menu.xml");
						} catch (JDOMException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						CacheKit.put("propertyCache", "menuDocument", menuDoc);

						return menuDoc;
					}
				});

		Element root = doc.getRootElement();

		List<Element> menus = root.getChildren();

		// 主页
		Breadcrumb rootBread = new Breadcrumb();
		rootBread.setTitle(root.getAttributeValue("title"));
		rootBread.setIcon(root.getAttributeValue("icon"));
		breadcrumbList.add(rootBread);

		for (Element menu : menus) {
			// 左侧边栏菜单
			Menu tempMenu = new Menu();
			tempMenu.setTitle(menu.getChildText("title"));
			tempMenu.setIcon(menu.getChildText("icon"));
			tempMenu.setAction(menu.getAttributeValue("action"));
			tempMenu.setLinks(menu.getChildText("links"));
			
			Element menuUri = menu.getChild("links");
			// 子菜单
			if (menuUri.getChildren().size() > 0) {
				List<Element> subMenus = menuUri.getChildren();
				List<SubMenu> subMenuList = new ArrayList<SubMenu>();
				for (Element subMenu : subMenus) {
					SubMenu tempSubMenu = new SubMenu();
					tempSubMenu.setTitle(subMenu.getChildText("title"));
					tempSubMenu.setIcon(subMenu.getChildText("icon"));
					tempSubMenu.setUri(subMenu.getChildText("uri"));
					if(menuinfoList != null && menuinfoList.size() > 0){
						for(int i = 0; i < menuinfoList.size(); i++){
							Menuinfo menuinfo = menuinfoList.get(i);
							if(menuinfo.getStr("link").equals(subMenu.getChildText("uri"))){
								subMenuList.add(tempSubMenu);
							}
						}
					}
					
				}
				tempMenu.setSubMenu(subMenuList);
			}
			menuList.add(tempMenu);
			
			if (menu.getAttributeValue("action").equals(action)) {
				// 主菜单
				Breadcrumb breadHome = new Breadcrumb();
				breadHome.setTitle(menu.getChildText("title"));
				breadHome.setIcon(menu.getChildText("icon"));
				breadcrumbList.add(breadHome);

				Element links = menu.getChild("links");
				// 子菜单
				if (links.getChildren().size() > 0) {

					List<Element> subMenus = links.getChildren();
					for (Element subMenu : subMenus) {
						if (subMenu.getChildText("uri").equals(currentUri)) {
							Breadcrumb subBread = new Breadcrumb();
							subBread.setTitle(subMenu.getChildText("title"));
							subBread.setIcon(subMenu.getChildText("icon"));
							breadcrumbList.add(subBread);
						} else if((currentUri.equals("myRouter/domainList") || currentUri.equals("myRouter/importBwlist")) && subMenu.getChildText("uri").equals("myRouter/bwList")) {
							Breadcrumb subBread = new Breadcrumb();
							subBread.setTitle("上网管理");
							subBread.setIcon("fa-caret-right");
							breadcrumbList.add(subBread);
						}
					}
				}
			}
		}
		// 设置面包屑
		ai.getController().setAttr("breadcrumb", breadcrumbList);
		
		// 设置功能左侧边栏菜单
		ai.getController().setAttr("menu", menuList);

		
		ai.invoke();
	}
	
	/**
	 * 获取菜单信息
	 */
	public List<Menuinfo> getMenuinfo(Integer userid){
		
		Roleinfo roleinfo = Roleinfo.dao.selectRoleinfoByUserId(userid);
		List<Menuinfo> menuinfo = null;
		if(roleinfo != null){
			 menuinfo = Menuinfo.dao.selectMenuinfoByRoleId(roleinfo.getInt("roleid"));
		}
		
		return menuinfo;
	}

}
