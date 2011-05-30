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
package jfk.function.exception;

import jfk.function.JFKException;

/**
 * Notifies a function executor that a wrong parameter has been specified.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public class BadParameterTypeException extends JFKException {
    
    /**
     * The required parameter type.
     */
    private Class requiredType = null;
    
    
    /**
     * The provided parameter type.
     */
    private Class suppliedType = null;
    

    /**
     * 
     */
    public BadParameterTypeException() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public BadParameterTypeException(String message, Throwable cause) {
	super(message, cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public BadParameterTypeException(String message) {
	super(message);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public BadParameterTypeException(Throwable cause) {
	super(cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * A method to access directly the requiredType
     * within this object instance.
     * @return the requiredType value
     */
    public synchronized final Class getRequiredType() {
        // return the value of the this.requiredType field
        return this.requiredType;
    }

    /**
     * A method to set the value of the requiredType
     * field within this object instance.
     * @param requiredType the requiredType to set
     */
    public synchronized final void setRequiredType(Class requiredType) {
        // set the value of the this.requiredType field
        this.requiredType = requiredType;
    }

    /**
     * A method to access directly the suppliedType
     * within this object instance.
     * @return the suppliedType value
     */
    public synchronized final Class getSuppliedType() {
        // return the value of the this.suppliedType field
        return this.suppliedType;
    }

    /**
     * A method to set the value of the suppliedType
     * field within this object instance.
     * @param suppliedType the suppliedType to set
     */
    public synchronized final void setSuppliedType(Class suppliedType) {
        // set the value of the this.suppliedType field
        this.suppliedType = suppliedType;
    }
    
    
    

}
