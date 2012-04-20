package jfk.role.impl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

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
import jfk.function.IFunction;
import jfk.function.classloaders.ClassLoaderStatus;
import jfk.function.classloaders.ClassLoaderUtils;
import jfk.function.classloaders.FunctionClassLoader;
import jfk.role.IRole;
import jfk.role.IRoleManager;
import jfk.role.ITeacher;
import jfk.role.Person;
import jfk.role.RoleMap;
import jfk.role.Teacher;

public class RoleManagerImpl implements IRoleManager{

    /**
     * A cache for already defined classes. Each function is mapped with a key
     * that corresponds to the class name the function will be bound to compound
     * by the name of the function annotation.
     */
    private final HashMap<Long, Class> allClasses = new HashMap<Long, Class>();

	
	// protected static Logger logger = org.apache.log4j.Logger.getLogger( RoleManagerImpl.class );
	// configure the logger
	/*
	static{
    	DOMConfigurator.configure("jfk.log4j.xml");
    }
    */
	/**
     * The target on which the method will be called when the function is executed.
     */
    protected Object targetInstance = null;
    

	
	public Class addRoleOld(Class target, Teacher role)
	{
		if(target == null || role == null)
			return null;
		
		// here should I create on the fly a class that implements ITeacher.
		// (?)
		
		// class tmpClass implements ITeacher
		// {
		  
		// }
		
		try {
			
			
			// get the class pool for working with classes and modifying them on the fly
		    final ClassPool pool = ClassPool.getDefault();
		    
		    // the array that will store the in-memory byte code
		    byte[] bytecode = null;
		    
		    // a name for the class to implement.
		    final String roleClassName = target.getName() + " " + role.getRoleName(); //ClassLoaderUtils.computeFunctionClassName( functionNameFromAnnotation, targetInstance.getClass() );
		    
		    // create a new class for the specified name
		    final CtClass newRoleCtClass = pool.makeClass( roleClassName );
		    
		    // extract ITeacher
		    Class<?> interfaces[] = role.getClass().getInterfaces();
		    
		    // create a CtClass for ITeacher
		    final CtClass iRoleCtClass = pool.makeInterface(interfaces[0].getName());
		   

		    // add a private target field to the role class
		    String targetDataType = target.getName();
		    
		    System.out.println("targetDataType = " + targetDataType);
		    
		    CtField targetField = CtField.make("private " + targetDataType + " _target;", newRoleCtClass);		    
		    
		    
		    newRoleCtClass.addField(targetField);
		    
		    
		    // add a default constructor
		    final CtConstructor constructor = new CtConstructor(null, newRoleCtClass );
		    constructor.setBody("{_target = new jfk.role.Person();}");
		    newRoleCtClass.addConstructor(constructor);
		    
		    
		    
		    // implements the interface role	
		    Method[] roleMethods = role.getClass().getMethods(); //role.getClass().getMethods();
		    
		    
		    // I would like to extract the role methods, construct
		    for (int i = 0; i < roleMethods.length; i++) {
		        // construct a string describing the method
		    	
		    	// return value type
		    	String methodSignature = "public " +
		    	roleMethods[i].getGenericReturnType().toString() + " ";
		    	
		    	String methodReturnType =
		    		roleMethods[i].getGenericReturnType().toString();
		    	
		    	methodReturnType = methodReturnType.trim();
		    	
		    	// method name
		    	methodSignature += roleMethods[i].getName();
		        
		    	String methodName = roleMethods[i].getName();
		    	
		    	
		    	// exclude the methods that each java class has
		    	Object o = new Object();
		    	Method[] objMethods = o.getClass().getMethods();
		    	Boolean skip = false;
		    	for(int m=0; m<objMethods.length; m++)
		    	{
		    		if (roleMethods[i].hashCode() == objMethods[m].hashCode())
		    			skip = true;
		    	}
		    	
		    	if(skip == true)
		    		continue;
		    	
		    	
		    	Class<?>[] parameters = roleMethods[i].getParameterTypes();
		    	
		    	
		    	methodSignature += "(";
		    	methodName += "(";
		    	
		    	String stringParams = "(";
		    	
		        // method parameters 
		        for(int p=0; p<parameters.length; p++)
		        {
		        	// param = roleMethods[i].getParameterTypes()
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
		        	// ......
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
		   
		    	newMethodBody += " _target." + roleMethods[i].getName() + stringParams + ";" + "\n}";
		    	
		    	
		    	
		    	System.out.println("method to add to interface: " + methodSignature);
		    	
		    	CtMethod newMethodSgn = CtMethod.make(
		    			methodSignature + ";",
		    			iRoleCtClass);
		    	
		    	iRoleCtClass.addMethod(newMethodSgn);
		      } 
		    
		    
		    
		    // ctclass must implement iTeacher
		    newRoleCtClass.addInterface(iRoleCtClass);
		 		    
		    
		    CtClass interfaceDumb = pool.makeInterface("interfaceDumb");
		    
		    CtMethod[] intMethods = iRoleCtClass.getMethods();
		    for(int h=0; h<intMethods.length; h++)
		    {
		    	CtMethod[] objMethods = interfaceDumb.getMethods();
		    	Boolean skip = false;
		    	for(int m=0; m<objMethods.length; m++)
		    	{
		    		if (intMethods[h].getName().equals(objMethods[m].getName()))
		    			skip = true;
		    	}
		    	
		    	if(skip == true)
		    	{
		    		System.out.println("new method to remove = " +intMethods[h].getName());
		    		continue;
		    	}
		    	
		    	
		    	
		    	// Then try to add the CtMethods directly to newRoleCtClass
		    	System.out.println("new method to add = " +intMethods[h].getName());
		    
		    }
		    
		    
		    
		    // I would like to extract the role methods, construct
		    for (int i = 0; i < roleMethods.length; i++) {
		        // construct a string describing the method
		    	
		    	// return value type
		    	String methodSignature = "public " +
		    	roleMethods[i].getGenericReturnType().toString() + " ";
		    	
		    	String methodReturnType =
		    		roleMethods[i].getGenericReturnType().toString();
		    	
		    	methodReturnType = methodReturnType.trim();
		    	
		    	// method name
		    	methodSignature += roleMethods[i].getName();
		        
		    	String methodName = roleMethods[i].getName();
		    	
		    	
		    	// exclude the methods that each java class has
		    	Object o = new Object();
		    	Method[] objMethods = o.getClass().getMethods();
		    	Boolean skip = false;
		    	for(int m=0; m<objMethods.length; m++)
		    	{
		    		if (roleMethods[i].hashCode() == objMethods[m].hashCode())
		    			skip = true;
		    	}
		    	
		    	if(skip == true)
		    		continue;
		    	
		    	
		    	Class<?>[] parameters = roleMethods[i].getParameterTypes();
		    	
		    	
		    	methodSignature += "(";
		    	methodName += "(";
		    	
		    	String stringParams = "(";
		    	
		        // method parameters 
		        for(int p=0; p<parameters.length; p++)
		        {
		        	// param = roleMethods[i].getParameterTypes()
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
		        	// ......
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
		   
		    	newMethodBody += " _target." + roleMethods[i].getName() + stringParams + ";" + "\n}";
		    	
		    	System.out.println("method to add to class = " + newMethodBody);

		    	
		    	CtMethod newMethod = CtMethod.make(
		    			newMethodBody, 
		    			newRoleCtClass);
		        
		    	newRoleCtClass.addMethod(newMethod);
		      } 
		    		    
		    // ctclass must implement iTeacher
		    newRoleCtClass.addInterface(iRoleCtClass);
		 		    
		    
		    
		    return newRoleCtClass.toClass(); 
		    
		}
		
		/*
		catch (final NotFoundException e) 
		{
		    // logger.error("Exception caught while defining a class",e);
			// System.out.println("Exception caught while defining a class");
		    //throw new ClassNotFoundException("Cannot find class", e );
		}
		*/
		/*
		catch (final IOException e) 
		{
		    throw new ClassNotFoundException("Cannot find class", e );
		}
		*/ 
		catch (final CannotCompileException e) 
		{
		    // logger.error("Cannot compile exception caught while definining a IFunction class ", e);
		    System.out.println("Cannot compile exception caught while definining a IRole class " + e);
		    // throw new ClassNotFoundException("Cannot find class", e );
		} 
		//catch (InstantiationException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		finally{
		    // the class loader is ready now
		    synchronized( this ){
			// status = ClassLoaderStatus.READY;
		    }
		}
		
		
		return null;
	}
	

	public void addRole(Class target, IRole role)
	{
		// TODO: check the input parameters
		
		// key for the hash table
		Long key = (long)target.hashCode() + (long)role.hashCode();
	
		// get the class pool for working with classes 
		// and modifying them on the fly
	    final ClassPool pool = ClassPool.getDefault();
	    
	    	    
	    // a name for the class to implement.
	    final String roleClassName = target.getSimpleName() + role.getClass().getSimpleName(); 
	    
	    
	    // create a new class for the specified name
	    final CtClass newRoleCtClass = pool.makeClass( roleClassName );
	    
	    

		Class<?>[] interfaces = role.getClass().getInterfaces();
	    
		
	    // create a CtClass for IRole
	    final CtClass iRoleCtClass = pool.makeInterface(interfaces[0].getName());
	    newRoleCtClass.addInterface(iRoleCtClass);
	    
	    Method[] interfaceMethods = interfaces[0].getDeclaredMethods();
	    
	    
	    // add a private target field to the role class
	    final String targetDataType = target.getName();
	    
	    
	    try {
	    	System.out.println("private " + targetDataType 
											 + " _target;");
			CtField targetField = CtField.make("private " + targetDataType 
											 + " _target;", newRoleCtClass);
			
	    	
	    	
			newRoleCtClass.addField(targetField);
		    
			
		    // add a default construct
			
		    // 
			String strConstructor = 
				"public " + roleClassName + "(" + targetDataType + " param)" +
				"{_target = param;}";
			
			System.out.println(strConstructor  + " body");
			final CtConstructor constructor =
				CtNewConstructor.make(strConstructor, newRoleCtClass);
				//new CtConstructor(new CtClass[0], newRoleCtClass);
		    
			constructor.setModifiers(Modifier.PUBLIC);
		    
		    
		    newRoleCtClass.addConstructor(constructor);
		    
		    // implements the interface role	
		    Method[] roleMethods = role.getClass().getDeclaredMethods(); 
		    
		    for (int i = 0; i < roleMethods.length; i++) {
		    		
		    	boolean methodInInterface = false;
		    	for(int m=0; m<interfaceMethods.length; m++)
		    	{
		    		//TODO: 21.03.2012
		    		// I must add the methods contained in IRole.
		    		// not only the methods with an annotation 
		    		
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
		    		
		    		methodInInterface = true;
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
		    				// TODO
		    				// throw exception roleMap.target != target	
		    			}
		    			
		    			if(roleMap.method() != roleMethods[i].getName())
		    			{
		    				// TODO
		    				// throw exception roleMap.method != currentMethod
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
				        	// param = roleMethods[i].getParameterTypes()
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
				        	// ......
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
				    	
				    	System.out.println("method to add to interface: " + methodSignature);
				    	
				    	System.out.println("method body to add to interface: " + newMethodBody);
				    	
				    	
				    	CtMethod newMethodSgn = CtMethod.make(
				    			newMethodBody + ";",
				    			newRoleCtClass);
				    	
				    	
				    	newRoleCtClass.addMethod(newMethodSgn);
		    		}
		    							
		    	}
		    		
		      } // end for (int i = 0; i < roleMethods.length; i++)  
		   
		    try {
				newRoleCtClass.writeFile("/home/claudio/Templates");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			
			Class finalClass = newRoleCtClass.toClass();
		    allClasses.put(key, finalClass);
		    
			    	
		    
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
	
		if (allClasses.containsKey(key) == false)
		{
			// TODO
			// throw exception
		}
		
		
		try {
			Class classTest = allClasses.get(key);
			System.out.println("CIAO " + classTest.getName());
			
			for( Constructor c : classTest.getConstructors() ){
				System.out.println("Costruttore " + c.toGenericString() );
				for( Class f : c.getParameterTypes() )
				System.out.println("\t" + f.getName());
			}
			
			Constructor ctr = classTest.getDeclaredConstructor(target.getClass());//.newInstance();
			
			
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