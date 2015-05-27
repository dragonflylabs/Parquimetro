package lib.lennken.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by caprinet on 10/14/14.
 */
public class Files {


    /*

    Initialize somewhere in the code

     */
    public static String FILES_PATH;
    public static String STORE_PATH;

    @SuppressWarnings("resource")
    public static Object loadJSON(String path,String name, Class<?> clss){
        File file = new File(path, name);
        if(file.exists()){
            try{
                Gson gson = new Gson();
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String cad = "";
                StringBuilder sb = new StringBuilder();
                while((cad = br.readLine()) != null){
                    sb.append(cad);
                }
                return gson.fromJson(sb.toString(), clss);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

    public static void saveJSON(Object response){
        File dir = new File(FILES_PATH);
        if(!dir.exists())
            dir.mkdir();
        try{
            File file = new File(FILES_PATH+STORE_PATH);
            if(!file.exists()){
                file.createNewFile();
            }
            OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(file, false));
            Gson gson = new Gson();
            String json = gson.toJson(response);
            fout.write(json);
            fout.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Uri saveImage(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public static void unzip(File file, Context context) throws IOException{
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry e;
        try{
            String filePath = file.getAbsolutePath().
                    substring(0,file.getAbsolutePath().lastIndexOf(File.separator));
            while ((e = zin.getNextEntry()) != null){
                String s = e.getName();
                File f = new File(filePath + "/" + s);
                Log.d("unzipping", s);
                FileOutputStream out = new FileOutputStream(f);
                byte []b = new byte[512];
                int len = 0;
                while ((len = zin.read(b)) != -1){
                    out.write(b,0,len);
                }
                out.close();
            }
        }catch(IOException ex){
            if (zin != null){
                zin.close();
            }
            throw ex;
        }
        zin.close();
    }

    public static byte[] getBytesFromFile(Uri uri) {
        try {
            FileInputStream is = new FileInputStream(new File(uri.getPath()));
            ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) >= 0){
                os.write(buffer, 0, len);
            }
            String image = Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP);
            Log.d("Image Compressed", image);
            return os.toByteArray();
        }catch (Exception e){
            return new byte[0];
        }
    }
}
