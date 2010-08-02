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
import jfk.function.classloaders.IDelegateConnector;
import jfk.function.delegates.Connect;
import jfk.function.delegates.Delegate;
import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.delegates.IDelegateManager;
import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;

/**
 * The default delegate manager implementation
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class DelegateManagerImpl implements IDelegateManager {
    
    

    /**
     * A map with the already built delegates, so that they can be reused.
     */
    private Map<Class, IDelegatable> delegatles = new HashMap<Class, IDelegatable>();
    

    

    /* (non-Javadoc)
     * @see jfk.function.delegates.IDelegateManager#createAndBind(java.lang.Class, jfk.function.delegates.IDelegate)
     */
    @Override
    public synchronized IDelegatable createAndBind(
				      Class delegatableClass,
				      IDelegate delegateTarget)
							       throws CannotConnectDelegateException,
							       AlreadyImplementedDelegateException {
	

	// check if the delegate has been already defined
	if( this.delegatles.containsKey(delegatableClass) )
	    throw new AlreadyImplementedDelegateException("Cannot redefine an already implemented delegate!\nHint: forgot the delegate");
	
	
	// the first thing to do is to check if the class has at least one delegatable method
	boolean found = false;
	for( Method delegatebleMethod : delegatableClass.getDeclaredMethods() ){
	    // is the method annotated?
	    if( delegatebleMethod.isAnnotationPresent( Delegate.class  ) ){
		found = true;
		break;
	    }
	}
	
	// if not found at least a method, throw an exception
	if( ! found )
	    throw new CannotConnectDelegateException( "No delegate method found!" );
	
	
	// now check if the target object has at least one connect annotation
	found = false;
	for( Method connectMethod : delegateTarget.getClass().getDeclaredMethods() )
	    if( connectMethod.isAnnotationPresent( Connect.class ) ){
		found = true;
		break;
	    }
	
	
	// if not found at least a method, throw an exception
	if( ! found )
	    throw new CannotConnectDelegateException( "No connect method found!" );
	
	
	// get a new delegate builder
	IDelegateConnector connector = JFK.getDelegateConnector();
	// initialize the connector
	connector.setDelegatableSource(delegatableClass);
	
	
	
	// if here both the target and the delegatable class have annotated methods, now I must check for
	// each method in the delegate that a method with the same name and signature is in
	// the target
	for( Method delegatableMethod : delegatableClass.getDeclaredMethods() )
	    if( delegatableMethod.isAnnotationPresent( Delegate.class ) ){
		// get the annotation and its parameters
		Delegate delegateAnnotation = delegatableMethod.getAnnotation( Delegate.class );
		String delegateName   = delegateAnnotation.name();
		boolean allowMultiple = delegateAnnotation.allowMultiple();
		
		// now iterate on the target to see if a method with the counterpart connect annotation
		// can be found
		found = false;
		for( Method connectMethod : delegateTarget.getClass().getDeclaredMethods() )
		    if( connectMethod.isAnnotationPresent( Connect.class ) ){
			// get the annotation and check the parameters
			Connect connectAnnotation = connectMethod.getAnnotation( Connect.class );
			if( connectAnnotation.name().equals(delegateName) ){
			    found = true;
			    
			    // add this method connection
			    connector.prepareConnection(delegatableMethod, connectMethod, delegateTarget);
			    
			}
		    }
		
		
		// if here and the connect method has not been found, throw an exception
		if( ! found )
		    throw new CannotConnectDelegateException("Delegate method " + delegatableMethod.getName() + " has not connection to any method!");
	    }
	
	
	
	// if here both the delegate and the connecter can be bound, so I need to proceed to
	// the implementation of the methods
	IDelegatable instance = connector.createDelegate();
	this.delegatles.put(delegatableClass, instance);
	return instance;
    }

    /* (non-Javadoc)
     * @see jfk.function.delegates.IDelegateManager#addDelegate(jfk.function.delegates.IDelegatable, jfk.function.delegates.IDelegate)
     */
    @Override
    public boolean addDelegate(IDelegatable source, IDelegate destination) {
	// TODO Auto-generated method stub
	return false;
    }

    /* (non-Javadoc)
     * @see jfk.function.delegates.IDelegateManager#removeDelegate(jfk.function.delegates.IDelegatable, jfk.function.delegates.IDelegate)
     */
    @Override
    public boolean removeDelegate(IDelegatable source, IDelegate destination) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public synchronized void forgetDelegatable(Class source) {
	this.delegatles.remove(source);
	
    }

}
