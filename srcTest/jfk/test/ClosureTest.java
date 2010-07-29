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
package jfk.test;


import jfk.core.JFK;
import jfk.function.IClosureBuilder;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A test for the closure builders.
 * 
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class ClosureTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }
    
    
    @Test
    public void testClosureBuilder(){
	// get a new closure builder
	IClosureBuilder builder = (IClosureBuilder) JFK.getBean( IClosureBuilder.class );
	
	// the builder cannot be null
	if( builder == null )
	    fail("Cannot get a builder for the configuration");
	
	
    }

}
