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
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import jfk.function.Function;
import jfk.function.IFunction;
import jfk.function.JFKException;
import jfk.function.classloaders.ClassLoaderUtils;
import jfk.function.classloaders.FunctionClassLoader;

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
	FunctionClassLoader loader = new FunctionClassLoader();
	
	DummyClass dummy = new DummyClass();
	
	for( Method m : dummy.getClass().getDeclaredMethods() )
	    if( m.isAnnotationPresent( Function.class) ){
		Function annotation = m.getAnnotation( Function.class );
		
		
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
		IFunction function = null;
		try{
		    function = (IFunction) clazz.newInstance();
		}catch(InstantiationException e){
		    System.out.println("Exception " + e +" = " + e.getCause() );
		    e.printStackTrace();
		    throw e;

		}
		
		
		if( function == null )
		    fail("Null function instance");
		if( !(function instanceof IFunction) )
		    fail("Not an IFunction object!");
	    }
	
    }

}
