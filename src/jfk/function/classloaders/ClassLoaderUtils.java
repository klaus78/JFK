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

import java.util.StringTokenizer;

import jfk.function.Function;

/**
 * A class that contains some utility methods to work with class loaders.
 * For instance, this class provides a unique naming schema for new and created classes.
 * 
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class ClassLoaderUtils {

    /**
     * A counter incremented each time a new name is computed.
     */
    private static int counter = 0;
    
    
    /**
     * This method computes a unique name for the function object depending on the function annotation,
     * the target class and the counter. The resulting name is compound by the target class simple name, 
     * the function annotation identifier, and a unique numeric value.
     * An example is <code> myClass_method1_123</code>
     * @param functionAnnotation the function annotation
     * @param targetClass the target class
     * @return the string with the name
     */
    public static synchronized String computeFunctionClassName( Function functionAnnotation, Class targetClass ){
	
	// check params
	if( functionAnnotation == null || targetClass == null )
	    throw new IllegalArgumentException("Cannot compute a name without the class of the target object and/or the function annotation!" );
	
	return computeFunctionClassName( functionAnnotation.name(), targetClass);
	
    }
    
    /**
     * Computes the name of the class to be defined within the function.
     * @param nameFromTheAnnotation
     * @param targetClass
     * @return
     */
    public static synchronized String computeFunctionClassName( String nameFromTheAnnotation, Class targetClass ){
	StringBuffer buffer = new StringBuffer(50);

	// the name of the class to build will be the composition of the simple name of the
	// target class name and the name of the annotation
	buffer.append( targetClass.getSimpleName() );
	buffer.append( "_" );
	buffer.append( nameFromTheAnnotation );
	buffer.append( "_" );
	buffer.append( ++counter );

	// all done
	return buffer.toString();
    }
    
    
    
    /**
     * Creates a new name for a private reference of the specified class. The name is compound by the fully
     * qualified name of the class with all the dots replaced with underscore, so for instance as <tt>jfk_example_myexample</tt>.
     * We don't use here a numeric discriminator in the name because it can be useful to find out if the private refernce
     * is already present is superclasses.
     * @param targetClass the class the reference will point to
     * @return the name of the reference to use
     */
    public static synchronized String computePrivateTargetReferenceName( Class targetClass ){
	// check arguments
	if( targetClass == null )
	    throw new IllegalArgumentException(" Cannot compute a name without the class of the target object!" );
	
	return computePrivateTargetReferenceName( targetClass.getName() );
	
    }
    
    
    /**
     * Creates a reference name starting from the class name.
     * The name is compound by the fully
     * qualified name of the class with all the dots replaced with underscore, so for instance as <tt>jfk_example_myexample</tt>.
     * We don't use here a numeric discriminator in the name because it can be useful to find out if the private refernce
     * is already present is superclasses.
     * @param className the name of the class to use
     * @return the reference name
     */
    public static synchronized String computePrivateTargetReferenceName( String className ){
	// compose the name
	StringBuffer buffer = new StringBuffer(50);
	
	normalizeClassName(className, buffer);
	
	if( buffer.length() == 0 )
	    buffer.append("_unknwon");
	   
	
	

	
	// all done
	return buffer.toString();
    }


    /**
     * Changes the '.' in a class name with a '_'.
     * @param className the name of the class
     * @param buffer the buffer to use
     */
    private final static void normalizeClassName(String className, StringBuffer buffer) {
	StringTokenizer tokenizer = new StringTokenizer( className, "." );
	while( tokenizer.hasMoreElements() ){
	    buffer.append( "_" );
	    buffer.append( tokenizer.nextToken() );
	}
    }
    

   
    
    
    /**
     * Provide a standard name for the set target object method.
     * @return the standard name of the method
     */
    public static synchronized String getSetTargetMethodName(){
	return "__jfk_setTargetObject";
    }
    
    
    /**
     * Computes the name of a closure class to construct.
     * @return the closure name
     */
    public static synchronized String computeClosureClassName(){
	return "Closure_" + (++counter);
    }
    
    /**
     * Gets a class name for a closure class to be defined on the fly. The class name does not represent
     * an existing class.
     * @return the class name to implement
     */
    public static synchronized String getClosureClassName(){
	return "jfk.function.ClosureTargetObject_" + (++counter);
    }
    
    
    /**
     * Provides a name for the subclass delegate class name.
     * @param originalName the name of the superclass, the original class name
     * @return the name to use when defining the subclass
     */
    public static synchronized String getDelegateClassName( String originalName ){
	return originalName + "_impl_" +  (++counter);
    }


    /**
     * Creates the name of a private list to keep functions.
     * @param name the name of a class
     * @return the name to use as a private reference for the list of functions
     */
    public static synchronized String computePrivateListName(String name) {
	if( name == null || name.length() < 0 )
	    throw new IllegalArgumentException( "Empty or invalid name!" );
	
	StringBuffer buffer = new StringBuffer(50);
	normalizeClassName(name, buffer);
	buffer.append("_privateFunctionList_");
	buffer.append( ++counter );
	return buffer.toString();
    }
    
    
  
    
    
}
