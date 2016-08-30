package com.kdgz.uwifi.backoffice.controller;

import com.jfinal.core.Controller;

public class ActivityController extends Controller{

	public void index(){
		render("/admin/activity/list.html");
	}
}
