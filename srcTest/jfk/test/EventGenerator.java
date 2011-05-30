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

import jfk.function.delegates.Delegate;
import jfk.function.delegates.IDelegatable;

/**
 * An event generator to test the delegates.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public abstract class EventGenerator implements IDelegatable{

    
    public void doEvent(){
	for( int i = 0; i < 10; i++ )
	    this.notifyEvent( "Event " + i );
    }
    
    
    @Delegate( name="event", allowMultiple = true )
    public abstract void notifyEvent(String event);
    
}
