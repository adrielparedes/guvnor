package org.kie.guvnor.testscenario.model;

import sun.tools.javap.FieldData;

import java.util.ArrayList;
import java.util.List;

public class CollectionFieldData implements Field {

    private String name;

    private List<FieldData> collectionFieldList = new ArrayList<FieldData>();

    @Override
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public List<FieldData> getCollectionFieldList() {
        return collectionFieldList;
    }

    public void setCollectionFieldList(List<FieldData> collectionFieldList) {
        this.collectionFieldList = collectionFieldList;
    }
}
