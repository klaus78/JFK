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

import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;

/**
 * The main interface for a delegate builder.
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public interface IDelegateConnector {

    /**
     * A method to prepare the binding between two methods.
     * @param sourceMethod the source method (the abstract one)
     * @param targetMethod the target method (the one already implemented)
     * @param targetInstance the object that defines the target method
     * @return true if the bind has been registered
     */
    public boolean prepareConnection( Method sourceMethod, Method targetMethod, IDelegate targetInstance );
    
    
    /**
     * Creates the delegate implementation.
     * @return the delegate implemented
     * @throws CannotConnectDelegateException
     * @throws AlreadyImplementedDelegateException
     */
    public IDelegatable createDelegate() throws CannotConnectDelegateException, AlreadyImplementedDelegateException;
}
