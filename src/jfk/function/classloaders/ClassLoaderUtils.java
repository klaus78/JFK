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
	
	StringBuffer buffer = new StringBuffer(50);
	
	// the name of the class to build will be the composition of the simple name of the
	// target class name and the name of the annotation
	buffer.append( targetClass.getSimpleName() );
	buffer.append( "_" );
	buffer.append( functionAnnotation.name() );
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
	
	// compose the name
	StringBuffer buffer = new StringBuffer(50);
	
	StringTokenizer tokenizer = new StringTokenizer( targetClass.getName(), "." );
	while( tokenizer.hasMoreElements() ){
	    buffer.append( "_" );
	    buffer.append( tokenizer.nextToken() );
	}
	
	if( buffer.length() == 0 )
	    buffer.append("_unknwon");
	   
	
	

	
	// all done
	return buffer.toString();
    }
    
    
    /**
     * Provide a standard name for the set target object method.
     * @return the standard name of the method
     */
    public static synchronized String getSetTargetMethodName(){
	return "__jfk_setTargetObject";
    }
    
}
