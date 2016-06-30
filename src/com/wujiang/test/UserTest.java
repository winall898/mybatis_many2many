package com.wujiang.test;

import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.ibatis.io.Resources;     
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wujiang.domain.Group;
import com.wujiang.domain.User;
import com.wujiang.domain.UserGroupLink;



public class UserTest {

    private final static String IBATIS_CONFIG_XML  = "com/wujiang/configuration/sqlMapConfig.xml"; 
    private static SqlSession session;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd k:mm:ss");
          
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 Reader reader = Resources.getResourceAsReader(IBATIS_CONFIG_XML);  //读取ibatis配置文件
		 SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
		  session = sqlMapper.openSession(true);
		  session.commit(false); //将默认提交事务属性设置为否
	}
	
	
	//保存用户信息
	@Test
	public void saveUserTest(){
		User user = new User();
		user.setName("张三");
		user.setPassword("123456");
		session.insert("com.wujiang.domain.User.saveUser", user);
	}
   
	//获取用户信息
	@Test
	public void getUserTest(){
		User user = (User) session.selectOne("com.wujiang.domain.User.selectUser", 1L);
		System.out.println("用户名： "+user.getName());
		System.out.println("用户密码:  "+user.getPassword());
		System.out.println("用户创建日期："+sdf.format(user.getCreateTime()));
	}
   
	//获取用户和用户所在组信息
	@Test
	public void getUserAndGroupTest(){
		 User  user =  (User)session.selectOne("com.wujiang.domain.User.selectUserGroup", 1L);
 		 
		 System.out.println(user.getName() +"所属组信息:");
 		for(Group group : user.getGroup()){
 			System.out.println("	组名:"+group.getName()+"，组创建时间:"+sdf.format(group.getCreateTime())+",状态："+group.getState());
  		}
 		
		
	}
	
	//保存用户和用户所在组信息
    //当用户所在组不存在时，创建该组，并生成映射关系
	@Test
	public void saveUserAndGroupTest(){
     	User user = new User();
 	    user.setName("gudeng");
 		user.setPassword("wuchang");
 		session.insert("com.wujiang.domain.User.saveUser", user);
		
		Group groupImpl = (Group)session.selectOne("com.wujiang.domain.Group.getGroupByName","用户组4");//获取组实例
		UserGroupLink ugl = new UserGroupLink();//声明User和Group实体间映射关系实例
		
		//查询到的组实例为空时的逻辑处理
		if(groupImpl == null){
		   	 Group group = new Group();
		   	 group.setName("用户组4");
		   	session.insert("com.wujiang.domain.Group.saveGroup", group);//持久化创建好的组实例
		   	
		   	//设置映射关系实例相关的属性
 		   	ugl.setUser(user);
 		   	ugl.setGroup(group);
            session.insert("com.wujiang.domain.User.saveRelativity",ugl);//持久化映射关系实力
            	   	
		}else{
 		    ugl.setGroup(groupImpl);
 			ugl.setUser(user);
			session.insert("com.wujiangs.domain.User.saveRelativity",ugl);
		}
	}
	
	
	//删除组信息的同时，取消组内所有的成员与该组的关联关系
	@Test
	public void deleteGroupTest(){
		Group group = new Group();
		group.setId(3L);  //以组id作为查询条件
//		group.setName("用户组1"); //以组name作为查询条件
		Group groupImpl = (Group)session.selectOne("com.wujiang.domain.Group.selectGroupUser",group);//获取组实例
		
		//组实例存在时
	    if(groupImpl != null){
	    	
	    	List<User> users = groupImpl.getUser();
	    	
          //查看用户组1中是否存在用户
	    	if(users != null && users.size() > 0){
	    		
	    		//存在用户时，先删除组与用户的对应关系
	    		UserGroupLink ugl = new UserGroupLink();
	    		for(User user : users){
	    		   ugl.setUser(user);
	    		   ugl.setGroup(groupImpl);
	    		   session.delete("com.wujiang.domain.Group.deleteGroupUser",ugl );
	    		} 
	    	}
	    	//删除组信息
	    	session.delete("com.wujiang.domain.Group.deleteGroup", groupImpl);
	    	
	    }else{
	    	throw new RuntimeException("查询的组不存在!!");
	    }
		
		
	}
	
	
	//更新组状态，当组状态由可见状态变成不可见时，取消该组下的用户与该组的映射关系
	@Test
	public void updateGroupStateTest(){
		Group group = new Group();
//		group.setName("用户组2");
		group.setId(4L);
		Group groupImpl = (Group) session.selectOne("com.wujiang.domain.Group.selectGroupUser",group);
		
		if(groupImpl != null){
			int state = groupImpl.getState() == 1 ? 0 : 1;
			
 			
			//组状态由0变成1时，即由可见变为不可见
			if(state == 1){
				List<User> users = groupImpl.getUser();
           //查看用户组2中是否存在用户
		    	if(users != null && users.size() > 0){
		    		
		    		//存在用户时，删除组与用户的对应关系
		    		UserGroupLink ugl = new UserGroupLink();
		    		for(User user : users){
		    		   ugl.setUser(user);
		    		   ugl.setGroup(groupImpl);
		    		   session.delete("com.wujiang.domain.Group.deleteGroupUser",ugl );
		    		} 
		    	}
			} 
			
			//更新组状态
			groupImpl.setState(state);
		}
		else{
			throw new RuntimeException("查询的组不存在!!");
		}
		 
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	  if(session != null){
		 session.commit(); //提交事务
		 session.close(); //关闭连接
	  }
	} 
}
