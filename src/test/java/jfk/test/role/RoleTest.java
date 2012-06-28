package jfk.test.role;

import jfk.core.JFK;
import jfk.role.IRole;
import jfk.role.IRoleManager;
import jfk.role.impl.RoleManagerImpl;

import org.junit.Test;

public class RoleTest {

	@Test
    public void roleBuilder()
	{
		// get an instance of the role manager
		IRoleManager roleManager = JFK.getRoleManager();
		
		Person p = new Person();
		
		// create a role
		IRole t = new Teacher();
		
		// a role is added to the instance of Person
		roleManager.addRole(p.getClass(), t);
	
		// the instance of Person is converted to an instance of Teacher
		Teacher tTrasf = (Teacher)roleManager.getAsRole(p, t);
		
		// now a person can also teach
		tTrasf.teach("I am a person but I am teaching like a teacher");
	}
}
