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

package xxl.core.collections.containers;

import java.util.Iterator;
import java.util.NoSuchElementException;

import xxl.core.functions.Function;
import xxl.core.io.converters.FixedSizeConverter;

/**
 * The class provides a constained decorator for a container. This class
 * is similar to the decorator container, but it does not exactly follow
 * the <i>Decorator Design Pattern</i> (for further details see
 * Structural Patterns, Decorator in <i>Design Patterns: Elements of
 * Reusable Object-Oriented Software</i> by Erich Gamma, Richard Helm,
 * Ralph Johnson, and John Vlissides). Whenever possible, the constrained
 * decorator uses the default implementations of the container class.
 * I.e., this class provides no usual decorator that simply redirects
 * method call to an underlying container, but a kind of partial
 * decorator that maps its methods to a set of <i>central</i> methods,
 * which are decorated. Therefore, this class provides a way to profit
 * from the default implementations of the container class and the
 * advantages of decoration.<p>
 *
 * <b>Note</b> that this class provides only a workaround that allows the
 * user to decorate only a single central method instead of a complete
 * <i>set</i> of methods and therefore to save time.<p>
 *
 * Usage example (1).
 * <pre>
 *     // create a new decorated container that adds functionality to the insert method and leaves
 *     // the other methods untouched
 *
 *     DecoratorContainer container = new DecoratorContainer(new MapContainer()) {
 *         public Object insert (Object object, boolean unfix) {
 *
 *             // every desired functionality can be added to this method
 *
 *             System.out.println("Before the insertion of the specified object!");
 *             Object object = super.insert(object, unfix);
 *             System.out.println("After the insertion of the specified object!");
 *             return object;
 *         }
 *     }
 * </pre>
 *
 * @see xxl.core.io.converters.Converter
 * @see Function
 * @see Iterator
 * @see NoSuchElementException
 */
public abstract class ConstrainedDecoratorContainer extends AbstractContainer {

	/**
	 * A factory method to create a new DecoratorContainer. It may
	 * only be invoked with an array (<i>parameter list</i>) (for further
	 * details see Function) of containers. The array (<i>parameter
	 * list</i>) will be used to initialize the decorated container. This
	 * field is set to
	 * <code>{@link UnmodifiableContainer#FACTORY_METHOD UnmodifiableContainer.FACTORY_METHOD}</code>.
	 *
	 * @see Function
	 */
	public static final Function FACTORY_METHOD = UnmodifiableContainer.FACTORY_METHOD;

	/**
	 * A reference to the container to be decorated. This reference is
	 * used to perform method calls on the <i>original</i> container.
	 */
	protected Container container;

	/**
	 * Constructs a new DecoratorContainer that decorates the specified
	 * container.
	 *
	 * @param container the container to be decorated.
	 */
	public ConstrainedDecoratorContainer (Container container) {
		this.container = container;
	}

	/**
	 * Returns a converter for the ids generated by this container. A
	 * converter transforms an object to its byte representation and vice
	 * versa - also known as serialization in Java.<br>
	 * Since the identifier may have an arbitrary type (which has to be
	 * known in the container), the container has to provide such a method
	 * when the data is not stored in main memory.
	 *
	 * @return a converter for serializing the identifiers of the
	 *         container.
	 */
	public FixedSizeConverter objectIdConverter () {
		return container.objectIdConverter();
	}

	/**
	 * Returns the size of the ids generated by this container in bytes.
	 * This call is forwarded to the primary Container.
	 * @return the size in bytes of each id.
	 */
	public int getIdSize() {
		return container.getIdSize();
	}
	
	/**
	 * Removes all elements from the Container. After a call of this
	 * method, <tt>size()</tt> will return 0.<br>
	 * Note, that the implementation of this method relies on the remove
	 * operation of the iterator returned by the method <tt>ids()</tt>.
	 */
	public void clear () {
		container.clear();
	}

	/**
	 * Closes the Container and releases all sources. For external
	 * containers, this method closes the files immediately. MOREOVER, all
	 * iterators operating on the container can be in illegal states.
	 * Close can be called a second time without any impact. The default
	 * implementation of close is empty (which is OK for a MapContainer).<br>
	 * Note, that it would be desirable that the finalize-mechanism of
	 * Java would already offer the functionality of close. However,
	 * finalize does not release the sources immediately! Consequently,
	 * the corresponding file of a "closed" Container may be opened and
	 * some of the data is still not written back. This is a problem when
	 * for example the JVM stops running (because of a system error).
	 */
	public void close () {
		container.close();
	}

	/**
	 * Returns true if the container contains an object for the identifier
	 * <tt>id</tt>.<br>
	 * Note, that when a user would like to store null objects in a
	 * container this method has to be reimplemented.
	 *
	 * @param id identifier of the object.
	 * @return true if the container contains an object for the specified
	 *          identifier.
	 */
	public boolean contains (Object id) {
		return container.contains(id);
	}

