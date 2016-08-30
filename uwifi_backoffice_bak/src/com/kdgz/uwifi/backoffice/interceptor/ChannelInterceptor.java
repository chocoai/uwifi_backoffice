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
import com.kdgz.uwifi.backoffice.constant.Constants;

/**
 * 店铺拦截器
 * @author allen
 *
 */
public class ChannelInterceptor implements Interceptor{

	@Override
	public void intercept(ActionInvocation ai) {
		
			String roleid = ai.getController().getSessionAttr("roleid");
			
			if(roleid == null){
				ai.getController().redirect("/site/index");
				return;
			}

			// 设置共通登录昵称
			String loginName = ai.getController().getSessionAttr("loginName");

			ai.getController().setAttr("loginName", loginName);

			// 生成面包屑
			List<Breadcrumb> breadcrumbList = new ArrayList<Breadcrumb>();
			
			// 生成侧边栏
			List<Menu> menuList = new ArrayList<Menu>();

			String action = ai.getControllerKey().substring(1);
			
			String currentUri = action;
			
			
			ai.getController().setAttr("action", action);
			ai.getController().setAttr("currentUri", currentUri);

			Document doc = CacheKit.get("propertyCache3", "menuDocument3",
					new IDataLoader() {

						@Override
						public Object load() {
							// 默认的解析器
							SAXBuilder builder = new SAXBuilder();
							Document menuDoc = null;
							try {
								menuDoc = builder.build(PathKit.getWebRootPath()
										+ File.separator + "WEB-INF"
										+ File.separator + "channelMenu.xml");
							} catch (JDOMException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

							CacheKit.put("propertyCache3", "menuDocument3", menuDoc);

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
				if(Constants.ROLE_SILVERCHANNEL.equals(roleid)){
					if(!"channelManager".equals(menu.getChildText("links"))){
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
								if(subMenu.getChildText("uri").equals("channelBusiness")){
									continue;
								}
								subMenuList.add(tempSubMenu);
								
							}
							tempMenu.setSubMenu(subMenuList);
						}
						menuList.add(tempMenu);
					}
				}else{
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
				
				
				
			}
			
			// 设置功能左侧边栏菜单
			ai.getController().setAttr("channelmenu", menuList);
			
			ai.invoke();
		}
}
