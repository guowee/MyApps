package com.haomee.util.imageloader;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片加载的工具类
 * @author admin
 *
 */
public class ImageLoaderCharles {
    
    private static ImageLoaderCharles instance;
                       private Context context;                                                                                  
    private ExecutorService executorService;   //线程池
    private ImageMemoryCache memoryCache;     //内存缓存
    private ImageFileCache fileCache;        //文件缓存
    private Map<String, ImageView> taskMap; //存放任务
    private boolean allowLoad = true; //是否允许加载图片
    private Bitmap mLoadingBitmap;                                                                                                     
    private ImageLoaderCharles(Context context) {
    	this.context = context;
        // 获取当前系统的CPU数目
        int cpuNums = Runtime.getRuntime().availableProcessors();
        //根据系统资源情况灵活定义线程池大小
        this.executorService = Executors.newFixedThreadPool(cpuNums + 1);
                                                                                                             
        this.memoryCache = new ImageMemoryCache(context);
        this.fileCache = new ImageFileCache();
        this.taskMap = new HashMap<String, ImageView>();
        
        // 默认加载图片
       // this.setLoadingImage(R.drawable.pic_loading2);
    }
    
    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
    }
                                                                                                         
    /**
     * 使用单例，保证整个应用中只有一个线程池和一份内存缓存和文件缓存
     */
    public static ImageLoaderCharles getInstance(Context context) {
        if (instance == null){
        	instance = new ImageLoaderCharles(context);
        }
        return instance;
    }
                                                                                                         
    /**
     * 恢复为初始可加载图片的状态
     */
    public void restore() {
        this.allowLoad = true;
    }
                                                                                                         
    /**
     * 锁住时不允许加载图片
     */
    public void lock() {
        this.allowLoad = false;
    }
                                                                                                         
    /**
     * 解锁时加载图片
     */
    public void unlock() {
        this.allowLoad = true;
        doTask();
    }
    
    /**
     * 添加任务
     */
    public void addTask(String url, ImageView img) {
        //先从内存缓存中获取，取到直接加载
        Bitmap bitmap = memoryCache.getBitmapFromCache(url);
        img.setTag(url);
        if (bitmap != null) {
            img.setImageBitmap(bitmap);
        } else {
            synchronized (taskMap) {
                /**
                 * 因为ListView或GridView的原理是用上面移出屏幕的item去填充下面新显示的item,
                 * 这里的img是item里的内容，所以这里的taskMap保存的始终是当前屏幕内的所有ImageView。
                 */
                taskMap.put(Integer.toString(img.hashCode()), img);
            }
            if (allowLoad) {
                doTask();
            }
        }
    }
                                                                                                       
    /**
     * 加载存放任务中的所有图片
     */
    
    private void doTask() {
        synchronized (taskMap) {
            Collection<ImageView> con = taskMap.values();
            for (ImageView i : con) {
                if (i != null) {
                    if (i.getTag() != null) {
                        loadImage((String) i.getTag(), i);
                    }
                }
            }
            taskMap.clear();
        }
    }
                                                                                                         
    private void loadImage(String url, ImageView img) {
        this.executorService.submit(new TaskWithResult(new TaskHandler(url, img), url));
    }
           
    
    public InputStream getStream(String url){
    	return ImageGetFromHttp.downloadStream(url);
    }
    
    /*** 获得一个图片,从三个地方获取,首先是内存缓存,然后是文件缓存,最后从网络获取 ***/
    public Bitmap getBitmap(String url) {
    	
        // 从内存缓存中获取图片
        Bitmap result = memoryCache.getBitmapFromCache(url);
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(url);
            if (result == null) {
                // 从网络获取
            	Log.i("image_url", "-------"+url+"-------");
                result = ImageGetFromHttp.downloadBitmap(url);
                if (result != null) {
                	try{
                		fileCache.saveBitmap(result, url);
                        memoryCache.addBitmapToCache(url, result);
                	}catch(Exception e){
                		e.printStackTrace();
                	}
                }
            } else {
                // 添加到内存缓存
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }
                                                                                                         
    /*** 子线程任务 ***/
    private class TaskWithResult implements Callable<String> {
        private String url;
        private Handler handler;
                                                                                                     
        public TaskWithResult(Handler handler, String url) {
            this.url = url;
            this.handler = handler;
        }
                                                                                                     
        @Override
        public String call() throws Exception {
            Message msg = new Message();
            msg.obj = getBitmap(url);
            if (msg.obj != null) {
                handler.sendMessage(msg);
            }
            return url;
        }
    }
                                                                                            
    /*** 完成消息 ***/
    private class TaskHandler extends Handler {
        String url;
        ImageView img;
                                                                                                     
        public TaskHandler(String url, ImageView img) {
            this.url = url;
            this.img = img;
        }
                                                                                                     
        @SuppressWarnings("deprecation")
		@Override
        public void handleMessage(Message msg) {
            /*** 查看ImageView需要显示的图片是否被改变  ***/
            if (img.getTag()!=null && img.getTag().equals(url)) {
                if (msg.obj != null) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    
                    
                    
                    /******* 图片加载的渐变效果，耗内存所以省去 ******/
                  /*  final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[] {
                                    new ColorDrawable(android.R.color.transparent),
                                    new BitmapDrawable(context.getResources(), bitmap)
                            });
                    // Set background to loading bitmap
                    img.setBackgroundDrawable(new BitmapDrawable(context.getResources(),mLoadingBitmap));

                    img.setImageDrawable(td);
                    td.startTransition(100);*/
                    
                    
                    
                    img.setImageBitmap(bitmap);
                }
            }
        }
    }
    
    
                                                                                                     
}