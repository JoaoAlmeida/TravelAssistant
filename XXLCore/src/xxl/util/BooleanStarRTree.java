/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xxl.util;

import java.util.Iterator;
import xxl.util.statistics.StatisticCenter;
import xxl.core.collections.containers.io.ConverterContainer;
import xxl.core.functions.Function;
import xxl.core.io.converters.ConvertableConverter;
import xxl.core.spatial.KPE;
import xxl.core.spatial.rectangles.Rectangle;

/**
 *
 * @author joao
 */
public class BooleanStarRTree extends StarRTree{

    public BooleanStarRTree(StatisticCenter statisticCenter, String id, String outputPath,
            int dimensions, int bufferSize, int blockSize, int minCapacity, int maxCapacity){
        super(statisticCenter, id, outputPath, dimensions, bufferSize, blockSize, minCapacity, maxCapacity);
    }

    @Override
    public Rectangle rectangle (Object entry) {
        return (Rectangle) ((BooleanRectangle)descriptor(entry)).clone();
    }

    @Override
    protected ConverterContainer createConverterContainer(final int dimensions){
        return new ConverterContainer(
            fileContainer,
            this.nodeConverter(new ConvertableConverter(
                new Function () {
                    @Override
                    public Object invoke () {
                        return new KPE(new BooleanRectangle(dimensions));
                    }
                }), this.indexEntryConverter(
                        new ConvertableConverter(
                            new Function () {
                                @Override
                                public Object invoke () {
                                    return new BooleanRectangle(dimensions);
                                }
                            }
                        )
                   )
            )
        );
    }

    public void checkTree(IndexEntry n){
        boolean aggValue=false;
        boolean childValue=false;
        Object child;
        for(Iterator it = n.get().entries();it.hasNext();){
            child = it.next();
            if(child instanceof IndexEntry){
                childValue = ((BooleanRectangle)((IndexEntry) child).descriptor()).getBoolean();
                checkTree((IndexEntry)child);
            }else{ //child instanceof KPE
                childValue = ((BooleanRectangle)((KPE) child).getData()).getBoolean();
            }
            aggValue = aggValue || childValue;
        }
        if(aggValue!=((BooleanRectangle)((IndexEntry) n).descriptor()).getBoolean()){
            throw new RuntimeException("E R R O R ! -> Checking BooleanStarRTree.");
        }
    }
}
