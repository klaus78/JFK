package jfk.test.role;

public class Person {
	
	public Person(String name)
	{
		_name = name;
	}
	
	public void talk(String what)
	{
		System.out.println("Person of name " + _name + " says " + what);
	}
	
	
	String _name;
}
