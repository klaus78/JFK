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
package jfk.core;

import jfk.function.IFunction;
import jfk.function.IFunctionBuilder;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * This is a centralized service object that can provide several utility methods
 * to access the main components of JFK.
 * 
 * @author Luca Ferrari - cat4hire (at) users.sourceforge.net
 *
 */
public class JFK {

    /**
     * The spring xml bean factory, used to instantiate the beans.
     */
    private static XmlBeanFactory xmlBeanFactory = null;
    
    static{
	 // configure the spring resource in order to get it available for the
        // beans configurations. Please note that the configuration file must be
	// in the classpath.
        String springConfigurationPath = "spring.xml";
        ClassPathResource classPathResource = new ClassPathResource( springConfigurationPath );
        xmlBeanFactory = new XmlBeanFactory(classPathResource);
    }
    
    
    /**
     * Provides the default function builder to use in the function building process.
     * A function builder is an object that can provide a function for every object
     * and every function identifier.
     * @return the function builder to use in the current system configuration
     */
    public static synchronized IFunctionBuilder getFunctionBuilder(){
	return (IFunctionBuilder) xmlBeanFactory.getBean( IFunctionBuilder.class.getSimpleName() );
    }
    

}
