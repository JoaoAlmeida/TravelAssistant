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

package xxl.core.math.statistics.nonparametric.kernels;

import java.util.Arrays;
import java.util.Iterator;

import xxl.core.comparators.ComparableComparator;
import xxl.core.cursors.Cursor;
import xxl.core.cursors.mappers.Aggregator;
import xxl.core.cursors.sources.ContinuousRandomNumber;
import xxl.core.functions.Function;
import xxl.core.math.statistics.parametric.aggregates.LastN;
import xxl.core.math.statistics.parametric.aggregates.Maximum;
import xxl.core.math.statistics.parametric.aggregates.Minimum;
import xxl.core.util.random.JavaContinuousRandomWrapper;

/** In the context of online aggregation, running aggregates are built. Given an 
 * iterator of data, an {@link xxl.core.cursors.mappers.Aggregator Aggregator}
 * computes iteratively aggregates. For instance, the current maximum
 * of the already processed data is determined. An internal aggregation function processes
 * the computation of the new element by consuming the old aggregate and the new element
 * from the input cursor.
 * 
 * Generally, each aggregation function must support a function call of the following type:<br>
 * <tt>agg_n = f (agg_n-1, next)</tt>. <br>
 * There, <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps,
 * <tt>f</tt> represents the aggregation function,
 * <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
 * and <tt>next</tt> the next object to use for computation.
 * <br>
 * This class implements an aggregation function that computes {@link xxl.core.math.statistics.nonparametric.kernels.HybridKernelDensityEstimator hybrid kernel based 
 * density estimators} or {@link xxl.core.math.statistics.nonparametric.kernels.HybridKernelCDF hybrid kernel based 
 * cdf estimators}. There,
 * the data is processed in blocks of a predefined size. Given such a block of data, a hybrid kernel based
 * estimator is established. For determining parameters required for the computation of the 
 * bandwidth, also the variance, the minimum and the maximum are iteratively computed.
 * Thus, given an input iterator, this hybrid kernel based block estimator aggregation function
 * computes a new hybrid kernel based estimator with the corresponding parameters for a new
 * data block. Concerning the change points, a predefined function computes them for the current block.
 * <br>
 * Consider the following example that displays a concrete application of a hybrid
 * kernel based block estimator aggregation function combined with an aggregator:
 * <code><pre>
 	Aggregator aggregator =
			new Aggregator(
				HybridKernelBasedBlockEstimatorAggregationFunction.inputCursor(cursor, blockSize),
				new HybridKernelBasedBlockEstimatorAggregationFunction(
					Function estimatorFactory, 
					new BiweightKernel(), 
					int bandwidthType, 
					Function changePointsAggregateFunction
				)
			);
 * </pre></code>
 * 
 * @see xxl.core.cursors.mappers.Aggregator
 * @see xxl.core.math.functions.AdaptiveAggregationFunction
 * @see xxl.core.math.statistics.nonparametric.kernels.HybridKernelCDF
 * @see xxl.core.math.statistics.nonparametric.kernels.KernelBasedBlockEstimatorAggregationFunction
 *
 */
public class HybridKernelBasedBlockEstimatorAggregationFunction extends Function {

	/** indicates the return of the last n objects */
	public static final int LASTN = 0;

	/** indicates the return of the current variance */
	public static final int MINIMUM = 1;

	/** indicates the return of the current minimum */
	public static final int MAXIMUM = 2;

	/** indicates the return of the current maximum */
	public static final int BINBORDERS = 3;

	/** Returns the current variance, maximum or minimum of the wrapped input iterator in accordance to the
	 * defined type.  
	 * 
	 * @param type indicates whether variance, minimum or maximum has to be returned
	 * @return current variance, maximum or minimum
	 */
	public static Function accessValue(final int type) {
		return new Function() {
			public Object invoke(Object o) {
				return ((Object[]) o)[type];
			}
		};
	}

	/** Constructs a cursor that builds data blocks and online aggregates for a given iterator.
	 * Namely, the variance, the minimum and the maximum
	 * are iteratively computed respectively estimated. Generally, the aggregator delivers
	 * data blocks of a predefined size and the estimated variance, minimum and maximum.
	 *
	 * @param input input data delivered by an iterator
	 * @param blockSize size of the blocks
	 * @param changePointsAggregateFunction function delivering iteratively the corresponding change points
	 * @return a cursor delivering Objects of type <code>Object[]</code>
	 * containing:<BR>
	 * 0. last seen n objects<BR>
	 * 1. estimation of the variance of the whole data<BR>
	 * 2. minimum of the previous data<BR>
	 * 3. maximum of the previous data<BR>
	 * 4. function delivering iteratively the corresponding change points<BR>
	 */
	public static Cursor inputCursor(Iterator input, int blockSize, final Function changePointsAggregateFunction) {
		return new Aggregator(
			input,
			new Function[] { new LastN(blockSize), new Minimum(), new Maximum(), changePointsAggregateFunction });
	}

	/** indicates what type of bandwidth to use */
	protected int bandwidthType;

	/** factory for a kernel based estimator */
	protected Function estimatorFactory;

	/** used kernel function for the estimators */
	protected KernelFunction kf;

	/** internally used counter to determine how many objects are processed */
	protected int c;

	/** index of the last built estimator */
	protected int last;

	/** indicates whether this instance is initialized */
	protected boolean init;

