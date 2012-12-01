/* 
 * JFK - Java Function Kernel
 *
 * This project provides a run-time framework to achieve functional programming
 * in Java. The idea is to have the capability to get something similar to function pointers
 * and C# delegates in Java, doing all the bindings and reference resolution at run-time being able,
 * at the same time, being able to compile the program using a function-first-class entity and abstraction.
 *
 * Copyright (C) Luca Ferrari 2010-2012 - fluca1978 (at) gmail.com
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

import jfk.function.IClosure;
import jfk.function.IClosureBuilder;
import jfk.function.classloaders.ClosureClassLoader;
import jfk.function.classloaders.IFunctionBinder;
import jfk.function.exception.ClosureException;
import jfk.function.exception.TargetBindException;

/**
 * The default closure builder.
 * Creates a closure using a specific class loader.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public class ClosureBuilderImpl implements IClosureBuilder {

    /**
     * 
     */
    public ClosureBuilderImpl() {
	// TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see jfk.function.IClosureBuilder#buildClosure(java.lang.String)
     */
    @Override
    public IClosure buildClosure(final String code)
    throws  ClosureException {
	try {
	    final ClosureClassLoader loader = new ClosureClassLoader();
	    loader.setClosureCode(code);
	    final IClosure closure = loader.getClosure();

	    ((IFunctionBinder) closure).setTargetObject( loader.getTargetInstance() );
	    return closure;
	} catch (final TargetBindException e) {
	    throw new ClosureException("Cannot bind closure", e);
	}


    }

}
