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

/**
 * An interface used to mark a class as a possible target for delegates, that means the class implementing
 * this interface can be used as a target to which delegates can be attached.
 * This interfaces only defines the methods to add/remove a new delegate, such methods must remain abstract since they
 * will be implemented at run-time. Making the class abstract is good, since the rule is that every delegatable method
 * must be kept abstract.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public interface IDelegatable {

    /**
     * Adds the target delegate to this object. When attaching a new delegate, the delegate will be "scanned" to see
     * which methods must be attached to which delegatable methods of this object.
     * @param delegateToAdd the delegate target to add
     * @param name the name of the connection to establish (i.e., the name of the connection annotation)
     * @return true if the delegate has been added, false if the delegate cannot be add (e.g., it is already attached
     * or it does not include any method to attach)
     */
    public boolean addDelegate( IDelegate delegateToAdd, String name );


    /**
     * Removes a delegate from this object.
     * @param delegateToRemove the delegate to remove from this object
     * @return true if the delegate has been removed, false otherwise
     */
    public boolean removeDelegate( IDelegate delegateToRemove );


}
