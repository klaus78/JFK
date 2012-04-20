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
		// CtClass da creare deve estendere la classe Teacher in 
		// quanto deve potere essere castata a Teacher
		IRole rGen = roleManager.getAsRole(p, t);
		System.out.println("new role " + rGen.getRoleName());
		//Teacher tTrasf = (Teacher)rGen;
		//tTrasf.teach("I am a teacher");
		//System.out.println(rGen.getRoleName() + " roleNew");
	}
}
