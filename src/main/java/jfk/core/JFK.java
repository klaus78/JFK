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
package jfk.core;

import jfk.function.IClosureBuilder;
import jfk.function.IFunctionBuilder;
import jfk.function.classloaders.IDelegateConnector;
import jfk.function.delegates.IDelegateManager;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * This is a centralized service object that can provide several utility methods
 * to access the main components of JFK.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
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
	final String springConfigurationPath = "jfk.spring-beans.xml";
	final ClassPathResource classPathResource = new ClassPathResource( springConfigurationPath );
	xmlBeanFactory = new XmlBeanFactory(classPathResource);
    }


    /**
     * Gets a bean (pluggable and configurable) for the system.
     * @param clazz the class (interface) to get the bean for, its simple name is used in
     * the configuration.
     * @return the bean from the configuration of this system
     */
    public static synchronized Object getBean( final Class clazz ){
	return xmlBeanFactory.getBean( clazz.getSimpleName() );
    }


    /**
     * Provides the default closure builder for this configuration.
     * @return the closure builder for the configuration
     */
    public synchronized static IClosureBuilder getClosureBuilder() {
	return xmlBeanFactory.getBean( IClosureBuilder.class );
    }


    /**
     * Provides the default delegate connector, that is the special class loader used
     * to implement a delegate.
     * @return the delegate connector
     */
    public static synchronized IDelegateConnector getDelegateConnector() {
	return xmlBeanFactory.getBean( IDelegateConnector.class );
    }


    /**
     * Provides the default delegate manager to use in this configuration.
     * @return the delegate manager to use
     */
    public static synchronized IDelegateManager getDelegateManager(){
	return xmlBeanFactory.getBean( IDelegateManager.class );
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
