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

import jfk.function.exception.BadArityException;
import jfk.function.exception.BadParameterTypeException;

/**
 * This interface represents the main concept of the functional programming as provided by JFK.
 * The idea is that this interface can wrap a single function, and therefore acts as "function pointer"
 * for a defined method somewhere else.
 * 
 * Take into account that this interface as a single method, that is a variadic method in order
 * to be adaptable to any method that is connected to instances of this interface.
 * 
 * Please note that this function will never be implemented manually, but it is thought to be implemented
 * by a dynamic instantion mechanism, so you don't have to worry about how the functional mechanism work, you have
 * to only use this object to invoke the method connected.
 * 
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public interface IFunction {
    
    /**
     * This is the method to call in order to execute the method call this object points to.
     * Consider that instances of this interface are like "function pointer", so calling this method
     * means that the method executed will be the one this instance points to, so a target method somewhere
     * else.
     * 
     * Please note that this method is as much generic as possible, so accepts a variable number of parameters
     * and returns an object. You have to cast arguments to their real nature in order to take advantage of them.
     * 
     * @param args the list of arguments to pass to the target method call
     * @return an object representing the (generic) result of the target method call
     * @throws BadArityException if the number of parameters is wrong (evaluated at run-time)
     * @throws BadParameterTypeException if one of the parameters is of the wrong type (evaluated at run-time)
     */
    public Object executeCall( Object...args ) throws BadArityException, BadParameterTypeException;

}
