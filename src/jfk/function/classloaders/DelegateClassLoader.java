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


import java.lang.reflect.Method;
import java.security.SecureClassLoader;
import java.util.LinkedList;
import java.util.List;

import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;

/**
 * The delegate class loaders, that implements abstract methods delegate.
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class DelegateClassLoader extends SecureClassLoader implements
	IDelegateConnector {

    
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
    }
    
    
    /**
     * A list of the connections to do when defining the implementation of the delegate.
     */
    private List<ConnectionData> connectionsToDo = new LinkedList<ConnectionData>();
    
    
    /**
     * The status of the class loader.
     */
    private ClassLoaderStatus status = ClassLoaderStatus.READY;
    
    
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
	// TODO Auto-generated method stub
	return null;
    }

}
