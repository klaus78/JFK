package jfk.role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to mark a method as a "slot" to connect to a delegate.
 * 
 * @author Claudio Varini - varini.claudio (at) gmail.com
 *
 */
//the annotation must be present at run-time!
@Retention( RetentionPolicy.RUNTIME )
// the annotation can be applied to a method!
@Target( ElementType.METHOD ) 
@Inherited
public @interface RoleMap {

	/**
     * The name of the class that can be extented by a role.
     * @return the class 
     */
	public Class target(); 
	
    /**
     * The name of the method to be overwritten when the role is active.
     * @return the name of the method to be overwritten
     */
    public String method() default "";
}
