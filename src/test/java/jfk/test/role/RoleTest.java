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
	
		// create a role
		IRole teacherRole = new Teacher();
	
		// a data type class Person extended to teacher role 
		// is added to the roleManager
		roleManager.addRole(Person.class, teacherRole);
	
		
		// create a new instance of person with name Paul
		Person personPaul = new Person("Paul");
		
		// return the instance of Person Paul extended with the teacher role 
		Teacher personPaulToTeacher = 
			(Teacher)roleManager.getAsRole(personPaul, teacherRole);
		
		// now the Person Paul can also teach
		// note that here the method teacher.teach internally calls 
		// person.talk 
		personPaulToTeacher.teach("I am a person but I am teaching like a teacher");
		
	
		
		Person personMarc = new Person("Marc");
		// return the instance of Person Marc extended with the teacher role 
		Teacher personMarcToTeacher = 
			(Teacher)roleManager.getAsRole(personMarc, teacherRole);
		
		personMarcToTeacher.teach("I am Marc working as teacher");
		
		
		// note that if you create an instance of teacher, i.e. you use the
		// constructor, when you call teacher.teach then the method is executed
		// and no call to method person.talk happens
		Teacher teacher = new Teacher();
		teacher.teach("I am only a teacher");
		
		
	}
}
