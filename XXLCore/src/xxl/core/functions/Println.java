/* XXL: The eXtensible and fleXible Library for data processing

Copyright (C) 2000-2004 Prof. Dr. Bernhard Seeger
                        Head of the Database Research Group
                        Department of Mathematics and Computer Science
                        University of Marburg
                        Germany

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307,
USA

	http://www.xxl-library.de

bugs, requests for enhancements: request@xxl-library.de

If you want to be informed on new versions of XXL you can
subscribe to our mailing-list. Send an email to

	xxl-request@lists.uni-marburg.de

without subject and the word "subscribe" in the message body.
*/

package xxl.core.functions;

import java.io.PrintStream;

/**
 * The Println-Function prints arguments to a specified print stream and returns them.
 * If no print stream is specified, the standard out is taken as default.
 * If an array of arguments is given the single objects will be separated by a given
 * delimeter.
 * Println-Functions can be used to log a composition of functions at a
 * specified level. To do so just a Prinln-Function has to be inserted at the right
 * place like
 * <code>
		Function tan = div.compose ( (new Println()).compose( sin), cos);
 * </code>
 * instead of 
 * <code>
		Function tan = div.compose ( sin, cos);
 * </code>
 *
 */

public class Println extends Function {

	/** Default Function for Println using System.out as
	 * {@link java.io.PrintStream PrintStream} and Space
	 * as delimeter.
	 */
	public static final Println DEFAULT_INSTANCE = new Println();

	/** {@link java.io.PrintStream PrintStream} used for output */
	protected PrintStream printStream;

	/** used delimeter to separate the given arguments */
	protected Object delimeter;

	/** Constructs a new Println-Function.
	 * @param printStream {@link java.io.PrintStream PrintStream} using for the output
	 * @param delimeter delimeter used for separating array-arguments
	 */
	public Println( PrintStream printStream, Object delimeter) {
		this.printStream = printStream;
		this.delimeter = delimeter;
	}

	/** Constructs a new Println-Function using a space as delimeter.
	 * @param printStream {@link java.io.PrintStream PrintStream} using for the output
	 */
	public Println( PrintStream printStream) {
		this( printStream, " ");
	}

	/** Constructs a new Println-Function using a space as delimeter and
	 * <code>System.out</code> as output.
	 */
	public Println() {
		this( System.out);
	}

	/** Prints the given arguments to a {@link java.io.PrintStream PrintStream}
	 * and returns the arguments.
	 * @param arguments the arguments to print
	 * @return the arguments given.
	 */
	public Object invoke( Object [] arguments) {
		// arrays of length 0? -> just doing 'newLine'
		if (( arguments == null) || ( arguments.length == 0)){
			printStream.println();
			return arguments;
		}
		// if just a single object is given, return this single object itself
		if ( arguments.length == 1) return invoke ( arguments [0]);
		// else process the given array
		for ( int i=0; i < arguments.length -1; i++){
			printStream.print( arguments [i]);
			printStream.print( delimeter);
		}
		printStream.println( arguments [arguments.length-1] );
		return arguments;
	}

	/** Prints the given argument to a {@link java.io.PrintStream PrintStream}
	 * and returns the argument itself.
	 * @param argument the argument to print
	 * @return the argument given.
	 */
	public Object invoke( Object argument) {
		printStream.println( argument);
		return argument;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * submit parameters when the main method is called.
	 */
	public static void main( String [] args){
		Function sin = new Function() {
					public Object invoke( Object o) {
						return new Double( Math.sin( ( (Number) o).doubleValue()));
					}
				};
		Function cos = new Function() {
					public Object invoke( Object o) {
						return new Double( Math.cos( ( (Number) o).doubleValue()));
					}
				};
		Function div = new Function() {
					public Object invoke( Object o1, Object o2) {
						return new Double( ( (Number) o1).doubleValue() / ((Number) o2).doubleValue() );
					}
				};
		System.out.print("Printing result of sin at computation:\nsin=");
		Function tan = div.compose ( (new Println()).compose( sin), cos);
		System.out.println( "tan(0.5)=" + tan.invoke ( new Double(0.5)));
		//
		System.out.println("----------------------------\nJust printing an array:");
		Println.DEFAULT_INSTANCE.invoke ( new Double[]{ new Double(1.0), new Double(2.0), new Double(3.0)});
		//
		System.out.println("----------------------------\nJust printing array of length 0:");
		Println.DEFAULT_INSTANCE.invoke ( new Object[0]);
		//
		System.out.println("----------------------------\nJust printing null:");
		Println.DEFAULT_INSTANCE.invoke ( null);
	}
	
}