	/**
	 * Flushes all modified elements from the buffer into the container.
	 * After this call the buffer and the container are synchronized. The
	 * default implementation of flush is empty which is OK for an
	 * unbuffered container.
	 */
	public void flush () {
		container.flush();
	}

	/**
	 * Flushes the object with identifier <tt>id</tt> from the buffer into
	 * the container.
	 *
	 * @param id identifier of the object that should be written back.
	 */
	public void flush (Object id) {
		container.flush(id);
	}

	/**
	 * Returns the object associated to the identifier <tt>id</tt>. An
	 * exception is thrown when the desired object is not found. If unfix,
	 * the object can be removed from the underlying buffer. Otherwise
	 * (!unfix), the object has to be kept in the buffer.
	 *
	 * @param id identifier of the object.
	 * @param unfix signals whether the object can be removed from the
	 *        underlying buffer.
	 * @return the object associated to the specified identifier.
	 * @throws NoSuchElementException if the desired object is not found.
	 */
	public Object get (Object id, boolean unfix) throws NoSuchElementException {
		return container.get(id, unfix);
	}

	/**
	 * Returns an iterator that deliver the identifiers of all objects of
	 * the container.
	 *
	 * @return an iterator of object identifiers.
	 */
	public Iterator ids () {
		return container.ids();
	}

	/**
	 * Inserts a new object into the container and returns the unique
	 * identifier that the container has been associated to the object.
	 * The identifier can be reused again when the object is deleted from
	 * the buffer. If unfixed, the object can be removed from the buffer.
	 * Otherwise, it has to be kept in the buffer until an
	 * <tt>unfix()</tt> is called.<br>
	 * After an insertion all the iterators operating on the container can
	 * be in an invalid state.<br>
	 * This method also allows an insertion of a null object. In the
	 * application would really like to have such objects in the
	 * container, some methods have to be modified.
	 *
	 * @param object is the new object.
	 * @param unfix signals whether the object can be removed from the
	 *        underlying buffer.
	 * @return the identifier of the object.
	 */
	public Object insert (Object object, boolean unfix) {
		return container.insert(object, unfix);
	}

	/**
	 * Checks whether the <tt>id</tt> has been returned previously by a
	 * call to insert or reserve and hasn't been removed so far.
	 *
	 * @param id the id to be checked.
	 * @return true exactly if the <tt>id</tt> is still in use.
	 */
	public boolean isUsed (Object id) {
		return container.isUsed(id);
	}

	/**
	 * Removes the object with identifier <tt>id</tt>. An exception is
	 * thrown when an object with an identifier <tt>id</tt> is not in the
	 * container.<br>
	 * After a call of <tt>remove()</tt> all the iterators (and cursors)
	 * can be in an invalid state.
	 *
	 * @param id an identifier of an object.
	 * @throws NoSuchElementException if an object with an identifier
	 *         <tt>id</tt> is not in the container.
	 */
	public void remove (Object id) throws NoSuchElementException {
		container.remove(id);
	}

	/**
	 * Reserves an id for subsequent use. The container may or may not
	 * need an object to be able to reserve an id, depending on the
	 * implementation. If so, it will call the parameterless function
	 * provided by the parameter <tt>getObject</tt>.
	 *
	 * @param getObject A parameterless function providing the object for
	 * 			that an id should be reserved.
	 * @return the reserved id.
	*/
	public Object reserve (Function getObject) {
		return container.reserve(getObject);
	}

	/**
	 * Returns the number of elements of the container.
	 *
	 * @return the number of elements.
	 */
	public int size () {
		return container.size();
	}

	/**
	 * Unfixes the Object with identifier <tt>id</tt>. This method throws
	 * an exception when no element with an identifier <tt>id</tt> is in
	 * the container. After one call of unfix the buffer is allowed to
	 * remove the object (although the objects have been fixed more than
	 * once).<br>
	 * The default implementation only checks whether an object with
	 * identifier <tt>id</tt> is in the buffer.
	 *
	 * @param id identifier of an object that should be unfixed in the
	 *        buffer.
	 * @throws NoSuchElementException if no element with an identifier
	 *         <tt>id</tt> is in the container.
	 */
	public void unfix (Object id) throws NoSuchElementException {
		container.unfix(id);
	}

	/**
	 * Overwrites an existing (id,*)-element by (id, object). This method
	 * throws an exception if an object with an identifier <tt>id</tt>
	 * does not exist in the container.
	 *
	 * @param id identifier of the element.
	 * @param object the new object that should be associated to
	 *        <tt>id</tt>.
	 * @param unfix signals whether the object can be removed from the
	 *        underlying buffer.
	 * @throws NoSuchElementException if an object with an identifier
	 *         <tt>id</tt> does not exist in the container.
	 */
	public void update (Object id, Object object, boolean unfix) throws NoSuchElementException {
		container.update(id, object, unfix);
	}
}
