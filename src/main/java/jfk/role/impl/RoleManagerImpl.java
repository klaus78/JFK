package jfk.role.impl;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import jfk.role.IRole;
import jfk.role.IRoleManager;
import jfk.role.RoleMap;
import org.apache.log4j.*;
import org.apache.log4j.xml.DOMConfigurator;

public class RoleManagerImpl implements IRoleManager{


    /**
     * The logger for this class loader.
     */
    private static Logger logger = org.apache.log4j.Logger.getLogger( RoleManagerImpl.class );

    /**
     * A cache for already defined classes. Each function is mapped with a key
     * that corresponds to the class name the function will be bound to compound
     * by the name of the function annotation.
     */
    private final HashMap<Long, Class> cacheClasses = new HashMap<Long, Class>();
    

    public void addRole(Class target, IRole role)
    {
	// key for the hash table
	Long key = (long)target.hashCode() + (long)role.hashCode();
	
	// get the class pool for working with classes 
	// and modifying them on the fly
	final ClassPool pool = ClassPool.getDefault();
	    
	    	    
	// a name for the class to implement.
	// The name is composed by the concatenation of the target class simple name
	// and the role class name
	final String roleClassName = String.format( "%s%s",
						    target.getSimpleName(),
						    role.getClass().getSimpleName() ); 
	    
	    
	// create a new class for the specified name
	final CtClass newRoleCtClass = pool.makeClass( roleClassName );
	    
	try {
	    CtClass roleCtClass = pool.get(role.getClass().getCanonicalName());
	    newRoleCtClass.setSuperclass(roleCtClass);
	} catch (NotFoundException e1) {
	    logger.error( "Cannot get the role CtClass!", e1 );
	    return;
	}
	catch (CannotCompileException e1) {
	    logger.error( "Cannot compile exception!", e1 );
	    return;
	}
		
	    
	// get the list of the interfaces that the role class is implementing
	Class<?>[] interfaces = role.getClass().getInterfaces();
	    
		
	// create a CtClass for IRole
	final CtClass iRoleCtClass = pool.makeInterface(interfaces[0].getName());
	newRoleCtClass.addInterface(iRoleCtClass);
	    
	    
	// add a private target field to the role class
	final String targetDataType = target.getName();
	    
	    
	try {			
	    // generate a random name to allow the same class to implment
	    // multiple roles
	    String targetFieldName = String.format( "__target_%s", target.hashCode() );

	    String targetFieldSourceCode = String.format( "private %s %s;",
							  targetDataType,
							  targetFieldName
							  );
	    logger.debug( String.format( "[ROLE] Connector field [%s]", targetFieldSourceCode ) );
	    CtField targetField = CtField.make( targetFieldSourceCode, newRoleCtClass);
			
	    newRoleCtClass.addField(targetField);
			
	    // add a constructor 
	    String strConstructorSourceCode = String.format( "public %s( %s param ){ %s = param; }",
							     roleClassName,
							     targetDataType,
							     targetFieldName
							     );
			
	    final CtConstructor constructor =
		CtNewConstructor.make(strConstructorSourceCode, newRoleCtClass);
			
	    constructor.setModifiers(Modifier.PUBLIC);
		    

	    newRoleCtClass.addConstructor(constructor);

	    // TODO: what if the interfaces are empty?
	    Method[] interfaceMethods = interfaces[0].getDeclaredMethods();
		    
	    // implements the interface role	
	    Method[] roleMethods = role.getClass().getDeclaredMethods(); 
		    
	    for (int i = 0; i < roleMethods.length; i++) {
		for(int m=0; m<interfaceMethods.length; m++)
		    {	
			// TODO: 21.03.2012
			// Find a sensible way to compare two method headers


			if(roleMethods[i].getName() != 	interfaceMethods[m].getName()) 
			    continue;
		    		

			String currentDelegateMethodBodySource = getSourceCodeForDelegateMethod( interfaceMethods[ m ], "this", null );		    		
			CtMethod ctMet = CtNewMethod.make( currentDelegateMethodBodySource,
							   newRoleCtClass);

			newRoleCtClass.addMethod(ctMet);
		    		
		    }// for(int m=0; m<interfaceMethods.length; m++)
		    	
		    	
		// analyze the annotation of the method
		Annotation[] annotations = roleMethods[i].getAnnotations();
		    	
		// a role method must have an annotation
		if(annotations.length == 0)
		    {
			continue;
		    }
		for(int a =0; a<annotations.length; a++)
		    {
			if (annotations[a] instanceof RoleMap)
			    {   			
				// here we have a method with a recognized annotation	
				// The body of the method will be	
				// _target.method
		    			
				RoleMap roleMap = (RoleMap)annotations[a];
		    			
				if (roleMap.target().getName() != 
				    target.getName())
				    {
		    				
					//throw new IllegalArgumentException(
					//		"The target class is not compatible " +
					//		"with the target in the RoleMap annotation!" );
				    }
		    			
		    			
		    			

				    	    	
				String connectorMethodSourceCode = getSourceCodeForDelegateMethod( roleMethods[ i ], targetFieldName, roleMap.method() );
				    	
				CtMethod newMethodSgn = CtMethod.make(
								      connectorMethodSourceCode,
								      newRoleCtClass);
				    	
				    	
				newRoleCtClass.addMethod( newMethodSgn );
			    }
		    							
		    }
		    		
	    } // end for (int i = 0; i < roleMethods.length; i++)  
		    
			
	    Class finalClass = newRoleCtClass.toClass();
	    cacheClasses.put(key, finalClass);
		    
	} catch ( Exception e ) {
	    logger.error( "Cannot compile exception while making the role connection", e );
	    e.printStackTrace();
	    return;
	}
		
    }
	
	
    public IRole getAsRole(Object target, IRole role)
    {
		
	// key for the hash table
	Long key = (long)target.getClass().hashCode() + (long)role.hashCode();
	IRole classToReturn = null;
		
	try {
	    if( ! cacheClasses.containsKey( key ) )
		classToReturn = null;
	    else{
		Class classTest = cacheClasses.get(key);
			
		Constructor ctr = classTest.getDeclaredConstructor(target.getClass());
			
		classToReturn = (IRole) ctr.newInstance(target);
	    }
			
	}catch( Exception e ){
	    logger.error( "Exception while getting the instance as role", e );
	    role = null;
	}
	finally{
	    return role;
	}
    }

	

