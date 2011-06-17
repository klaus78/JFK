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
 * An exception used to notify the function executor that a wrong number of parameters have been used.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public class BadArityException extends JFKException {


    /**
     * The required arity.
     */
    private int requiredArity = 0;

    /**
     * The supplied arity.
     */
    private int suppliedArity = 0;

    /**
     * 
     */
    public BadArityException() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public BadArityException(final String message) {
	super(message);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public BadArityException(final String message, final Throwable cause) {
	super(message, cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public BadArityException(final Throwable cause) {
	super(cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * A method to access directly the requiredArity
     * within this object instance.
     * @return the requiredArity value
     */
    public synchronized final int getRequiredArity() {
	// return the value of the this.requiredArity field
	return requiredArity;
    }

    /**
     * A method to access directly the suppliedArity
     * within this object instance.
     * @return the suppliedArity value
     */
    public synchronized final int getSuppliedArity() {
	// return the value of the this.suppliedArity field
	return suppliedArity;
    }

    /**
     * A method to set the value of the requiredArity
     * field within this object instance.
     * @param requiredArity the requiredArity to set
     */
    public synchronized final void setRequiredArity(final int requiredArity) {
	// set the value of the this.requiredArity field
	this.requiredArity = requiredArity;
    }

    /**
     * A method to set the value of the suppliedArity
     * field within this object instance.
     * @param suppliedArity the suppliedArity to set
     */
    public synchronized final void setSuppliedArity(final int suppliedArity) {
	// set the value of the this.suppliedArity field
	this.suppliedArity = suppliedArity;
    }



}
