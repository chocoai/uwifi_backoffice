$(document).ready(function(){
	
	/**
     * 左边主导航折叠效果
     */
		$("#main-nav li ul").hide(); // Hide all sub menus
		$("#main-nav li a.current").parent().find("ul").slideToggle("slow"); // Slide down the current menu item's sub menu
		
		$("#main-nav li a.nav-top-item").click( // When a top menu item is clicked...
			function () {
				$(this).parent().siblings().find("ul").slideUp("normal"); // Slide up all sub menus except the one clicked
				$(this).next().slideToggle("normal"); // Slide down the clicked sub menu
				return false;
			}
		);
		
		$("#main-nav li a.no-submenu").click( // When a menu item with no sub menu is clicked...
			function () {
				window.location.href=(this.href); // Just open the link instead of a sub menu
				return false;
			}
		); 

		$("#main-nav li .nav-top-item").hover(
			function () {
				$(this).stop().animate({ paddingRight: "25px" }, 200);
			}, 
			function () {
				$(this).stop().animate({ paddingRight: "15px" });
			}
		);

    //Minimize Content Box
		
		$(".content-box-header h3").css({ "cursor":"s-resize" }); // Give the h3 in Content Box Header a different cursor
		$(".closed-box .content-box-content").hide(); // Hide the content of the header if it has the class "closed"
		$(".closed-box .content-box-tabs").hide(); // Hide the tabs in the header if it has the class "closed"
		
		$(".content-box-header h3").click( // When the h3 is clicked...
			function () {
			  $(this).parent().next().toggle(); // Toggle the Content Box
			  $(this).parent().parent().toggleClass("closed-box"); // Toggle the class "closed-box" on the content box
			  $(this).parent().find(".content-box-tabs").toggle(); // Toggle the tabs
			}
		);

    // Content box tabs:
		
		$('.content-box .content-box-content div.tab-content').hide(); // Hide the content divs
		$('ul.content-box-tabs li a.default-tab').addClass('current'); // Add the class "current" to the default tab
		$('.content-box-content div.default-tab').show(); // Show the div with class "default-tab"
		
		$('.content-box ul.content-box-tabs li a').click( // When a tab is clicked...
			function() { 
				$(this).parent().siblings().find("a").removeClass('current'); // Remove "current" class from all tabs
				$(this).addClass('current'); // Add class "current" to clicked tab
				var currentTab = $(this).attr('href'); // Set variable "currentTab" to the value of href of clicked tab
				$(currentTab).siblings().hide(); // Hide all content divs
				$(currentTab).show(); // Show the content div with the id equal to the id of clicked tab
				return false; 
			}
		);

    //Close button:
		
		$(".close").click(
			function () {
				$(this).parent().fadeTo(400, 0, function () { // Links with the class "close" will close parent
					$(this).slideUp(400);
				});
				return false;
			}
		);

		/**
		 * 隔行换色
		 */
		$('table.list tbody tr:even').addClass("alt-row"); 

		/**
		 * 全选
		 */
		$('.check-all').click(
			function(){
				$(this).parent().parent().parent().parent().find("input[type='checkbox']").attr('checked', $(this).is(':checked'));   
			}
		);

		/**
	     * 覆盖ARTDIALOG默认配置
	     */
	    $.dialog.defaults.title = 'Message';
	    $.dialog.defaults.okVal = '确定';
	    $.dialog.defaults.cancelVal = '取消';
});
  
/**
 * 弹出模态窗口
 */
function openPopWindow(url, title, width, height, refresh)
{
    var url = $.trim(url);
    var title = $.trim(title) ? $.trim(title) : 'Information';
    var width = parseInt(width) > 0 ? parseInt(width) : 900;
    var height = parseInt(height) > 0 ? parseInt(height) : 600;
    var refresh = parseInt(refresh) ? parseInt(refresh) : 0;

    if (!url)
    {
        return;
    }

    var option = {
        'title': title,
        'width': width,
        'height': height,
        'lock': true,
        'fixed': true,
        'background': '#000',
        'opacity': .2,
        'close': function()
        {
            if (refresh == 1)
            {
                window.location.reload();
            }
        }
    };

    $.dialog.open(url, option);

}

/**
 * 模拟ALERT函数
 * @param msg
 */
function doAlert(msg)
{
    $.dialog({
        'title': 'Alert',
        'content': msg,
        'icon': 'warning',
        'ok': true,
        'lock': true,
        'fixed': true,
        'opacity': .6
    });
}

/**
 * 模拟CONFIRM函数
 */
function doConfirm(msg, operationOk, operationCancel)
{
    var config = {
        'title': '确认',
        'content': msg,
        'icon': 'question',
        'ok': true,
        'cancel': typeof(operationCancel) == 'function' ? operationCancel : true,
        'lock': true,
        'fixed': true,
        'opacity': .6
    };

    if (typeof(operationOk) === 'function')
    {
        config.ok = operationOk;
    }
    else if (typeof(operationOk) === 'string')
    {
        config.ok = function()
        {
            window.location.href = operationOk;
        };
    }

    $.dialog(config);

    return false;
}

/**
 * 图片上传
 */
function imageUploadHandler(id, type, picture)
{
    var originObject = $('#' + id + '');
    var loading = originObject.parent().next();
    var preview = originObject.parent().parent().next();
    var topParent = originObject.parent().parent().parent();

    var uploader = new qq.FileUploader({
        element: originObject[0],
        action: IMAGE_UPLOAD_TARGE,
        allowedExtensions: ['jpg', 'jpeg', 'png', 'gif', 'bmp'],
        params: {
            type: type
        },
        onSubmit: function(id, fileName)
        {
            loading.css('display', 'block');

            return true;
        },
        onComplete: function(id, fileName, responseJSON)
        {
            loading.hide();

            if (responseJSON.error != undefined)
            {
                return false;
            }

            originObject.parent().parent().hide();

            var imageHtml = '<img src="' + responseJSON.preview + '" /><a href="">' + delete_str + '</a>';

            preview.append(imageHtml).show();
            preview.find('a').bind('click', imageUploadDelete);

            topParent.find('input.picture_hidden').val(responseJSON.preview);
            topParent.find('input.logo_type').val('2');

            return true;
        },
        showMessage: function(message)
        {
            doAlert(message);
        }
    });

    if (picture != undefined && picture)
    {
        topParent.find('.upload_image_tool').hide();

        var html = '<img src="' + getUploadPictureUrl(picture, 160) + '" /><a href="" onclick="imageUploadHide(this); return false;">' + delete_str + '</a>';

        topParent.find('.upload_image_preview').html(html).show();
        topParent.find('.picture_hidden').val(picture);
    }

    return uploader;
}
  
  