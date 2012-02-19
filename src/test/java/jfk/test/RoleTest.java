package jfk.test;

import javassist.compiler.MemberResolver.Method;
import jfk.role.*;
import jfk.role.impl.*;

import org.junit.Test;

public class RoleTest {

	
	@Test
    public void roleBuilder()
	{
		
		RoleManagerImpl roleManager = new RoleManagerImpl();
		
		Person p = new Person();
		Class classPerson = p.getClass(); 
		
		ITeacher t = new Teacher();
		
		/*
		Class objRole = roleManager.addRoleOld(classPerson, t);
		try {
			ITeacher pRoleT = (ITeacher)objRole.newInstance();
			pRoleT.teach("I am a role capito?");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		roleManager.addRole(classPerson, ITeacher.class);
		
	}
}
