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
		
		IRole t = new Teacher();
		
		roleManager.addRole(classPerson, t);
		
		// IRole pRole = roleManager.getAsRole(classPerson, t);
	}
}
