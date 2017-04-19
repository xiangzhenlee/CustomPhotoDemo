package com.yushan.customphotodemo.entity;

import java.util.ArrayList;
import java.util.List;

public class ImageFolderEntity implements Comparable<ImageFolderEntity> {
    private static String sDefaultName;
    private String name;
    private String path;
    private List<ImageEntity> list;

    public ImageFolderEntity() {
        list = new ArrayList<ImageEntity>();
    }

    public ImageFolderEntity(String defaultString) {
        list = new ArrayList<ImageEntity>();
        sDefaultName = defaultString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCount() {
        return list.size();
    }

    public ImageEntity getFirst() {
        return list.get(0);
    }

    public List<ImageEntity> getList() {
        return list;
    }

    public void addFile(ImageEntity file) {
        list.add(file);
    }

    @Override
    public int compareTo(ImageFolderEntity another) {
    	if(sDefaultName == null){
    		return 0;
    	}
        if (sDefaultName.equals(name) || sDefaultName.equals(another.getName())) {
            if (sDefaultName.equals(name) && sDefaultName.equals(another.getName())) {
                return 0;
            }

            if (sDefaultName.equals(name) && !sDefaultName.equals(another.getName())) {
                return -1;
            }

            if (!sDefaultName.equals(name) && sDefaultName.equals(another.getName())) {
                return 1;
            }
        }
        int value = name.compareToIgnoreCase(another.getName());
        if (value > 0) {
            return 1;
        } else if (value < 0) {
            return -1;
        } else {
            return 0;
        }
    }

}
