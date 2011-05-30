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
package jfk.test;

import jfk.core.JFK;
import jfk.function.IClosureBuilder;
import jfk.function.IFunction;
import jfk.function.IFunctionBuilder;
import jfk.function.delegates.Connect;
import jfk.function.delegates.Delegate;
import jfk.function.delegates.IDelegatable;
import jfk.function.delegates.IDelegate;
import jfk.function.delegates.IDelegateManager;
import jfk.function.exception.BadArityException;
import jfk.function.exception.BadParameterTypeException;
import jfk.function.exception.CannotBindFunctionException;
import jfk.function.exception.ClosureException;
import jfk.function.exception.delegates.AlreadyImplementedDelegateException;
import jfk.function.exception.delegates.CannotConnectDelegateException;

/**
 * An example to see how the api works.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public class ExampleMain {

    /**
     * @param args
     * @throws CannotBindFunctionException 
     * @throws BadParameterTypeException 
     * @throws BadArityException 
     * @throws ClosureException 
     * @throws AlreadyImplementedDelegateException 
     * @throws CannotConnectDelegateException 
     */
    public static void main(String[] args) throws CannotBindFunctionException, BadArityException, BadParameterTypeException, ClosureException, CannotConnectDelegateException, AlreadyImplementedDelegateException {
	
	
	// 1) create a pointer to a method into an object
	//    		here the dummy object has a couple of methods that have
	//		been annotated with Function
	DummyClass dummy = new DummyClass();
	
	// get a function builder and a pointer to the "hello" method on the dummy instance
	IFunctionBuilder builder = JFK.getFunctionBuilder();
	IFunction function = builder.bindFunction( dummy, "hello" );
	
	// execute the function
	String result = (String) function.executeCall( null );
	
	
	// get a pointer to another function
	// the "double" function returns a computation of a double passed on the stack
	IFunction function2 = builder.bindFunction(dummy, "double" );
	dummy = null;			// note that the dummy object is no more used!!!
	Double d1 = new Double(10.5);
	Double d2 = (Double) function2.executeCall( new Object[]{ d1 } );
	System.out.println("Computation of the double function returned " + d2);
	// it prints
	// Computation of the double function returned 21.0
	
	
	// 2) create a closure
	//		create a method and use it without defining a class/object
	IClosureBuilder closureBuilder = JFK.getClosureBuilder();
	IFunction closure = closureBuilder.buildClosure("public String concat(String s, Integer i){ return s + i.intValue(); }");
	
	// now use the closure, please note that there is no object/class created here!
	String closureResult = (String) closure.executeCall( new Object[]{ "Hello JFK!", new Integer(1234) } );
	System.out.println("Closure result: " + closureResult);
	// it prints
	// Closure result: Hello JFK!1234
	
	
	
	// 3) implement dynamic delegates
	//	consider the EventGenerator class, that has an abstract method that notifies an event thru 
	//		@Delegate( name="event", allowMultiple = true )
	//		public abstract void notifyEvent(String event);
	//		
	// 	the EventConsumer class has a method that matches the signature of the generator method (only the name
	//	changes) and that is annotated with @Connect( name="event" )
	//
	//		@Connect( name="event" )
	//      	public void consumeEvent(String event){ ..}
	//
	//	What happens at run-time is that the notifyEvent method is implemented as to call directly
	//	the  consumeEvent method.
	
	IDelegateManager manager = JFK.getDelegateManager();
	IDelegatable consumer = (IDelegatable) manager.createAndBind( EventGenerator.class, new EventConsumer() );
	// now the delegate will invoke the abstract method, that has been defined at run-time to match the
	// consumer method in the EventConsumer object
	((EventGenerator) consumer).doEvent();
	// it prints
	// ********** Cosuming event Event 0				<- from EventConsumer
	// **********>>>>>>> Cosuming event Event 0			<- from EventConsumer2
	// ....
	// ********** Cosuming event Event 9				<- from EventConsumer
	// **********>>>>>>> Cosuming event Event 9			<- from EventConsumer2
	
	
	// now it is possible to add another consumer to the event generator, since it allows a multiple
	// connection. To do this, we can add another delegate to the instance
	manager.addDelegate(consumer, new EventConsumer2() );
	// now the delegate will invoke the abstract method, that has been defined at run-time to match the
	// consumer method in the EventConsumer object
	((EventGenerator) consumer).doEvent();
	// it prints
	// ********** Cosuming event Event 0
	// ....
	// ********** Cosuming event Event 9
    }

}
