package jfk.test.role;

import jfk.role.IRole;
import jfk.role.RoleMap;

public class Teacher implements IRole{

	public String getRoleName() {
		return "Teacher role";
	}

	@RoleMap (target = Person.class, method = "talk")
	public void teach(String what) {
		System.out.println("Teacher teaching " + what);	
	}

}
