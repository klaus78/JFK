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
		
		Person person = new Person();
		
		// create a role
		IRole teacherRole = new Teacher();
		
		// teacher role is added to the instance of Person
		roleManager.addRole(person.getClass(), teacherRole);
	
		// return the instance of Person implementing the teacher role 
		Teacher personToTeacher = (Teacher)roleManager.getAsRole(person, teacherRole);
		
		// now a person can also teach
		// note that here the method teacher.teach actually calls 
		// person.talk internally
		personToTeacher.teach("I am a person but I am teaching like a teacher");
		
		
		// note that if you create an instance of teacher, i.e. you use the
		// constructor, when you call teacher.teach then the method is executed
		// and 
		Teacher teacher = new Teacher();
		teacher.teach("I am only a teacher");
		
		
	}
}
