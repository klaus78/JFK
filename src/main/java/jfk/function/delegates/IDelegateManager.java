/* 
 * JFK - Java Function Kernel
 *
 * This project provides a run-time framework to achieve functional programming
 * in Java. The idea is to have the capability to get something similar to function pointers
 * and C# delegates in Java, doing all the bindings and reference resolution at run-time being able,
 * at the same time, being able to compile the program using a function-first-class entity and abstraction.
 *
 * Copyright (C) Luca Ferrari 2010-2011 - fluca1978 (at) gmail.com
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
package jfk.function.delegates;

import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;

/**
 * A delegate manager is the main object that can build, bind and unbind delegates to instances and methods.
 * Use this interface when you want to connect a new instance to a delegate.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public interface IDelegateManager {

    /**
     * Connects a new delegate to a delegatable source.
     * @param source the source with the delegatable methods
     * @param destination the implementation of the methods
     * @return true if the delegate has been added
     */
    public boolean addDelegate( IDelegatable source, IDelegate destination );


    /**
     * The main method to create and bind a delegate to a specific target. 
     * The delegate has a set of abstract methods that must be implemented at run-time and connected to 
     * counterparts method on the delegate target.
     * @param delegatableClass the class the delegate belongs to
     * @param delegateTarget the target that implements the methods to use
     * @return the instance of the delegate class with the implemented methods
     * @throws CannotConnectDelegateException if the methods cannot be bound
     * @throws AlreadyImplementedDelegateException if the delegate class as already an implementation of the methods
     */
    public IDelegatable createAndBind( Class delegatableClass, IDelegate delegateTarget ) throws CannotConnectDelegateException, AlreadyImplementedDelegateException;


    /**
     * A method to forget a delegatable object, that is to allow a new creation of the same delegate with different binds.
     * @param source the source to delete
     */
    public void forgetDelegatable( Class source );


    /**
     * Removes the delegate from the delegatable source.
     * @param source the delegatable source
     * @param destination the implementation to remove
     * @return true if the delegate has been removed
     */
    public boolean removeDelegate( IDelegatable source, IDelegate destination );
}