    /**
     * Utility method to get the source code of a delegating method.
     *
     * \param delegating the method to which the call will be delegated
     * \param referenceName the name of the variable to use as trampoline for the delegation
     * \param delegatedName the name of the delegated method or null if the method has the same name
     * of the delgating one
     * \return the source code of the method
     */
    private final String getSourceCodeForDelegateMethod( Method delegating, String referenceName, String delegatedName ){

	// if no delegated suppose the delegate has the same properties
	// of the delegating method
	if( delegatedName == null || delegatedName.isEmpty() )
	    delegatedName = delegating.getName();

	// what return type does this method have?
	Class currentReturnType   = delegating.getReturnType();
	Class[] currentParameters = delegating.getParameterTypes();
	Class[] currentExceptions = delegating.getExceptionTypes();

	// build a list of parameters
	StringBuffer paramsNameBuffer = new StringBuffer( 10 * currentParameters.length );
	StringBuffer paramsListBuffer = new StringBuffer( 10 * currentParameters.length );
	for( int z = 0; z < currentParameters.length; z++ ){
	    Class currentParameterType = currentParameters[ z ];
	    String currentParName = String.format( "delegateParameter%d", z );

	    paramsNameBuffer.append( String.format( "%s %s %s",
						    ( z > 0 ? "," : "" ),
						    currentParameterType.getName(),
						    currentParName )
				     );

	    paramsListBuffer.append( String.format( "%s %s", ( z > 0 ? "," : "" ), currentParName ) );
								    
	}


	StringBuffer exceptionBuffer = new StringBuffer( 10 * currentExceptions.length );
	for( int z = 0; z < currentExceptions.length; z++ )
	    exceptionBuffer.append( String.format( "%s %s", ( z > 0 ? "," : "" ), currentExceptions[ z ].getName() ) );



	return String.format( "%s %s %s( %s ) %s  { %s %s.%s( %s ); }",
			      Modifier.toString( delegating.getModifiers() ).replace( "abstract", "" ), // remove abstract modifier!
			      currentReturnType.getName().toString(),
			      delegating.getName(),
			      paramsNameBuffer.toString(),
			      ( currentExceptions.length > 0 ? String.format( "throws %s", exceptionBuffer.toString() ) :  "" ),
			      ("void".equals( currentReturnType.toString() ) ? "" : "return"),
			      referenceName,
			      delegatedName,
			      paramsListBuffer.toString()
			      );

    }

}