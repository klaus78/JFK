package jfk.role;

/**
 * An interface that provides basic information about a role.
 *
 * @author Claudio Varini
 * @author Luca Ferrari - fluca1978 (at) gmail (dot) com
 */
public interface IRole {

    /**
     * The following constants are used when building a role implementation.
     * Their value must respect the name and args of the getRoleNameMethodBody.
     *
     * TODO: use an annotation here!
     * 
     */
    public final String METHOD_NAME_GET_ROLE_NAME = "getRoleName";
    public final String METHOD_ARGS_GET_ROLE_NAME = "";

    /**
     * Provides the role name.
     *
     * \return a string that contains the role name
     * 
     */
    public String getRoleName();
}
