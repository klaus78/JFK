/* 
 * JFK - Java Function Kernel
 *
 * This project provides a run-time framework to achieve functional programming
 * in Java. The idea is to have the capability to get something similar to function pointers
 * and C# delegates in Java, doing all the bindings and reference resolution at run-time being able,
 * at the same time, being able to compile the program using a function-first-class entity and abstraction.
 *
 * Copyright (C) Luca Ferrari 2010-2012 - fluca1978 (at) gmail.com
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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import jfk.function.IClosure;
import jfk.function.IFunction;
import jfk.function.exception.BadArityException;
import jfk.function.exception.BadParameterTypeException;
import jfk.function.exception.ClosureException;
import jfk.function.exception.TargetBindException;

/**
 * The class loader to build a closure.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public class ClosureClassLoader extends FunctionClassLoader {





    /**
     * The closure code to compile and get information on the closure.
     */
    private String closureCode = null;


    /**
     * 
     */
    public ClosureClassLoader() {
	// TODO Auto-generated constructor stub
    }




    /* (non-Javadoc)
     * @see java.lang.ClassLoader#findClass(java.lang.String)
     */
    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
	try {

	    // security check: do not load any class that is not the IFunction one!
	    if( ! IClosure.class.getName().equals(name) )
		throw new ClassNotFoundException("Cannot load a class different from IClosure with this classloader!");


	    // the class loader is busy now
	    synchronized( this ){
		status = ClassLoaderStatus.BUSY;
	    }


	    // get the class pool for working with classes and modifying them on the fly
	    final ClassPool pool = ClassPool.getDefault();
	    final CtClass baseProxyClass = null;

	    // the array that will store the in-memory byte code
	    byte[] bytecode = null;



	    // I need also a ctclass for the target object
	    // Since a closure does not have a target, I need to use a simple Object
	    final String targetObjectClassName = ClassLoaderUtils.getClosureClassName();
	    final CtClass targetObjectCtClass = pool.makeClass( targetObjectClassName );

	    // compile the method passed and inject it into the target object
	    logger.debug("Compiling the closure method code\n");
	    logger.debug( closureCode );
	    final CtMethod closureMethod = CtMethod.make( closureCode, targetObjectCtClass );
	    targetObjectCtClass.addMethod(closureMethod);


	    try{
		// now I compile the class and create a new object
		bytecode = targetObjectCtClass.toBytecode();
		final Class compiledClosureClass = this.defineClass( targetObjectClassName, bytecode, 0, bytecode.length );
		targetInstance = compiledClosureClass.newInstance();

		// if here I've got the closure target object, get the method for such target object
		for( final Method met : compiledClosureClass.getDeclaredMethods() )
		    if( met.getName().equals( closureMethod.getName() ) ){
			currentMethod = met;
			break;
		    }

		// the method must be found
		if( currentMethod == null )
		    throw new ClassNotFoundException("Cannot find the closure implementation method");

		logger.debug("Closure target object built: " + targetInstance.getClass() );

	    }catch (final InstantiationException e) {
		logger.error("Cannot instantiate class while defining closure target object", e);
		throw new ClassNotFoundException("Instantiation problem while creating the closure target object", e);
	    } catch (final IllegalAccessException e) {
		logger.error("Cannot access class while defining closure target object", e);
		throw new ClassNotFoundException("Access problem while defining the closure target object",  e);
	    }



	    // I have to compute a name for the class to implement.
	    final String closureClassName = ClassLoaderUtils.computeClosureClassName();
	    logger.debug("The new closure class name is " + closureClassName );

	    // create a new class for the specified name
	    final CtClass newClosureClass = pool.makeClass( closureClassName );



	    // now the class has the new member, I need an empty constructor so to be sure that reflection
	    // will work instantiating the class
	    logger.debug("Creating a new empty constructor");
	    final CtConstructor constructor = new CtConstructor(null, newClosureClass );
	    constructor.setBody(";");
	    newClosureClass.addConstructor(constructor);



	    // now I have to add a field with the target object on which I'm going to call the method.
	    // The idea is that the function object will have a private refence to the target object
	    // on which it will call the method specified.
	    final String privateReferenceName = ClassLoaderUtils.computePrivateTargetReferenceName( targetObjectClassName );
	    logger.debug("The private reference to the target object will be " + privateReferenceName );

	    // now that I've got the name, create the field to add to the new class
	    final CtField privateReferenceField = new CtField( targetObjectCtClass,		// class type of the field 
		    privateReferenceName, 		// field name
		    newClosureClass 		// declaring class
	    );
	    // TODO make the modifier private
	    //privateReferenceField.setModifiers(  )
	    newClosureClass.addField( privateReferenceField );


	    // now I need to implement the inteface IClosure
	    final CtClass iClosureCtClass = pool.get( IClosure.class.getName() );
	    newClosureClass.addInterface(iClosureCtClass);

	    // now add the IFunction method implementation
	    StringBuffer methodCode = new StringBuffer(1000);
	    // WARNING: IClosure does not define any method, I have to go to the superclass which is a IFunction
	    final CtClass functionCtClass = pool.get( IFunction.class.getName() );
	    for( final CtMethod iFunctionMethod : functionCtClass.getDeclaredMethods() ){

		logger.debug("Implementing the IClosure method " + iFunctionMethod.getName() );

		// method name and qualifiers
		methodCode.append( "public");
		methodCode.append( " " );
		methodCode.append( iFunctionMethod.getReturnType().getName() );
		methodCode.append( " " );
		methodCode.append( iFunctionMethod.getName() );
		methodCode.append( "(" );

		// parameter list
		final CtClass params[] = iFunctionMethod.getParameterTypes();
		for( int paramNumber = 0; (params != null) && (paramNumber < params.length); paramNumber++ ){
		    if( paramNumber > 0 )
			methodCode.append( ", ");

		    methodCode.append( params[paramNumber].getName() );
		    methodCode.append( " " );
		    methodCode.append( "param" + paramNumber); 
		}

		methodCode.append( ") " );


		// exception management
		final CtClass exceptions[] = iFunctionMethod.getExceptionTypes();
		if( (exceptions != null) && (exceptions.length > 0) ){
		    methodCode.append( "throws " );

		    for( int exceptionNumber = 0; exceptionNumber < exceptions.length; exceptionNumber++ ){
			if( exceptionNumber > 0 )
			    methodCode.append( ", " );

			methodCode.append( exceptions[ exceptionNumber ].getName() );
		    }
		}



		// the body of the method starts here
		methodCode.append( "\n{\n\t" );





		// security checks: the method must receive the exact number of parameters!
		final int requiredParameters = ( currentMethod.getParameterTypes() != null ? currentMethod.getParameterTypes().length : 0 );
		if( requiredParameters > 0 ){
		    methodCode.append(" if( param0 == null || param0.length != ");
		    methodCode.append( currentMethod.getParameterTypes().length );
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
		    final Class parameterTypes[] = currentMethod.getParameterTypes();

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

		// WARNING: if the method has a void return type, do not insert a return statement
		final boolean isVoid = ( currentMethod.getReturnType().toString().equals("void") || java.lang.Void.class.equals( currentMethod.getReturnType() ) ); 
		if(! isVoid ){
		    methodCode.append( "return");
		    methodCode.append( " " );
		    methodCode.append( "(" );
		    methodCode.append( currentMethod.getReturnType().getName() );
		    methodCode.append( ") " );
		}

		methodCode.append( " this." );
		methodCode.append( privateReferenceName );
		methodCode.append( "." );
		methodCode.append( currentMethod.getName() );
		methodCode.append( "(" );

		// now the parameter list, that should be only for those expected
		final Class targetMethodParameters[] = currentMethod.getParameterTypes();

		for( int paramNumber = 0; (targetMethodParameters != null) && (paramNumber < targetMethodParameters.length); paramNumber++ ){
		    if( paramNumber > 0 )
			methodCode.append( ", " );

		    // cast the current argument to the right type
		    methodCode.append( "(" );
		    methodCode.append( targetMethodParameters[paramNumber].getName() );
		    methodCode.append( ")" );
		    // the argument value is in the param array
		    methodCode.append( "param0"  + "[" + paramNumber + "]" );	// variadic method -> array param0!
		}

		methodCode.append( ");" );


		// a return statement?
		if( isVoid ){
		    // insert a return statement
		    methodCode.append( "\n\t return null;\n" );
		}

		// end of the method body
		methodCode.append( "\n}\n" );

		logger.debug("Generated method body:\n");
		logger.debug( methodCode.toString() );

		// now compile the method and add it to the class
		final CtMethod compiledMethod = CtMethod.make( methodCode.toString(), newClosureClass );
		newClosureClass.addMethod(compiledMethod);
		logger.debug("Creation of the method completed!");
	    }





	    // now I need to add the interface and the method to initialize the private
	    // variable of the new function instance
	    methodCode = new StringBuffer( 1000 );
	    final CtClass binderClass = pool.get( IFunctionBinder.class.getName() );

	    // add the interface to the current class
	    newClosureClass.addInterface(binderClass);

	    // create all the methods (it should be only one)
	    for( final CtMethod currentMethod : binderClass.getDeclaredMethods() ){
		methodCode.append( " public ");
		methodCode.append( currentMethod.getReturnType().getName() );
		methodCode.append(  " " );
		methodCode.append( currentMethod.getName() );
		methodCode.append( "(" );

		final CtClass[] parameters = currentMethod.getParameterTypes();
		for( int parameterNumber = 0; (parameters != null) && (parameterNumber < parameters.length); parameterNumber++ ){
		    if( parameterNumber > 0 )
			methodCode.append( ", " );

		    methodCode.append( parameters[parameterNumber].getName() );
		    methodCode.append( " " );
		    methodCode.append( " param" + parameterNumber );
		}

		methodCode.append( ") " );

		// exception management
		final CtClass exceptions[] = currentMethod.getExceptionTypes();
		if( (exceptions != null) && (exceptions.length > 0) ){
		    methodCode.append( "throws " );

		    for( int exceptionNumber = 0; exceptionNumber < exceptions.length; exceptionNumber++ ){
			if( exceptionNumber > 0 )
			    methodCode.append( ", " );

			methodCode.append( exceptions[ exceptionNumber ].getName() );
		    }
		}

		methodCode.append( "\n" );
		// body definition
		methodCode.append( "{\n\t" );


		// security check: the first argument must be of the right type
		methodCode.append(" if( param0 == null || (! ( param0 instanceof ");
		methodCode.append( targetInstance.getClass().getName() );
		methodCode.append( ") ) )" );
		methodCode.append( "\n\t\t" );
		methodCode.append( "throw new " );
		methodCode.append( TargetBindException.class.getName() );
		methodCode.append( "(\"The binding object is not of the right type!\");" );
		methodCode.append( "\n\n" );


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
		final CtMethod compiledMethod = CtMethod.make( methodCode.toString(), newClosureClass );
		newClosureClass.addMethod(compiledMethod);
		logger.debug("Creation of the method completed!");
	    }



	    // now define the class
	    bytecode = newClosureClass.toBytecode();

	    // the class loader is ready now
	    synchronized( this ){
		status = ClassLoaderStatus.READY;
	    }


	    return this.defineClass( closureClassName, bytecode, 0, bytecode.length );


	} catch (final NotFoundException e) {
	    logger.error("Exception caught while defining a class",e);
	    throw new ClassNotFoundException("Cannot find class", e );
	} catch (final IOException e) {
	    throw new ClassNotFoundException("Cannot find class", e );
	} catch (final CannotCompileException e) {
	    logger.error("Cannot compile exception caught while definining a IFunction class ", e);
	    throw new ClassNotFoundException("Cannot find class", e );
	} finally{
	    // the class loader is ready now
	    synchronized( this ){
		status = ClassLoaderStatus.READY;
	    }
	}

    }




    /**
     * A method to get the closure implementation.
     * @return
     * @throws ClosureException
     */
    public synchronized final IClosure getClosure() throws ClosureException {
	Class closureClass;
	try {
	    closureClass = findClass( IClosure.class.getName() );
	    return (IClosure) closureClass.newInstance();
	} catch (final ClassNotFoundException e) {
	    throw new ClosureException( e );
	} catch (final InstantiationException e) {
	    throw new ClosureException( e );
	} catch (final IllegalAccessException e) {
	    throw new ClosureException( e );
	}

    }




    /**
     * Provides the built target instance.
     * @return
     */
    public synchronized final Object getTargetInstance() {
	return targetInstance;
    }




    /**
     * A method to set the value of the closureCode
     * field within this object instance.
     * @param closureCode the closureCode to set
     */
    public synchronized final void setClosureCode(final String closureCode) {
	// set the value of the this.closureCode field
	this.closureCode = closureCode;
    }








}