	/** Constructs a KernelBasedBlockEstimatorAggregationFunction. The factory for
	 * building the block estimators, the kernel function and the bandwith type are given.
	 *
	 * @param estimatorFactory factory constructing the estimators
	 * @param kf used kernel function 
	 * @param bandwidthType used bandwidth strategy
	 *
	 */
	public HybridKernelBasedBlockEstimatorAggregationFunction(
		Function estimatorFactory,
		KernelFunction kf,
		int bandwidthType) {
		this.kf = kf;
		this.estimatorFactory = estimatorFactory;
		this.bandwidthType = bandwidthType;
		c = 0;
		last = 0;
		init = false;
	}

	/** Constructs a KernelBasedBlockEstimatorAggregationFunction. The factory for
	 * building the block estimators is given. Concerning the kernel estimators
	 * Biweigth kernel functions and the normal scale rule for the bandwidth are used.
	 *
	 * @param estimatorFactory factory constructing the estimators
	 */
	public HybridKernelBasedBlockEstimatorAggregationFunction(Function estimatorFactory) {
		this(estimatorFactory, new BiweightKernel(), KernelBandwidths.THUMB_RULE_1D);
	}

	/** Constructs a KernelBasedBlockEstimatorAggregationFunction. The factory for
	 * building the block estimators returns hybrid kernel based density estimators.
	 * Concerning the kernel estimators
	 * Biweight kernel functions and the normal scale rule for the bandwidth are used.
	 */
	public HybridKernelBasedBlockEstimatorAggregationFunction() {
		this(HybridKernelDensityEstimator.FACTORY, new BiweightKernel(), KernelBandwidths.THUMB_RULE_1D);
	}

	/** Two-figured function call for supporting aggregation by this function.
	 * Each aggregation function must support a function call like <tt>agg_n = f (agg_n-1, next)</tt>,
	 * where <tt>agg_n</tt> denotes the computed aggregation value after <tt>n</tt> steps, <tt>f</tt>
	 * the aggregation function, <tt>agg_n-1</tt> the computed aggregation value after <tt>n-1</tt> steps
	 * and <tt>next</tt> the next object to use for computation.
	 * This method delivers only <tt>null</tt> as aggregation result as long as the aggregation
	 * has not yet initialized.
	 * As result of the aggregation a hybrid kernel based block estimator, that relies on the current block, 
	 * is returned.
	 * 
	 * @param old result of the aggregation function in the previous computation step
	 * @param next next object used for computation
	 * @return new kernel based block estimator
	 */
	public Object invoke(Object old, Object next) { // next[0] = sample, next[1] = min , next[2] = max, next[3] = cps
		c++;
		if (next == null)
			return null;
		Object[] aggregate = (Object[]) next;
		boolean build = false;
		// indicates whether a new function must be built or not
		// all needed aggregates fully initialized?
		Object[] block = (Object[]) aggregate[0];
		if (block == null)
			// if the block did not init, this functions also did not init
			return null;
		if (!init) { // building up first function (block != null, but no functions returned so far)
			last = c; // storing time
			build = true; // building up
			init = true;
		} else {
			int blockSize = block.length;
			if (c >= last + blockSize) { // new block
				last = c; // storing time
				build = true; // building up
			}
		}
		if (build) {
			double min = ((Number) aggregate[1]).doubleValue();
			double max = ((Number) aggregate[2]).doubleValue();
			Object[] cps = ((Object[]) aggregate[3]);
			// --- copying and sorting block treated as sample -------
			Object[] sample = new Object[block.length];
			System.arraycopy(block, 0, sample, 0, block.length);
			Arrays.sort(sample, new ComparableComparator());
			// --- building up parameter array for function factory ----
			return new HybridKernelCDF(kf, sample, bandwidthType, min, max, cps);
		} else
			return old;
	}

	/**
	 * The main method contains some examples to demonstrate the usage
	 * and the functionality of this class.
	 *
	 * @param args array of <tt>String</tt> arguments. It can be used to
	 * 		submit parameters when the main method is called.
	 */
	public static void main(String[] args) {
		/*********************************************************************/
		/*                            Example                                */
		/*********************************************************************/
		int blockSize = 100;
		int N = 2000;
		xxl.core.cursors.Cursor cursor = new ContinuousRandomNumber(new JavaContinuousRandomWrapper(), N);
		Aggregator aggregator = 
			new Aggregator(
				inputCursor(
					cursor, 
					blockSize, 
					new Function() {
						public Object invoke(Object old, Object next) {
							return new Double[] { new Double(0.5)};
						}
					}
				), 
				new HybridKernelBasedBlockEstimatorAggregationFunction()
			);
		int c = 0;
		java.util.List l = new java.util.ArrayList(N / blockSize);
		double[] grid = xxl.core.util.DoubleArrays.equiGrid(0.0, 1.0, 100);
		Object last = null;
		while (aggregator.hasNext()) {
			Object next = aggregator.next();
			if (!(last == next)) {
				System.err.println("c:" + (++c));
				if (N % blockSize == 0) {
					l.add(xxl.core.math.Statistics.evalRealFunction(grid, (xxl.core.math.functions.RealFunction) next));
				}
				last = next;
			}
		}
		// --- output ---
		for (int i = 0; i < grid.length; i++) {
			System.out.print(grid[i] + "\t");
			for (int j = 0; j < l.size(); j++) {
				System.out.print(((double[]) l.get(j))[i] + "\t");
			}
			System.out.println();
		}
	}
}