/* 
 * JFK - Java Function Kernel
 *
 * This project provides a run-time framework to achieve functional programming
 * in Java. The idea is to have the capability to get something similar to function pointers
 * and C# delegates in Java, doing all the bindings and reference resolution at run-time being able,
 * at the same time, being able to compile the program using a function-first-class entity and abstraction.
 *
 * Copyright (C) Luca Ferrari 2010-2011 - cat4hire@users.sourceforge.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package jfk.function.classloaders;


import java.io.IOException;
import java.lang.reflect.Method;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.NotFoundException;

import jfk.core.JFK;
import jfk.function.IFunction;
import jfk.function.IFunctionBuilder;
import jfk.function.delegates.Connect;
import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;
import jfk.function.impl.IDelegatableInitializer;

/**
 * The delegate class loaders, that implements abstract methods delegate.
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class DelegateClassLoader extends SecureClassLoader implements
	IDelegateConnector {

    
    /**
     * The logger for this class loader.
     */
    protected static Logger logger = org.apache.log4j.Logger.getLogger( FunctionClassLoader.class );
    
    // configure the logger
    static{
	DOMConfigurator.configure("conf/log4j.xml");
    }
    
    
    /**
     * An utility class to keep data for a connection.
     * It is used a C-like structure.
     * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
     *
     */
    class ConnectionData{
	public Method sourceMethod = null;
	public Method targetMethod = null;
	public IDelegate targetInstance = null;
	public String privateReferenceKey = null;
	public String annotationMethodName = null;
    }
    
    
    /**
     * A list of the connections to do when defining the implementation of the delegate.
     */
    private List<ConnectionData> connectionsToDo = new LinkedList<ConnectionData>();
    
    
    /**
     * The status of the class loader.
     */
    private ClassLoaderStatus status = ClassLoaderStatus.READY;


    /**
     * The name of the class to load and instantiate.
     */
    private String delegatableSuperClassName;
    
    

    
    
    /* (non-Javadoc)
     * @see jfk.function.classloaders.IDelegateConnector#prepareConnection(java.lang.reflect.Method, java.lang.reflect.Method, jfk.function.delegates.IDelegate)
     */
    @Override
    public synchronized boolean prepareConnection(
				     Method sourceMethod,
				     Method targetMethod,
				     IDelegate targetInstance) {
	
	// if the class loader is busy, avoid making the connection
	if( this.status.equals( ClassLoaderStatus.BUSY) )
	    return false;
	else{
	    ConnectionData data = new ConnectionData();
	    data.sourceMethod   = sourceMethod;
	    data.targetInstance = targetInstance;
	    data.targetMethod   = targetMethod;
	    
	    // get the annotation and store the method id
	    Connect connectAnnotation = targetMethod.getAnnotation( Connect.class );
	    data.annotationMethodName = connectAnnotation.name();
	    
	    this.connectionsToDo.add(data);
	    return true;
	}

    }

    /* (non-Javadoc)
     * @see jfk.function.classloaders.IDelegateConnector#createDelegate()
     */
    @Override
    public IDelegatable createDelegate()
				     throws CannotConnectDelegateException,
				     AlreadyImplementedDelegateException {
	
	
	try {
	    Class clazz = this.findClass( this.delegatableSuperClassName );
	    IDelegatable delegatable = (IDelegatable) clazz.newInstance();
	    // now initialize the delegate
	    for( ConnectionData data : this.connectionsToDo )
		((IDelegatableInitializer) delegatable)._setPrivateTarget( data.privateReferenceKey, data.targetInstance);
	    
	    // all done
	    return delegatable;
	    
	} catch (ClassNotFoundException e) {
	    throw new CannotConnectDelegateException("Cannot create the delegate instance", e);
	} catch (InstantiationException e) {
	    throw new CannotConnectDelegateException("Cannot instantiate the delegate ", e);
	} catch (IllegalAccessException e) {
	    throw new CannotConnectDelegateException("Cannot instantiate the delegate ", e);
	}
	
    }

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
	// check arguments
	if( name == null || name.length() <= 0 || this.connectionsToDo.isEmpty() )
	    throw new ClassNotFoundException("Cannot load the class, invalid name or nothing to connect!");
	
	// the class loader is busy now
	synchronized( this ){
	    this.status = ClassLoaderStatus.BUSY;
	}


	// get the class pool for working with classes and modifying them on the fly
	ClassPool pool = ClassPool.getDefault();
	CtClass baseProxyClass = null;

	// the array that will store the in-memory byte code
	byte[] bytecode = null;
	
	

	try {
	    // load the class requested as superclass
	    CtClass delegatableSuperClass = pool.get(name);
	    
	    // now create a new class as subclass of the above one
	    String delegatableClassName = ClassLoaderUtils.getDelegateClassName(name);
	    logger.debug("The new subclass will have the name " + delegatableClassName);
	    CtClass delegatableCtClass = pool.makeClass(delegatableClassName);
	    delegatableCtClass.setSuperclass(delegatableSuperClass);
	    
	    

	    
	    
	    // create a map to keep track of the objects to be used as target
	    String privateMapName = "__targetMap_";
	    CtField mapField = new CtField( pool.get("java.util.HashMap"),	// type of the field
		    			    privateMapName,			// the name of the field
		    			    delegatableCtClass			// the class this field belongs to
		    			   );
	    delegatableCtClass.addField(mapField);
	    
	    // the list for the functions to bind at run-time
	    String privateFunctionListName = ClassLoaderUtils.computePrivateListName( name );
	    // add a new list member to contain all the functions
	    CtField privateFunctionListCtField = new CtField( pool.get("java.util.List"),	// field type
		    					privateFunctionListName,		// name of the field
    							delegatableCtClass			// declaring class
	    );
	    delegatableCtClass.addField(privateFunctionListCtField);
	    
	    // now iterate on each connection I need to do
	    for( ConnectionData currentConnectionData : this.connectionsToDo ){
		// extract the data for this connection
		Method sourceMethod = currentConnectionData.sourceMethod;
		Method targetMethod = currentConnectionData.targetMethod;
		IDelegate target    = currentConnectionData.targetInstance;
		
		
		
		logger.debug("Analyzing the connection " + sourceMethod.getName() +" -> " + target + "->" + targetMethod.getName());
		
		
		// check that the methods have the same number of parameters and the same type
		// as well as the same type of return
		Class[] sourceParameters = sourceMethod.getParameterTypes();
		Class[] targetParameters = targetMethod.getParameterTypes();
		if( (sourceParameters == null && targetParameters != null ) || (sourceParameters != null && targetParameters == null) 
			|| (sourceParameters.length != targetParameters.length) )
		    throw new CannotConnectDelegateException("Wrong signature: invalid argument numbers");
		
		// check arguments type
		for( int i = 0; sourceParameters != null && targetParameters != null && i < sourceParameters.length; i++ )
		    if( ! sourceParameters[i].equals(targetParameters[i]) )
			throw new CannotConnectDelegateException("Wrong signature: argument " + i + " mismatch");
		
		// check the return type
		if( ! sourceMethod.getReturnType().equals(targetMethod.getReturnType()) )
		    throw new CannotConnectDelegateException("Wrong signature: return type mismatch");
		

		
		// if here I can proceed building the method binding
		
		
		
		// the target field will be stored in the private map with the key as the string
		// defined here
		String privateRefenceName = ClassLoaderUtils.computePrivateTargetReferenceName( target.getClass() );
		logger.debug("Injecting the private reference to the target object " + privateRefenceName);
		currentConnectionData.privateReferenceKey = privateRefenceName;
		
		
		// now I can implement the method
		StringBuffer methodCode = new StringBuffer( 1000 );
		methodCode.append( "public " );
		if( sourceMethod.getReturnType().getName().equals("void") || sourceMethod.getReturnType().equals(java.lang.Void.class) )
		    methodCode.append(" void ");
		else
		    methodCode.append( sourceMethod.getReturnType().getName() );
		
		methodCode.append(" ");
		methodCode.append( sourceMethod.getName() );
		methodCode.append( "(" );
		for(int i = 0; targetParameters != null &&  i < targetParameters.length; i++){
		    if( i > 0 )
			methodCode.append(",");
		    
		    methodCode.append( sourceParameters[i].getName() );
		    methodCode.append( " " );
		    methodCode.append( "param" + i );
		}
		
		methodCode.append( "){\n\t" );
		
		// body definition
		if( sourceMethod.getReturnType().getName().equals("void") || sourceMethod.getReturnType().equals(java.lang.Void.class) )
		    methodCode.append(" ");
		else
		    methodCode.append( "return ");

		methodCode.append( "((" );
		methodCode.append( target.getClass().getName() );
		methodCode.append( ")" );
		methodCode.append("this.");
		methodCode.append( privateMapName );
		methodCode.append( ".get(\"");
		methodCode.append( privateRefenceName );
		methodCode.append( "\")" );
		methodCode.append( ")" );

		methodCode.append(".");
		methodCode.append( targetMethod.getName() );
		
		
		StringBuffer parameterCode = new StringBuffer(500);
		methodCode.append( "(" );
		
		for(int i = 0; targetParameters != null &&  i < targetParameters.length; i++){
		    if( i > 0 )
			parameterCode.append(",");
		    
		    parameterCode.append( "(" );
		    parameterCode.append( targetParameters[i].getName() );
		    parameterCode.append( ") " );
		    parameterCode.append( " " );
		    parameterCode.append( "param" + i );
		}
		
		// copy the list of parameters to the method code
		methodCode.append( parameterCode.toString() );
		methodCode.append(");");
		
		
		methodCode.append("\n\n\n\t");
		
		// now I've done the first connection, the others must be done
		// using an iteration over the function list
		methodCode.append(" try{\n\t\t" );
		methodCode.append( "if( this." );
		methodCode.append( privateFunctionListName );
		methodCode.append( " != null )\n\t\t\t" );
		methodCode.append( "for(int i = 0; i < " );
		methodCode.append( privateFunctionListName );
		methodCode.append( ".size(); i++)\n\t\t\t\t" );
		methodCode.append( " ((jfk.function.IFunction) " );
		methodCode.append( privateFunctionListName );
		methodCode.append(".get(i) " );
		methodCode.append( ").executeCall" );
		methodCode.append( "( new Object[]{" );
		methodCode.append( parameterCode.toString() );
		methodCode.append( "} );");
		methodCode.append( "\n\n\t" );
		methodCode.append("}catch(Exception e){ }\n\n ");
		
		
		// end of the body
		methodCode.append("\n}\n");
		
		logger.debug("Generated method code\n\n" + methodCode.toString());
		
		// now create this method and add to the class
		CtMethod currentMethodImplementation = CtMethod.make( methodCode.toString(), delegatableCtClass);
		delegatableCtClass.addMethod(currentMethodImplementation);
		
		
		
		
		
		 
	    }
	    
	    
	    
	    // now I need to implement a new method to set the delegate reference (initialization)
	    StringBuffer methodCode = new StringBuffer(1000);
	    methodCode = new StringBuffer(1000);
	    methodCode.append( "public void _setPrivateTarget(" );
	    methodCode.append( " String key, " );
	    methodCode.append( IDelegate.class.getName() );
	    methodCode.append( " delegate){\n\t" );
	    methodCode.append(" if( this." );
	    methodCode.append( privateMapName );
	    methodCode.append( " == null )\n\t\t this. ");
	    methodCode.append( privateMapName );
	    methodCode.append( " = new java.util.HashMap();\n" );
	    methodCode.append( "\t this.");
	    methodCode.append( privateMapName );
	    methodCode.append( ".put(key,delegate);" );

	    methodCode.append( "\n}\n");

	    logger.debug("Generated setter method\n" + methodCode.toString() );
	    // now create this method and add to the class
	    CtMethod currentMethodImplementation = CtMethod.make( methodCode.toString(), delegatableCtClass);
	    delegatableCtClass.addMethod(currentMethodImplementation);
	    // add the delegate initializer interface
	    delegatableCtClass.addInterface( pool.get( IDelegatableInitializer.class.getName() ) );

	    
	    
	    
	    
	    
	    // I need to create the methods to add a new delegate and to remove one
	    
	    methodCode = new StringBuffer( 1000 );
	    methodCode.append( "public boolean addDelegate( jfk.function.delegates.IDelegate delegateToAdd, String name ){\n\t");
	    methodCode.append("if( this." );
	    methodCode.append( privateFunctionListName );
	    methodCode.append( " == null)\n\t\t" );
	    methodCode.append( " this." );
	    methodCode.append( privateFunctionListName );
	    methodCode.append( " = new java.util.LinkedList();\n\n\t");
	    methodCode.append( "if( ! this." );
	    methodCode.append( privateFunctionListName );
	    methodCode.append( ".contains(delegateToAdd) ){\n\t\t");
	    methodCode.append( "this." );
	    methodCode.append( privateFunctionListName );
	    methodCode.append( ".add( ");
	    // I have to build a function for this target
	    methodCode.append( " jfk.core.JFK.getFunctionBuilder().bindDelegateFunction(delegateToAdd, name  ) " );
	    
	    methodCode.append(" );\n\t\t return true;\n\t}\n" );

	    methodCode.append("\telse return false;\n}\n");
	    logger.debug("Creating the add delegate method\n" + methodCode.toString());
	    CtMethod addDelegateMethod = CtMethod.make( methodCode.toString(), delegatableCtClass);
	    delegatableCtClass.addMethod(addDelegateMethod);
	    
	    // create the remove delegate method
	    methodCode = new StringBuffer( 1000 );
	    methodCode.append( "public boolean removeDelegate( jfk.function.delegates.IDelegate delegateToRemove ){\n\t");
	    methodCode.append("if( this." );
	    methodCode.append( privateFunctionListName );
	    methodCode.append( " == null)\n\t\t return false;\n\n\t" );
	    methodCode.append( "if(  this." );
	    methodCode.append( privateFunctionListName );
	    methodCode.append( ".contains(delegateToRemove) ){\n\t\t");
	    methodCode.append( "this." );
	    methodCode.append( privateFunctionListName );
	    methodCode.append( ".remove(delegateToRemove);\n\t\t return true;\n\t}\n");
	    methodCode.append("\telse return false;\n}\n");
	    logger.debug("Creating the remove delegate method\n" + methodCode.toString());
	    CtMethod removeDelegateMethod = CtMethod.make( methodCode.toString(), delegatableCtClass);
	    delegatableCtClass.addMethod(removeDelegateMethod);
	    
	    
	    
	    // all ready
	    // now define the class
	    bytecode = delegatableCtClass.toBytecode();

	    // the class loader is ready now
	    synchronized( this ){
		this.status = ClassLoaderStatus.READY;
	    }


	    logger.debug("Defining bytecode for class " + delegatableClassName);
	    return this.defineClass( delegatableClassName, bytecode, 0, bytecode.length );

	    
	} catch (NotFoundException e) {
	    logger.error("Not Found!", e);
	    throw new ClassNotFoundException("Cannot load the delegatable class", e);
	} catch (CannotCompileException e) {
	    logger.error("Compilation error!", e);
	    throw new ClassNotFoundException("Cannot compile the subclass", e);
	} catch (CannotConnectDelegateException e) {
	    logger.error("Delegate error!", e);
	    throw new ClassNotFoundException("Cannot bind the delegate", e);
	} catch (IOException e) {
	    throw new ClassNotFoundException("Cannot generate the bytecode for the delegate", e);
	}
	
	
	
	
    }

    @Override
    public synchronized void setDelegatableSource(Class source) {
	// check arguments
	if( this.status.equals( ClassLoaderStatus.BUSY) || source == null )
	    throw new IllegalArgumentException("Cannot change the source class");
	
	// store the name of the class to instantiate
	this.delegatableSuperClassName = source.getName();	
	
	
	
	
    }

 
  
    
}
