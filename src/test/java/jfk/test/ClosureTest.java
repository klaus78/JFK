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
import jfk.function.IClosure;
import jfk.function.IClosureBuilder;
import jfk.function.exception.BadArityException;
import jfk.function.exception.BadParameterTypeException;
import jfk.function.exception.ClosureException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A test for the closure builders.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public class ClosureTest {

    /**
     * @throws java.lang.Exception
     */
    // @Before
    public void setUp() throws Exception {
    }
    
    
    // @Test
    public void testClosureBuilder() throws ClosureException, BadArityException, BadParameterTypeException{
	// get a new closure builder
	IClosureBuilder builder = (IClosureBuilder) JFK.getBean( IClosureBuilder.class );
	
	// the builder cannot be null
	if( builder == null )
	    fail("Cannot get a builder for the configuration");
	
	// compile a code
	StringBuffer code = new StringBuffer(1000);
	code.append( "public java.lang.String hello(){ System.out.println(\"Hello Closure World\"); return \"Hello Closure World!\"; }" );
	IClosure closure = builder.buildClosure( code.toString() );
	
	if( closure == null )
	    fail("Didn't get a closure!");
	
	// execute the closure
	closure.executeCall(null);

	// a new closure
	code = new StringBuffer(1000);
	code.append( " public void hello2(){ System.out.println(\"*** Hello from a void method\");}");
	closure = builder.buildClosure( code.toString() );

	if( closure == null )
	    fail("Didn't get a closure!");

	// execute the closure
	closure.executeCall(null);
	
	
	// a new closure
	code = new StringBuffer(1000);
	code.append( "public String concat(String s, Integer i){ return s + i.intValue(); }");
	closure = builder.buildClosure( code.toString() );
	String s = "Hello";
	int i = 10;
	String result = (String) closure.executeCall( new Object[]{ s, new Integer(i) } );

	if( ! result.equals( s + i ) )
	    fail("Closure result incorrect!");
	
	// another closure
	code = new StringBuffer(1000);
	code.append( "public Integer loop( Integer ir ){ for(int i =0; i< ir.intValue(); i++) System.out.println(\"Interaction \" + i); return ir; } ");
	closure = builder.buildClosure( code.toString() );
	
	Integer k = new Integer( 30 );
	Integer ir = (Integer) closure.executeCall( new Object[]{ k } );
	
	if( ir.intValue() != k.intValue() )
	    fail("Closure return mismatch!");
	
    }

}
