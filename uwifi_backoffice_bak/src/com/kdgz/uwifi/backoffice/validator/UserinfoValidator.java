package com.kdgz.uwifi.backoffice.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.kdgz.uwifi.backoffice.model.Userinfo;

public class UserinfoValidator extends Validator {

	public UserinfoValidator() {
		super();
		setShortCircuit(true);
	}

	@Override
	protected void handleError(Controller controller) {

		String actionKey = getActionKey();
		if (actionKey.equals("/account/update")) {
			String username = controller.getSessionAttr("loginName");
			controller.setAttr("userinfo",
					Userinfo.dao.getUserInfo(username));
			controller.render("/admin/user/account.html");
		}
	}

	@Override
	protected void validate(Controller controller) {
		validateRequiredString("userinfo.nickname", "errorMsg", "请输入昵称!");
		validateRequiredString("userinfo.password", "errorMsg", "请输入密码!");
		validateRequiredString("passwordConfirm", "errorMsg", "请输入密码确认内容!");
		validateEqualField("userinfo.password", "passwordConfirm",
				"errorMsg", "两次输入的密码不一致！");
	}

}
