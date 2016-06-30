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
		 Reader reader = Resources.getResourceAsReader(IBATIS_CONFIG_XML);  //��ȡibatis�����ļ�
		 SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
		  session = sqlMapper.openSession(true);
		  session.commit(false); //��Ĭ���ύ������������Ϊ��
	}
	
	
	//�����û���Ϣ
	@Test
	public void saveUserTest(){
		User user = new User();
		user.setName("����");
		user.setPassword("123456");
		session.insert("com.wujiang.domain.User.saveUser", user);
	}
   
	//��ȡ�û���Ϣ
	@Test
	public void getUserTest(){
		User user = (User) session.selectOne("com.wujiang.domain.User.selectUser", 1L);
		System.out.println("�û����� "+user.getName());
		System.out.println("�û�����:  "+user.getPassword());
		System.out.println("�û��������ڣ�"+sdf.format(user.getCreateTime()));
	}
   
	//��ȡ�û����û���������Ϣ
	@Test
	public void getUserAndGroupTest(){
		 User  user =  (User)session.selectOne("com.wujiang.domain.User.selectUserGroup", 1L);
 		 
		 System.out.println(user.getName() +"��������Ϣ:");
 		for(Group group : user.getGroup()){
 			System.out.println("	����:"+group.getName()+"���鴴��ʱ��:"+sdf.format(group.getCreateTime())+",״̬��"+group.getState());
  		}
 		
		
	}
	
	//�����û����û���������Ϣ
    //���û������鲻����ʱ���������飬������ӳ���ϵ
	@Test
	public void saveUserAndGroupTest(){
     	User user = new User();
 	    user.setName("gudeng");
 		user.setPassword("wuchang");
 		session.insert("com.wujiang.domain.User.saveUser", user);
		
		Group groupImpl = (Group)session.selectOne("com.wujiang.domain.Group.getGroupByName","�û���4");//��ȡ��ʵ��
		UserGroupLink ugl = new UserGroupLink();//����User��Groupʵ���ӳ���ϵʵ��
		
		//��ѯ������ʵ��Ϊ��ʱ���߼�����
		if(groupImpl == null){
		   	 Group group = new Group();
		   	 group.setName("�û���4");
		   	session.insert("com.wujiang.domain.Group.saveGroup", group);//�־û������õ���ʵ��
		   	
		   	//����ӳ���ϵʵ����ص�����
 		   	ugl.setUser(user);
 		   	ugl.setGroup(group);
            session.insert("com.wujiang.domain.User.saveRelativity",ugl);//�־û�ӳ���ϵʵ��
            	   	
		}else{
 		    ugl.setGroup(groupImpl);
 			ugl.setUser(user);
			session.insert("com.wujiangs.domain.User.saveRelativity",ugl);
		}
	}
	
	
	//ɾ������Ϣ��ͬʱ��ȡ���������еĳ�Ա�����Ĺ�����ϵ
	@Test
	public void deleteGroupTest(){
		Group group = new Group();
		group.setId(3L);  //����id��Ϊ��ѯ����
//		group.setName("�û���1"); //����name��Ϊ��ѯ����
		Group groupImpl = (Group)session.selectOne("com.wujiang.domain.Group.selectGroupUser",group);//��ȡ��ʵ��
		
		//��ʵ������ʱ
	    if(groupImpl != null){
	    	
	    	List<User> users = groupImpl.getUser();
	    	
          //�鿴�û���1���Ƿ�����û�
	    	if(users != null && users.size() > 0){
	    		
	    		//�����û�ʱ����ɾ�������û��Ķ�Ӧ��ϵ
	    		UserGroupLink ugl = new UserGroupLink();
	    		for(User user : users){
	    		   ugl.setUser(user);
	    		   ugl.setGroup(groupImpl);
	    		   session.delete("com.wujiang.domain.Group.deleteGroupUser",ugl );
	    		} 
	    	}
	    	//ɾ������Ϣ
	    	session.delete("com.wujiang.domain.Group.deleteGroup", groupImpl);
	    	
	    }else{
	    	throw new RuntimeException("��ѯ���鲻����!!");
	    }
		
		
	}
	
	
	//������״̬������״̬�ɿɼ�״̬��ɲ��ɼ�ʱ��ȡ�������µ��û�������ӳ���ϵ
	@Test
	public void updateGroupStateTest(){
		Group group = new Group();
//		group.setName("�û���2");
		group.setId(4L);
		Group groupImpl = (Group) session.selectOne("com.wujiang.domain.Group.selectGroupUser",group);
		
		if(groupImpl != null){
			int state = groupImpl.getState() == 1 ? 0 : 1;
			
 			
			//��״̬��0���1ʱ�����ɿɼ���Ϊ���ɼ�
			if(state == 1){
				List<User> users = groupImpl.getUser();
           //�鿴�û���2���Ƿ�����û�
		    	if(users != null && users.size() > 0){
		    		
		    		//�����û�ʱ��ɾ�������û��Ķ�Ӧ��ϵ
		    		UserGroupLink ugl = new UserGroupLink();
		    		for(User user : users){
		    		   ugl.setUser(user);
		    		   ugl.setGroup(groupImpl);
		    		   session.delete("com.wujiang.domain.Group.deleteGroupUser",ugl );
		    		} 
		    	}
			} 
			
			//������״̬
			groupImpl.setState(state);
		}
		else{
			throw new RuntimeException("��ѯ���鲻����!!");
		}
		 
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	  if(session != null){
		 session.commit(); //�ύ����
		 session.close(); //�ر�����
	  }
	} 
}
