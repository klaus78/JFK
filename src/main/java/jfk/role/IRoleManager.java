package jfk.role;

/**
 * This interface is used to manage roles added to classes.
 *
 * @author Claudio Varini
 * @author Luca Ferrari - fluca1978 (at) gmail (dot) com
 */
public interface IRoleManager {

    /**
     * Add a role to the target class.
     * The role must be an instance of a role class that will be connected to
     * the type of the specified target class. For instance, if the <i>teacher</i>
     * role is attached to the <i>person</i> class each instance of
     * the person class will be connected to the teacher running instance.
     * 
     * @param target the class receiving the new role
     * @param role the role to be added to the target
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
