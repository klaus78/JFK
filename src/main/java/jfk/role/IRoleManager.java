package jfk.role;

public interface IRoleManager {

	/**
     * Add a role to the target class.
     * @param target the class receiving the new role
     * @param role the role to be added to the target
     * @return void 
     */
	public void addRole(Class target, IRole role);
	
	// public bool addRole(Class target, Interface irole)
    public IRole getAsRole(Class target, IRole role);
}
