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
import com.kdgz.uwifi.backoffice.model.Businessinfo;

/**
 * 店铺拦截器
 * @author allen
 *
 */
public class ShopInterceptor implements Interceptor{

	@Override
	public void intercept(ActionInvocation ai) {

		Integer shopid = (Integer) ai.getController()
				.getSessionAttr("shopid");

		if (shopid == null) {
			ai.getController().redirect("/businessinfo");
		} else {
			//设置店铺名称
			Businessinfo businessinfo = Businessinfo.dao.findById(shopid);
			String busname = businessinfo.getStr("busname");
			// 设置共通登录昵称
			String loginName = ai.getController().getSessionAttr("loginName");

			ai.getController().setAttr("busname", busname);
			ai.getController().setAttr("loginName", loginName);

			// 生成面包屑
			List<Breadcrumb> breadcrumbList = new ArrayList<Breadcrumb>();
			
			// 生成侧边栏
			List<Menu> menuList = new ArrayList<Menu>();

			String action = ai.getControllerKey().substring(1);
			String mark = ai.getController().getPara("mark");
			
			String currentUri = action;
			
			if(mark != null){
				currentUri = action+"?mark="+mark;
			}
			
			
			ai.getController().setAttr("action", action);
			ai.getController().setAttr("currentUri", currentUri);

			Document doc = CacheKit.get("propertyCache2", "menuDocument2",
					new IDataLoader() {

						@Override
						public Object load() {
							// 默认的解析器
							SAXBuilder builder = new SAXBuilder();
							Document menuDoc = null;
							try {
								menuDoc = builder.build(PathKit.getWebRootPath()
										+ File.separator + "WEB-INF"
										+ File.separator + "shopMenu.xml");
							} catch (JDOMException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

							CacheKit.put("propertyCache2", "menuDocument2", menuDoc);

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
						subMenuList.add(tempSubMenu);
						
					}
					tempMenu.setSubMenu(subMenuList);
				}
				menuList.add(tempMenu);
				
			}
			
			// 设置功能左侧边栏菜单
			ai.getController().setAttr("shopmenu", menuList);
			
			ai.invoke();
		}
	}
}
