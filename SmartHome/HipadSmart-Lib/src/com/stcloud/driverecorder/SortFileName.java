package com.stcloud.driverecorder;

import java.io.File;
import java.util.Comparator;

public class SortFileName implements Comparator<File> {
    public int compare(File f1, File f2) {
          return f1.getName().compareToIgnoreCase(f2.getName());
    }
}
