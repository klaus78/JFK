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
package jfk.function;

import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.exception.CannotBindFunctionException;

/**
 * This is the main interface for building a new function given the specified
 * target object and the meta-information about the function itself.
 * 
 * Implementations of this interface must provide a protocol and a policy to build
 * the method/function binding, for instance using reflection, direct access, etc.
 * 
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public interface IFunctionBuilder {

    /**
     * This method defines how a function is bound to a specific object/class method.
     * 
     * @param target the object or the class on which the method will be invoked
     * @param name the identifier of the Function annotation that is used to mark the method
     * as a function
     * @return the function object to use as a first class entity
     */
    public IFunction bindFunction( Object target, String name ) throws CannotBindFunctionException;
    
    
    /**
     * This method is used to create the function for a delegate, that is a method that is marked
     * thru the Connect annotation.
     * @param target the delegate object
     * @param name the name of the target to search for
     * @return
     * @throws CannotBindFunctionException
     */
    public IFunction bindDelegateFunction( IDelegate target, String name ) throws CannotBindFunctionException;
}
