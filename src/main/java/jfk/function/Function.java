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
package jfk.function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a class/object method as available for method pointers.
 * Each method annotated with this annotation can be attached to a method pointer and used 
 * as delegates.
 * 
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
@Retention( RetentionPolicy.RUNTIME )		// the annotation must be present at run-time!
@Target( ElementType.METHOD )			// the annotation can be applied to a method!
public @interface Function {

    /**
     * The name of the function is used as an identifier to search for and bind a function pointer
     * to a function implementation. The name should be unique within the same object/hierachy of objects.
     * 
     * @return the name associated to this function, used as a tag for searching for the function to bind
     * to a function pointer
     */
    public String name() default "";
}
