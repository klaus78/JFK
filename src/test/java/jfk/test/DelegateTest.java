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
package jfk.test;


import java.lang.reflect.Method;

import jfk.core.JFK;
import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.delegates.IDelegateManager;
import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;
import jfk.function.exception.delegates.DelegateException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A test for the delegate subsystem.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public class DelegateTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    
    @Test
    public void testDelegateManager() throws CannotConnectDelegateException, AlreadyImplementedDelegateException{
	
	// get a new delegate manager
	IDelegateManager manager = JFK.getDelegateManager();
	
	// the delegate manager cannot be null
	if( manager == null )
	    fail("Cannot obtain a delegate manager");
	
	
	// this should fail, since the bad event consumer has not the right annotation
	try {
	    manager.createAndBind( EventGenerator.class, new BadEventConsumer() );
	    fail("Creating a bad delegate definition????");
	} catch (CannotConnectDelegateException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (AlreadyImplementedDelegateException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	
	// this should pass
	IDelegatable consumer = (IDelegatable) manager.createAndBind( EventGenerator.class, new EventConsumer() );
	
	System.out.println("Class instance " + consumer.getClass() );
	for( Method m : consumer.getClass().getDeclaredMethods() )
	    System.out.println("Method -> " + m.getName());
	
	((EventGenerator) consumer).notifyEvent("Hello Event!");
	
	
	// this should fail: duplicating the same delegate
	try{
	    IDelegatable consumer2 = (IDelegatable) manager.createAndBind( EventGenerator.class, new EventConsumer() );
	    fail("Allowing duplicated delegates definition within a forgot!");
	}catch(DelegateException e){}
	
	// this should pass
	manager.forgetDelegatable( EventGenerator.class );
	consumer = (IDelegatable) manager.createAndBind( EventGenerator.class, new EventConsumer() );
	((EventGenerator) consumer).notifyEvent("Hello Event2!");
	
	// add a new delegate
	manager.addDelegate(consumer,  new EventConsumer2() );
	((EventGenerator) consumer).notifyEvent("Hello Event3!");
	
	
    }
    
}
