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
package jfk.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore.Builder;
import java.util.StringTokenizer;

import jfk.core.JFK;
import jfk.function.Function;
import jfk.function.IFunction;
import jfk.function.IFunctionBuilder;
import jfk.function.JFKException;
import jfk.function.classloaders.ClassLoaderUtils;
import jfk.function.classloaders.FunctionClassLoader;
import jfk.function.classloaders.IFunctionBinder;
import jfk.function.classloaders.IFunctionClassDefiner;
import jfk.function.exception.CannotBindFunctionException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A test for the class loaders.
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class ClassLoaderTest {

    @Test
    public void testClassLoaderUtils(){
	// the set method name should always be the same
	String name1 = ClassLoaderUtils.getSetTargetMethodName();
	String name2 = ClassLoaderUtils.getSetTargetMethodName();
	
	if( ! name1.equals(name2) )
	    fail("Class set target object method name differs! ");
	
	
	// get the annotation from the dummy example
	for( Method m : DummyClass.class.getMethods() )
	    if( m.isAnnotationPresent( Function.class) ){
		Function annotation = m.getAnnotation( Function.class );
		
		name1 = ClassLoaderUtils.computeFunctionClassName(annotation, DummyClass.class );
		name2 = ClassLoaderUtils.computeFunctionClassName(annotation, DummyClass.class );
		
		if( name1.equals(name2))
		    fail("Class name clash!");
	    }
		
	
	// compute a reference name
	name1 = ClassLoaderUtils.computePrivateTargetReferenceName( DummyClass.class );
	name2 = "";
	StringTokenizer tokenizer = new StringTokenizer( DummyClass.class.getName(), "." );
	while( tokenizer.hasMoreElements() )
	    name2 += "_" + tokenizer.nextToken();
	
	if( ! name1.equals(name2) )
	    fail("Private reference name error! " + name1 + " " + name2);
	
    }
    
    
    @Test
    public void testFunctionClassDefinition() throws ClassNotFoundException, InstantiationException, IllegalAccessException, JFKException{
	
	// get a new class loader
	IFunctionClassDefiner loader = new FunctionClassLoader();
	
	DummyClass dummy = new DummyClass();
	
	for( Method m : dummy.getClass().getDeclaredMethods() )
	    if( m.isAnnotationPresent( Function.class) ){
		Function annotation = m.getAnnotation( Function.class );
		
		if( ! annotation.name().equals("hello") )
		    continue;
		
		// now get the IFunction object
		Class clazz = loader.getIFunctionClassDefinition( dummy, m );
		
		if( clazz == null )
		    fail("Loaded class is null");
		
		// print out what the class is
		System.out.println("Class " + clazz.getName() );
		for(Class intf : clazz.getInterfaces() )
		    System.out.println("Interface " + intf.getName() );
		
		for( Method mm : clazz.getMethods() )
		    System.out.println("Method " + mm.getName() + " access " + mm.getModifiers());
		
		for( Constructor mc : clazz.getDeclaredConstructors() ){
		    System.out.println("Constructor " + mc.getName() + " " );
		    System.out.println("\tAccess type " + mc.getModifiers());
		    for( Class p : mc.getParameterTypes() )
			System.out.println("\tParam " + p.getName() );
		}
		
		for( Field f : clazz.getDeclaredFields() )
		    System.out.println("Field " + f.getName()  + " " + f.getType().getName());
		
		System.out.println("Superclass " + clazz.getSuperclass().getName());
		
		// get an instance
		IFunction function = (IFunction) clazz.newInstance();
		
		
		if( function == null )
		    fail("Null function instance");
		if( !(function instanceof IFunction) )
		    fail("Not an IFunction object!");
		if( !(function instanceof IFunctionBinder) )
		    fail("Not an instance of IFunctionBinder");
		
		// set the target
		((IFunctionBinder) function).setTargetObject(dummy);
		
		// execute the function
		String result = (String) function.executeCall( null );
		
		// test the result
		if( ! dummy.resultString.equals(result) )
		    fail("Function invocation does not result!");
	    }
	
    }

    
    
    @Test
    public void testEndUserApi() throws JFKException{
	// create the dummy object
	DummyClass dummy = new DummyClass();
	
	// now get the function
	IFunctionBuilder builder = JFK.getFunctionBuilder();
	IFunction function = builder.bindFunction( dummy, "hello" );
	
	// execute the function
	String result = (String) function.executeCall( null );
	
	// test the result
	if( ! dummy.resultString.equals(result) )
	    fail("Function invocation does not result!");
	
	// now get another function
	IFunction function2 = builder.bindFunction(dummy, "double" );
	
	if( function.equals(function2) )
	    fail("Two functions should not be equale with different names!");
	
	Double d1 = new Double(10.5);
	Double d2 = (Double) function2.executeCall( new Object[]{ d1 } );
	if( (d1.doubleValue() * 2) != (d2.doubleValue() ) )
	    fail("Return value is not the same! (" + d1.doubleValue() + " vs " + d2.doubleValue() + ")");
	
	
	// test arity
	try{
	    function2 = builder.bindFunction(dummy, "string2");
	    function2.executeCall(null);
	}catch(JFKException e){
	    e.printStackTrace();
	}
	
	
	// another function
	int value = 10;
	IFunction function3 = builder.bindFunction(dummy, "string");
	if( function3.equals(function2) || function3.equals(function) )
	    fail("A function that should not be the same of a previous one!");
	
	result = (String) function3.executeCall( new Object[]{ value } );
	if( ! result.equals( dummy.resultString + value ) )
	    fail("Result value for string composition is wrong!");
	
	
	// test arity
	try{
	    function3 = builder.bindFunction(dummy, "string2");
	    function3.executeCall(new Object[]{ value });
	    fail("Method call bad arity check passed!");
	}catch(JFKException e){
	    e.printStackTrace();
	}
	
	
	// test function parameters
	try{
	    function3 = builder.bindFunction(dummy, "string2");
	    function3.executeCall(new Object[]{ new String("A"), new String("B") });
	    fail("Method call parameter check passed!");
	}catch(JFKException e){
	    e.printStackTrace();
	}
	
	
    }
    
    
    
    @Test
    public void testCache() throws CannotBindFunctionException{
	
	// create the dummy object
	DummyClass dummy = new DummyClass();
	
	// now get the function
	IFunctionBuilder builder = JFK.getFunctionBuilder();
	IFunction function = builder.bindFunction( dummy, "hello" );
	
	IFunction function2 = builder.bindFunction(dummy, "hello");
	
	if(! function.equals(function2) )
	    fail("Don't get the same function for the same method!");
    }
    
    
    
    @Test
    public void speedTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, JFKException{
	// create the dummy object
	DummyClass dummy = new DummyClass();
	
	// now get the function
	IFunctionBuilder builder = JFK.getFunctionBuilder();
	long start1 = System.nanoTime();
	IFunction function = builder.bindFunction( dummy, "hello" );
	long end1 = System.nanoTime();
	
	function.executeCall(null);
	long end2 = System.nanoTime();
	
	System.out.println("Total compilation and execution time = " + (end2 - start1) );
	System.out.println("Compilation/Invocation time " + (end1-start1) + " " + (end2 - end1) );
	long total1 = end2 - start1;
	
	
	// reflection method
	start1 = System.nanoTime();
	for( Method m : dummy.getClass().getDeclaredMethods() )
	    if( m.isAnnotationPresent( Function.class) ){
		Function annotation = m.getAnnotation( Function.class );
		
		if( ! annotation.name().equals("hello") )
		    continue;
		
		end1 = System.nanoTime();
		m.invoke(dummy, null);
		end2 = System.nanoTime();
		break;
	    }
	
	
	
	System.out.println("Total reflection and execution time = " + (end2 - start1) );
	System.out.println("Reflection/Invocation time " + (end1-start1) + " " + (end2 - end1) );
	long total2 = end2 - start1;
	
	if( total2 < total1 )
	    fail("Reflection is faster than function objects!");
    }
    
}
