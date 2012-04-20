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
		
		IRole t = new Teacher();
		
		roleManager.addRole(p.getClass(), t);
	
		Teacher tTrasf = (Teacher)roleManager.getAsRole(p, t);
		
		tTrasf.teach("I am teaching like a teacher");
	}
}
