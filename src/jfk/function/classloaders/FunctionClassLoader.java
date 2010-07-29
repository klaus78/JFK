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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.SecureClassLoader;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.NotFoundException;

import jfk.function.Function;
import jfk.function.IFunction;
import jfk.function.JFKException;
import jfk.function.exception.BadArityException;
import jfk.function.exception.BadParameterTypeException;
import jfk.function.exception.CannotBindFunctionException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;



/**
 * This is a class loader used to produce an implementation of
 * the IFunction {@link IFunction} object in order to allow method/function mapping.
 * 
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class FunctionClassLoader extends SecureClassLoader implements IFunctionClassDefiner {

    /**
     * The logger for this class loader.
     */
    private static Logger logger = org.apache.log4j.Logger.getLogger( FunctionClassLoader.class );
    
    // configure the logger
    static{
	DOMConfigurator.configure("conf/log4j.xml");
    }
    
    
    /**
     * The function annotation this class loader is working on.
     */
    private Function functionAnnotation = null;
    
    /**
     * The method to which the function will be bound.
     */
    private Method currentMethod = null;
    
    /**
     * The target on which the method will be called when the function is executed.
     */
    private Object targetInstance = null;
    
    /**
     * The current status of the class loader.
     */
    private ClassLoaderStatus status = ClassLoaderStatus.READY;

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
	try {
	    
	    // security check: do not load any class that is not the IFunction one!
	    if( ! IFunction.class.getName().equals(name) )
		throw new ClassNotFoundException("Cannot load a class different from IFunction with this classloader!");
	    
	    
	    // the class loader is busy now
	    synchronized( this ){
		this.status = ClassLoaderStatus.BUSY;
	    }
	    
	    
	    // get the class pool for working with classes and modifying them on the fly
	    ClassPool pool = ClassPool.getDefault();
	    CtClass baseProxyClass = null;
	    
	    // the array that will store the in-memory byte code
	    byte[] bytecode = null;
	    
	    // I have to compute a name for the class to implement.
	    String functionClassName = ClassLoaderUtils.computeFunctionClassName( this.functionAnnotation, this.targetInstance.getClass() );
	    logger.debug("The new function class name is " + functionClassName );
	    
	    // create a new class for the specified name
	    CtClass newFunctionClass = pool.makeClass( functionClassName );
	    
	    // I need also a ctclass for the target object
	    CtClass targetObjectCtClass = pool.get( this.targetInstance.getClass().getName() );
	    
	    
	    
	    
	    // now the class has the new member, I need an empty constructor so to be sure that reflection
	    // will work instantiating the class
	    logger.debug("Creating a new empty constructor");
	    CtConstructor constructor = new CtConstructor(null, newFunctionClass );
	    constructor.setBody(";");
	    newFunctionClass.addConstructor(constructor);
	    
	    
	    
	    // now I have to add a field with the target object on which I'm going to call the method.
	    // The idea is that the function object will have a private refence to the target object
	    // on which it will call the method specified.
	    String privateReferenceName = ClassLoaderUtils.computePrivateTargetReferenceName( this.targetInstance.getClass() );
	    logger.debug("The private reference to the target object will be " + privateReferenceName );
	    
	    // now that I've got the name, create the field to add to the new class
	    CtField privateReferenceField = new CtField( targetObjectCtClass,		// class type of the field 
		                                         privateReferenceName, 		// field name
		                                         newFunctionClass 		// declaring class
		                                         );
	    // TODO make the modifier private
	    //privateReferenceField.setModifiers(  )
	    newFunctionClass.addField( privateReferenceField );
	    
	    
	    // now I need to implement the inteface IFunction
	    CtClass iFunctionCtClass = pool.get( IFunction.class.getName() );
	    newFunctionClass.addInterface(iFunctionCtClass);
	    
	    // now add the IFunction method implementation
	    StringBuffer methodCode = new StringBuffer(1000);
	    for( CtMethod iFunctionMethod : iFunctionCtClass.getDeclaredMethods() ){
				
		logger.debug("Implementing the IFunction method " + iFunctionMethod.getName() );
		
		// method name and qualifiers
		methodCode.append( "public");
		methodCode.append( " " );
		methodCode.append( iFunctionMethod.getReturnType().getName() );
		methodCode.append( " " );
		methodCode.append( iFunctionMethod.getName() );
		methodCode.append( "(" );
		
		// parameter list
		CtClass params[] = iFunctionMethod.getParameterTypes();
		for( int paramNumber = 0; params != null && paramNumber < params.length; paramNumber++ ){
		    if( paramNumber > 0 )
			methodCode.append( ", ");
		    
		    methodCode.append( params[paramNumber].getName() );
		    methodCode.append( " " );
		    methodCode.append( "param" + paramNumber); 
		}
		
		methodCode.append( ") " );
		methodCode.append( " throws " );
		methodCode.append( BadArityException.class.getName() );
		methodCode.append( ", " );
		methodCode.append( BadParameterTypeException.class.getName() );
		
		// the body of the method starts here
		methodCode.append( "\n{\n\t" );
		//methodCode.append( "System.out.println(\" method call on \" + "+ privateReferenceName + ");");
		
		
		
		// security checks: the method must receive the exact number of parameters!
		int requiredParameters = ( this.currentMethod.getParameterTypes() != null ? this.currentMethod.getParameterTypes().length : 0 );
		if( requiredParameters > 0 ){
		    methodCode.append(" if( param0 == null || param0.length != ");
		    methodCode.append( this.currentMethod.getParameterTypes().length );
		    methodCode.append( ")\n\t\t" );
		    methodCode.append( "{\n\t\t\t" );
		    methodCode.append( BadArityException.class.getName() );
		    methodCode.append( " bae = new " );
		    methodCode.append( BadArityException.class.getName() );
		    methodCode.append( "(\"Bad arity!\");\n\n");
		    methodCode.append( "\n\t\t\t" );
		    methodCode.append( "bae.setRequiredArity( " );
		    methodCode.append( requiredParameters );
		    methodCode.append( " );" );
		    methodCode.append( "\n\t\t\t" );
		    methodCode.append( "bae.setSuppliedArity( (param0 != null ? param0.length : 0 ) );" );
		    methodCode.append( "\n\t\t\t" );
		    methodCode.append( "throw bae; " );
		    methodCode.append( "\n\t\t" );
		    methodCode.append( "}\n" );
		    
		    
		    // another security check: the method must receive the exact type of the parameters
		    Class parameterTypes[] = this.currentMethod.getParameterTypes();
		    
		    for( int checkNumber = 0; checkNumber < parameterTypes.length; checkNumber ++ ){
			methodCode.append( "\n\t" );
			methodCode.append( "if( ! param0[" );
			methodCode.append( checkNumber );
			methodCode.append( "].getClass().getName().equals( \"" );
			methodCode.append( parameterTypes[checkNumber].getName() );
			methodCode.append( "\") )" );
			methodCode.append( "\n\t\t" );
			methodCode.append( "{\n\t\t\t" );
			methodCode.append( BadParameterTypeException.class.getName() );
			methodCode.append( " bpe = new " );
			methodCode.append( BadParameterTypeException.class.getName() );
			methodCode.append( "(\"Bad parameter type!\");\n\n");
			methodCode.append( "\n\t\t\t" );
			methodCode.append( "bpe.setRequiredType( ");
			methodCode.append( parameterTypes[checkNumber].getName() );
			methodCode.append( ".class" );
			methodCode.append( " );" );
			methodCode.append( "\n\t\t\t" );
			methodCode.append( "bpe.setSuppliedType( (param0[" );
			methodCode.append( checkNumber );
			methodCode.append( "] != null ? param0[" );
			methodCode.append( checkNumber );
			methodCode.append( "].getClass() : java.lang.Void.class) );" );
			methodCode.append( "\n\t\t\t" );
			methodCode.append( "throw bpe; " );
			methodCode.append( "\n\t\t" );
			methodCode.append( "}\n" );

		    }

		}
		
		
		
		
		
		methodCode.append( "\n\t" );
		methodCode.append( "return");
		methodCode.append( " " );
		methodCode.append( "(" );
		methodCode.append( this.currentMethod.getReturnType().getName() );
		methodCode.append( ") " );
		methodCode.append( " this." );
		methodCode.append( privateReferenceName );
		methodCode.append( "." );
		methodCode.append( this.currentMethod.getName() );
		methodCode.append( "(" );
		
		// now the parameter list, that should be only for those expected
		Class targetMethodParameters[] = this.currentMethod.getParameterTypes();

		for( int paramNumber = 0; targetMethodParameters != null && paramNumber < targetMethodParameters.length; paramNumber++ ){
		    if( paramNumber > 0 )
			methodCode.append( ", " );
		    
		    // cast the current argument to the right type
		    methodCode.append( "(" );
		    methodCode.append( targetMethodParameters[paramNumber].getName() );
		    methodCode.append( ")" );
		    // the argument value is in the param array
		    methodCode.append( "param0"  + "[" + paramNumber + "]" );	// variadic method -> array param!
		}
		
		methodCode.append( ");" );
		
		
		// end of the method body
		methodCode.append( "\n}\n" );
		
		logger.debug("Generated method body:\n");
		logger.debug( methodCode.toString() );
		
		// now compile the method and add it to the class
		CtMethod compiledMethod = CtMethod.make( methodCode.toString(), newFunctionClass );
		newFunctionClass.addMethod(compiledMethod);
		logger.debug("Creation of the method completed!");
	    }


	    
	    
	    
	    // now I need to add the interface and the method to initialize the private
	    // variable of the new function instance
	    methodCode = new StringBuffer( 1000 );
	    CtClass binderClass = pool.get( IFunctionBinder.class.getName() );
	    
	    // add the interface to the current class
	    newFunctionClass.addInterface(binderClass);

	    // create all the methods (it should be only one)
	    for( CtMethod currentMethod : binderClass.getDeclaredMethods() ){
		methodCode.append( " public ");
		methodCode.append( currentMethod.getReturnType().getName() );
		methodCode.append(  " " );
		methodCode.append( currentMethod.getName() );
		methodCode.append( "(" );
		
		CtClass[] parameters = currentMethod.getParameterTypes();
		for( int parameterNumber = 0; parameters != null && parameterNumber < parameters.length; parameterNumber++ ){
		    if( parameterNumber > 0 )
			methodCode.append( ", " );
		    
		    methodCode.append( parameters[parameterNumber].getName() );
		    methodCode.append( " " );
		    methodCode.append( " param" + parameterNumber );
		}
		
		methodCode.append( ")\n" );
		
		// body definition
		methodCode.append( "{\n\t" );
		methodCode.append( " this." );
		methodCode.append( privateReferenceName );
		methodCode.append( " = " );
		methodCode.append( "(" );
		methodCode.append( targetObjectCtClass.getName() );
		methodCode.append( ") " );
		methodCode.append( "param0;" );
		methodCode.append( "\n}\n" );
		
		logger.debug("Generated setter method \n" + methodCode.toString() );
		
		// now compile the method and add it to the class
		CtMethod compiledMethod = CtMethod.make( methodCode.toString(), newFunctionClass );
		newFunctionClass.addMethod(compiledMethod);
		logger.debug("Creation of the method completed!");
	    }
	    
	    

	    // now define the class
	    bytecode = newFunctionClass.toBytecode();
	    
	    // the class loader is ready now
	    synchronized( this ){
		this.status = ClassLoaderStatus.READY;
	    }

	    
	    return this.defineClass( functionClassName, bytecode, 0, bytecode.length );
	    
	    
        } catch (NotFoundException e) {
            logger.error("Exception caught while defining a class",e);
            throw new ClassNotFoundException("Cannot find class", e );
        } catch (IOException e) {
            throw new ClassNotFoundException("Cannot find class", e );
        } catch (CannotCompileException e) {
            logger.error("Cannot compile exception caught while definining a IFunction class ", e);
            throw new ClassNotFoundException("Cannot find class", e );
        } finally{
         // the class loader is ready now
	    synchronized( this ){
		this.status = ClassLoaderStatus.READY;
	    }
        }

    }
    
    /* (non-Javadoc)
     * @see jfk.function.classloaders.IFunctionClassDefiner#getIFunctionClassDefinition(java.lang.Object, java.lang.reflect.Method)
     */
    @Override
    public final synchronized Class getIFunctionClassDefinition(Object targetObject, Method targetMethod ) throws JFKException{
	try {
	    // check params
	    if( ClassLoaderStatus.BUSY.equals( this.status ) )
		throw new JFKException("The classloader is busy!");
	    
	    if( targetObject == null || targetMethod == null )
		throw new IllegalArgumentException("Cannot proceed without the method and the target object");
	    
	    if( ! targetMethod.isAnnotationPresent( Function.class ) )
		throw new CannotBindFunctionException("The specified method is not a function annotated one!");
	    
	    // get the annotation from the method
	    Function functionAnnotation = targetMethod.getAnnotation( Function.class );
	    
	    // set the parameters for the loader
	    this.currentMethod = targetMethod;
	    this.targetInstance = targetObject;
	    this.functionAnnotation = functionAnnotation;
	    
	    // define the class
	    return this.findClass( IFunction.class.getName() );
	} catch (ClassNotFoundException e) {
	    logger.error("Error defining the IFunction class", e);
	    throw new JFKException(e);
	}
    }
    
    

    /**
     * A method to set the value of the functionAnnotation
     * field within this object instance.
     * @param functionAnnotation the functionAnnotation to set
     */
    public synchronized final void setFunctionAnnotation(Function functionAnnotation) {
        // set the value of the this.functionAnnotation field only if the class loader is not busy
	if(  ClassLoaderStatus.BUSY.equals( this.status ) )
	    throw new IllegalArgumentException("Class loader is busy at the moment!");
	
        this.functionAnnotation = functionAnnotation;
    }

    /**
     * A method to set the value of the currentMethod
     * field within this object instance.
     * @param currentMethod the currentMethod to set
     */
    public synchronized final void setCurrentMethod(Method currentMethod) {
        // set the value of the this.currentMethod field
	if(  ClassLoaderStatus.BUSY.equals( this.status ) )
	    throw new IllegalArgumentException("Class loader is busy at the moment!");
	
        this.currentMethod = currentMethod;
    }

    /**
     * A method to set the value of the targetInstance
     * field within this object instance.
     * @param targetInstance the targetInstance to set
     */
    public synchronized final void setTargetInstance(Object targetInstance) {
        // set the value of the this.targetInstance field
	if(  ClassLoaderStatus.BUSY.equals( this.status ) )
	    throw new IllegalArgumentException("Class loader is busy at the moment!");
	
        this.targetInstance = targetInstance;
    }
    
    
    
   
    

}
