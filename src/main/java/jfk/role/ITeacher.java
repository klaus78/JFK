package jfk.role;

public interface ITeacher { // extends IRole{

	public String getRoleName();
	
	// @RoleMap (target = Person.class, method = "talk") 
	public void teach(String what);
}
