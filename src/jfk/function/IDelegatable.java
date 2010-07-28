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

import jfk.function.exception.FunctionNotFoundException;

/**
 * This interfaces is used to mark (and provide run-time and compile-time services) a class
 * as having the capability to accept function pointers.
 * 
 * When applied to a class, this interface adds a few service methods that can be used to instantiate
 * and get a function pointer ({@link IFunction}). Such method pointers will be used to execute method calls
 * on the class/instance this interface is applied to.
 * 
 *  <B>Please take into account that applying this interface to a class makes your class abstract, and you have not to
 *  implement the methods defined here, since they will be implemented at run-time when your class is instantiated.
 *  </B>
 *  
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public interface IDelegatable {

    /**
     * The base service method to get a function pointer to a method of the object instance that implements
     * this interface.
     * Thanks to this method you can get easily a function pointer to a method of this object.
     * @param name the name of the function, that name identifies the function in a unique way within this object
     * instance
     * @return the function pointer object connected to the specified method within the instance object that implements
     * this interface
     * @throws FunctionNotFoundException if the method specified does not exist or does not allow a function pointer
     */
    public IFunction getDelegate( String name ) throws FunctionNotFoundException;
    
    
    /**
     * Provides an array of all the available delegates (i.e., method pointers) for the object instance that
     * implements this inteface.
     * If the object does not allow any method pointer, than this method returns an empty array.
     * @return the array of the method pointers available on this instance, or an emtpy array if no methods allow
     * a method pointer
     */
    public IFunction[] getDelegates();
}
