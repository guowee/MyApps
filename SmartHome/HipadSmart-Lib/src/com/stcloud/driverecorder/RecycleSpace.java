package com.stcloud.driverecorder;

import android.util.Log;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecycleSpace extends Thread {
    private static final String TAG = "RecycleSpace";
    // for video folder
    public static final int FOLDER_VIDEO = 0;
    // for locked video folder
    public static final int FOLDER_LOCKED_VIDEO = 1;
    // for photo folder
    public static final int FOLDER_PHOTO = 2;
    // for locked photo folder
    public static final int FOLDER_LOCKED_PHOTO = 3;
    // parent folder
    public static final int FOLDER_PARENT = 4;
    public static final int FOLDER_MAX = FOLDER_PARENT;
	
	public final static String FOLDER_PARENT_PATH = "/storage/external";
	public final static String STORAGE_PATH = "/storage/external/Hipad";
	
    public static final String[] FOLDER_NAME = new String[] {
    	STORAGE_PATH+"/Video",      // FOLDER_VIDEO
    	STORAGE_PATH+"/Video_lock", // FOLDER_LOCKED_VIDEO
    	STORAGE_PATH+"/Photo",      // FOLDER_PHOTO
    	STORAGE_PATH+"/Photo_lock", // FOLDER_LOCKED_PHOTO
    	FOLDER_PARENT_PATH,         // FOLDER_PARENT
    };

    public static final int[] MAX_PERCENT_FOLDER_VOLUME = new int[] {
            60, // FOLDER_VIDEO 
            20, // FOLDER_LOCKED_VIDEO
            1, // FOLDER_PHOTO
            2, // FOLDER_LOCKED_PHOTO
            0, // FOLDER_PARENT
    };

    private File [] m_path = new File[FOLDER_MAX];
    private File [] m_oldest_file = new File[FOLDER_MAX];
    private File m_path_parent = new File(FOLDER_NAME[FOLDER_PARENT]);

    public void run() {
        long total;
        int i;
        long [] used_size = new long[FOLDER_MAX];

        for(i=0; i<FOLDER_MAX; i++)
            m_path[i] = new File(FOLDER_NAME[i]);
        Log.d(TAG, "RecyleSpace running");
        while(!isInterrupted()) {
            total = m_path_parent.getTotalSpace();
            if(total > 0) {
                for (i = 0; i < FOLDER_MAX; i++) {
                    used_size[i] = this.getFileSize(m_path[i], i);
                    if (used_size[i] * 100 / total >= MAX_PERCENT_FOLDER_VOLUME[i]) {
                        // delete oldest file
                        Log.d(TAG, "will delete " + m_oldest_file[i].getName());
                        m_oldest_file[i].delete();
                    }
                }

                Log.d(TAG, "total:" + total + ", used(video):" + used_size[FOLDER_VIDEO] * 100 / total + "%, used(locked video):" + used_size[FOLDER_LOCKED_VIDEO] * 100 / total + "%, used(photo):" + used_size[FOLDER_PHOTO] * 100 / total + "%, used(locked photo):" + used_size[FOLDER_LOCKED_PHOTO] * 100 / total + "%");
            }
            try {
                sleep(5000);
            } catch (InterruptedException e) {}
        }
    }
    private long getFileSize(File path, int index) {
        long size = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date oldest_date;
        oldest_date = null;
        if (path == null || !path.exists()) return 0;
        for (File subf: path.listFiles()) {
            if(subf.isDirectory()) continue;
            size += subf.length();
            String filename = subf.getName();
            try {
                Date date = sdf.parse(filename.substring(4));
                if(oldest_date == null || date.before(oldest_date)) {
                    oldest_date = date;
                    m_oldest_file[index] = subf;
                }
            } catch (ParseException e) {}
        }
        return size;
    }
}
