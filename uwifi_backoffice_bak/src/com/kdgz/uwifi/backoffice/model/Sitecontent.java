package com.kdgz.uwifi.backoffice.model;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@SuppressWarnings("serial")
public class Sitecontent extends Model<Sitecontent>{

	public static final Sitecontent dao = new Sitecontent();
	
	
	public Page<Sitecontent> pageSitecontent(int pageNumber, int pageSize, String classids) {
		StringBuffer sql = new StringBuffer();
		sql.append("from sitecontent a, siteclassify b where a.classid = b.id");
		if(StrKit.notBlank(classids)){
			sql.append(" and a.classid in (" + classids + ") ");
		}else{
			sql.append(" and a.classid in ("+0+ ") ");
		}
		
		sql.append(" and a.delflag = 0 order by a.id desc");
		Page<Sitecontent> sPage = paginate(pageNumber, pageSize, "select a.id, a.classid, a.title, " +
				"a.info, from_unixtime(a.addtime) addtime, from_unixtime(a.updatetime) updatetime, a.sort, a.delflag , b.name",
				sql.toString());
		
		
		if(sPage.getTotalPage()>0 && pageNumber>sPage.getTotalPage()){
			sPage = paginate(1, pageSize, "select a.id, a.classid, a.title, " +
					"a.info, from_unixtime(a.addtime) addtime, from_unixtime(a.updatetime) updatetime, a.sort, a.delflag , b.name",
					sql.toString());
		}
		return sPage;
	}
	/**
	 * 查询分类列表信息
	 * @return
	 */
	public Sitecontent selectContent(Integer classid){
		
		Sitecontent sitecontent = dao.findFirst("select * from sitecontent where delflag = 0 and classid = "+classid);
		return sitecontent;
	}
	
	/**
	 * 获取文章内容obj
	 * @return
	 * @author jason
	 */
	public Record getSitecontent(String adid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from sitecontent where id=? and delflag=0 ");
		Record	sitecontent = Db.findFirst(sql.toString(), new Object[] { adid });
		return sitecontent;
	}

}
