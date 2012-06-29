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

public class RoleManagerImpl implements IRoleManager{

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
	    final String roleClassName = target.getSimpleName() + role.getClass().getSimpleName(); 
	    
	    
	    // create a new class for the specified name
	    final CtClass newRoleCtClass = pool.makeClass( roleClassName );
	    
		try {
			CtClass roleCtClass = pool.get(role.getClass().getCanonicalName());
			newRoleCtClass.setSuperclass(roleCtClass);
		} catch (NotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (CannotCompileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    
		Class<?>[] interfaces = role.getClass().getInterfaces();
	    
		
	    // create a CtClass for IRole
	    final CtClass iRoleCtClass = pool.makeInterface(interfaces[0].getName());
	    newRoleCtClass.addInterface(iRoleCtClass);
	    
	    
	    // add a private target field to the role class
	    final String targetDataType = target.getName();
	    
	    
	    try {
			CtField targetField = CtField.make("private " + targetDataType 
											 + " _target;", newRoleCtClass);
			
			newRoleCtClass.addField(targetField);
			
		    // add a constructor 
			String strConstructor = 
				"public " + roleClassName + "(" + targetDataType + " param)" +
				"{_target = param;}";
			
			final CtConstructor constructor =
				CtNewConstructor.make(strConstructor, newRoleCtClass);
			
			constructor.setModifiers(Modifier.PUBLIC);
		    
		    newRoleCtClass.addConstructor(constructor);
		    
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
		    		
		    		
				    String funBody = "public String getRoleName() " +
				    		"{ return \"Role implemented\"; }";
				    
				    CtMethod ctMet = CtNewMethod.make(
				    		funBody,
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
		    			
		    			
		    			
		    			// create the body of the method to add
		    			String methodSignature = "public " +
				    		roleMethods[i].getGenericReturnType().toString() + " ";
				    	
				    	// return value type
				    	String methodReturnType =
				    		roleMethods[i].getGenericReturnType().toString();
				    	
				    	methodReturnType = methodReturnType.trim();

				    	
				    	// method name
				    	methodSignature += roleMethods[i].getName();
				        
				    	String methodName = roleMethods[i].getName();
				    	    	
				    	Class<?>[] parameters = roleMethods[i].getParameterTypes();
				    	
				    	methodSignature += "(";
				    	methodName += "(";
				    	
				    	String stringParams = "(";
				    	
				        // method parameters 
				        for(int p=0; p<parameters.length; p++)
				        {
				        	String param = 
				        	parameters[p].getName() + " par" + p;
				        
				        	stringParams += "par" + p;
				        	if(p < (parameters.length - 1) )
				        	{
				        		param += ",";
				        		stringParams += ",";
				        	}
				        	methodSignature += param;
				        	methodName += param;
				        }
				        
				 
				    	methodSignature += ")";
				    	methodName += ")";
				    	stringParams += ")";
				    	
				    	
				    	// here is added the body of the method
				    	String newMethodBody = methodSignature + "\n{\n";
				    	
				    	
				    	if(methodReturnType.equals("void") != true) 
				    	{
				    		newMethodBody += " return";
				    	}
				   
				    	
				    	newMethodBody += " _target." + roleMap.method() + stringParams + ";" + "\n}";
				    	
				    	CtMethod newMethodSgn = CtMethod.make(
				    			newMethodBody + ";",
				    			newRoleCtClass);
				    	
				    	
				    	newRoleCtClass.addMethod(newMethodSgn);
		    		}
		    							
		    	}
		    		
		      } // end for (int i = 0; i < roleMethods.length; i++)  
		    
			
			Class finalClass = newRoleCtClass.toClass();
			cacheClasses.put(key, finalClass);
		    
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		    
		
	}
	
	
	public IRole getAsRole(Object target, IRole role)
	{
		// key for the hash table
		Long key = (long)target.getClass().hashCode() + (long)role.hashCode();
	
		if (cacheClasses.containsKey(key) == false)
		{
			return null;
		}
		
		try {
			Class classTest = cacheClasses.get(key);
			
			Constructor ctr = classTest.getDeclaredConstructor(target.getClass());
			
			IRole classToReturn = (IRole) ctr.newInstance(target);
			
			return classToReturn;
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return role;
	}

	
}