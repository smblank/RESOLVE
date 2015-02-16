package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MTNamed extends MTType {

    private final static int BASE_HASH = "MTNamed".hashCode();

    public final String name;

    public MTNamed(TypeGraph g, String name) {
        super(g);
        this.name = name;
    }

  /*  @Override
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTNamed(this);
    }

    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);
        v.endChildren(this);

        acceptClose(v);
    }

    @Override
    public void acceptClose(TypeVisitor v) {
        v.endMTNamed(this);
        v.endMTType(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MTType> getComponentTypes() {
        return (List<MTType>) Collections.EMPTY_LIST;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getHashCode() {
        return BASE_HASH;
    }*/

    @Override
    public String toString() {
        return "'" + name + "'";
    }

    public static Map<MTNamed, MTType> toMTNamedMap(TypeGraph source,
                                                    Map<String, MTType> original) {

        Map<MTNamed, MTType> result = new HashMap<MTNamed, MTType>();

        for (Map.Entry<String, MTType> e : original.entrySet()) {
            result.put(new MTNamed(source, e.getKey()), e.getValue());
        }

        return result;
    }
}
