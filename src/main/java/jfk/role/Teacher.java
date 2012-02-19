package jfk.role;

public class Teacher implements ITeacher{

	@Override
	public String getRoleName() {
		return "Teacher role";
	}

	@Override
	// @RoleMap (target = Person.class, method = "talk")
	public void teach(String what) {
		System.out.println("Teacher teaching " + what);	
	}

}
