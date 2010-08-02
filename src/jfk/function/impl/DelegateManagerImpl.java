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
package jfk.function.impl;

import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.delegates.IDelegateManager;
import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;

/**
 * The default delegate manager implementation
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class DelegateManagerImpl implements IDelegateManager {

    /* (non-Javadoc)
     * @see jfk.function.delegates.IDelegateManager#createAndBind(java.lang.Class, jfk.function.delegates.IDelegate)
     */
    @Override
    public IDelegatable createAndBind(
				      Class delegatableClass,
				      IDelegate delegateTarget)
							       throws CannotConnectDelegateException,
							       AlreadyImplementedDelegateException {
	

	return new IDelegatable() {
	    
	    @Override
	    public boolean removeDelegate(IDelegate delegateToRemove) {
		// TODO Auto-generated method stub
		return false;
	    }
	    
	    @Override
	    public boolean addDelegate(IDelegate delegateToAdd) {
		// TODO Auto-generated method stub
		return false;
	    }
	};
    }

    /* (non-Javadoc)
     * @see jfk.function.delegates.IDelegateManager#addDelegate(jfk.function.delegates.IDelegatable, jfk.function.delegates.IDelegate)
     */
    @Override
    public boolean addDelegate(IDelegatable source, IDelegate destination) {
	// TODO Auto-generated method stub
	return false;
    }

    /* (non-Javadoc)
     * @see jfk.function.delegates.IDelegateManager#removeDelegate(jfk.function.delegates.IDelegatable, jfk.function.delegates.IDelegate)
     */
    @Override
    public boolean removeDelegate(IDelegatable source, IDelegate destination) {
	// TODO Auto-generated method stub
	return false;
    }

}
