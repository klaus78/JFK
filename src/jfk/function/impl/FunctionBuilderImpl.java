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
package jfk.function.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jfk.core.JFK;
import jfk.function.Function;
import jfk.function.IFunction;
import jfk.function.IFunctionBuilder;
import jfk.function.JFKException;
import jfk.function.classloaders.IFunctionBinder;
import jfk.function.classloaders.IFunctionClassDefiner;
import jfk.function.delegates.Connect;
import jfk.function.delegates.Delegate;
import jfk.function.delegates.IDelegate;
import jfk.function.exception.CannotBindFunctionException;

/**
 * The default implementation of a method builder. This implementation simply
 * invokes the method on the target object.
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class FunctionBuilderImpl implements IFunctionBuilder {

    
    /**
     * A cache for already defined classes. Each function is mapped with a key
     * that corresponds to the class name the function will be bound to compound
     * by the name of the function annotation.
     */
    private Map<String, IFunction> cache = new HashMap<String, IFunction>();
    
    
    /* (non-Javadoc)
     * @see jfk.function.IFunctionBuilder#bindFunction(java.lang.Object, jfk.function.Function)
     */
    @Override
    public IFunction bindFunction(Object target, String name )
									  throws CannotBindFunctionException {
	// check arguments
	if( target == null || name == null || name.length() < 0 )
	    throw new CannotBindFunctionException("Cannot bind the method call to a function on " + target.getClass() + " for identifier " + name);
	
	// create a key for the cache entry
	String cacheKey = target.getClass().getName() + "_" + name;
	
	// check if the function is already in the cache
	if( this.cache.containsKey(cacheKey) )
	    return this.cache.get(cacheKey);
	
	
	// now iterate on each public method to see if one of the target object has the annotation
	// of a function with the specified name, and in such case prepare to get the function object
	for( Method currentMethod : target.getClass().getMethods() )
	    if( currentMethod.isAnnotationPresent( Function.class ) ){
		Function functionAnnotation = currentMethod.getAnnotation( Function.class );
		
		
		
		if( functionAnnotation.name().equals( name ) ){
		    // ok, this method must be mapped as a function!!    
		    
		    try {
			// get a new function definer and build the function
			IFunctionClassDefiner definer = (IFunctionClassDefiner) JFK.getBean( IFunctionClassDefiner.class );
			Class functionClass = definer.getIFunctionClassDefinition(target, currentMethod);
			
			// now create the instance
			IFunction function = (IFunction) functionClass.newInstance();
			
			// set the target object
			((IFunctionBinder) function ).setTargetObject(target);
			
			// all done
			this.cache.put(cacheKey, function);
			return function;
			
		    } catch (Exception e){
			throw new CannotBindFunctionException("Cannot create the function object ", e);
		    }
		}
		    
	    }
	
	
	// if here there is no method to map as a function
	throw new CannotBindFunctionException("No method found to be mapped as " + name + " on " + target.getClass() );
	
    }


    
    
    /**
     * Creates the binding for a delegate, that is on a delegate annotation.
     * @param target
     * @param name
     * @return
     * @throws CannotBindFunctionException
     */
    public IFunction bindDelegateFunction(IDelegate target, String name )  throws CannotBindFunctionException {
    
	// check arguments
	if( target == null || name == null || name.length() < 0 )
	    throw new CannotBindFunctionException("Cannot bind the method call to a function on " + target.getClass() + " for identifier " + name);
	
	
	// now iterate on each public method to see if one of the target object has the annotation
	// of a function with the specified name, and in such case prepare to get the function object
	for( Method currentMethod : target.getClass().getMethods() )
	    if( currentMethod.isAnnotationPresent( Connect.class ) ){
		Connect functionAnnotation = currentMethod.getAnnotation( Connect.class );
		
		
		
		if( functionAnnotation.name().equals( name ) ){
		    // ok, this method must be mapped as a function!!    
		    
		    try {
			// get a new function definer and build the function
			IFunctionClassDefiner definer = (IFunctionClassDefiner) JFK.getBean( IFunctionClassDefiner.class );
			Class functionClass = definer.getIFunctionClassDefinition(target, currentMethod);
			
			// now create the instance
			IFunction function = (IFunction) functionClass.newInstance();
			
			// set the target object
			((IFunctionBinder) function ).setTargetObject(target);
			
			// all done
			return function;
			
		    } catch (Exception e){
			e.printStackTrace();
			throw new CannotBindFunctionException("Cannot create the function object ", e);
		    }
		}
		    
	    }
	
	
	// if here there is no method to map as a function
	throw new CannotBindFunctionException("No method found to be mapped as " + name + " on " + target.getClass() );
	
	
    }
    
    
}
