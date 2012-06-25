package jfk.role;

public interface IRoleManager {

	/**
     * Add a role to the target class.
     * @param target the class receiving the new role
     * @param role the role to be added to the target
     * @return void 
     */
	public void addRole(Class target, IRole role);
	
	/**
     * Return an instance of the role applied to the target class.
     * Note that before you need to call addRole, otherwise you get
     * null because no role was added before to the target class.
     * @param target the class implementing the role
     * @param role the role implemented by the target
     * @return IRole an interface to the methods of the role.  
     */
    public IRole getAsRole(Object target, IRole role);
}
