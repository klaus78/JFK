package jfk.function.classloaders;

import java.lang.reflect.Method;

import jfk.function.JFKException;

/**
 * An interface that abstracts the function builder using a class loader.
 * @author Luca Ferrari - fluca1978 (at) gmail.com
 *
 */
public interface IFunctionClassDefiner {

    /**
     * The entry method to define a Function object.
     * @param targetObject the reference on which the method must be invoked
     * @param targetMethod the method to invoke
     * @return the class defintion
     * @throws JFKException if something goes wrong
     */
    public abstract Class getIFunctionClassDefinition(
                                                      Object targetObject,
                                                      Method targetMethod)
    throws JFKException;

}