package lib.lennken.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Print {

    public static final int BLUETOOTH_ERROR = 1;
    public static final int PAIR_ERROR = 2;
    public static final int DEVICE_ERROR = 3;
    public static final int DONE = 4;
    public static final int ERROR = 5;
    public static final int PRINTING = 6;
    private final String bluetoothDevice;
    private final WebView mWebView;
    private final String mHtmlString;
    private Context mContext;
    private PrintListener mPrintListener;
    private int width;
    private int height;
    private int mNumCopies;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    public Print(Context context, WebView webView,String toPrint, String bluetoothDevice, PrintListener callback) {
        this.mContext = context;
        this.bluetoothDevice = bluetoothDevice;
        this.mPrintListener = callback;
        this.mWebView = webView;
        this.mHtmlString = toPrint;
        mWebView.addJavascriptInterface(new JavaScriptInterface(mContext), "HtmlViewer");
        mWebView.setWebViewClient(new PrintWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    public void startPrinting(int copies){
        mNumCopies = copies;
        mWebView.loadDataWithBaseURL(null, mHtmlString, "text/html", "utf-8", null);
    }

    private void print(){
        int widthHtml = 580;
        int heightHtml = convertToPx(height + 50);

        Log.d("Width and Height: ", "" + width + "," + height);
        Log.d("Width and Height HTML: ", "" + widthHtml + "," + heightHtml);

        final Bitmap bitmap = Bitmap.createBitmap(widthHtml, heightHtml, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mWebView.draw(canvas);

        float widthPercent = (100.0f * 576.0f / (float)widthHtml)/100;
        float widthDesired = widthPercent * widthHtml;
        float heightDesired = widthPercent * heightHtml;
        final Bitmap printBitmap = Bitmap.createScaledBitmap(bitmap, (int)widthDesired, (int)heightDesired, false);

        BluetoothPrintService btService = null;
        try
        {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            if (btAdapter==null)
            {
                mPrintListener.onChangeStatus(BLUETOOTH_ERROR);
                return;
            }

            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

            if (pairedDevices.size()==0)
            {
                mPrintListener.onChangeStatus(PAIR_ERROR);
                return;
            }

            BluetoothDevice selectedBTDevice = null;
            for( BluetoothDevice device : pairedDevices)
            {
                if (device.getName().compareTo(bluetoothDevice)==0)
                {
                    selectedBTDevice = device;
                    break;
                }
            }

            if (selectedBTDevice==null)
            {
                mPrintListener.onChangeStatus(DEVICE_ERROR);
                return;
            }
            btService = getBtServiceConnected(selectedBTDevice, 8);
            mPrintListener.onChangeStatus(PRINTING);
            for(int i = 0; i < mNumCopies; i++) {
                doPrintJob(printBitmap, btService);
            }
            Thread.sleep(2000);
            mPrintListener.onChangeStatus(DONE);
        } catch (Throwable e){
            e.printStackTrace();
            mPrintListener.onChangeStatus(ERROR);
        } finally {
            if (btService != null) {
                btService.stop();
            }
        }
    }

    private byte[] getPrintBytes(Bitmap src) throws IOException{

        int width = src.getWidth();
        int height = src.getHeight();
        int []imageData =  new int[width * height];
        int bitBite;

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        src.getPixels(imageData, 0, width, 0, 0, width, height);
        byteArray.write(new byte[]{27,'P','$'});
        byteArray.write(new byte[]{27, 'V', (byte) (height & 0xFF), (byte) (height >> 8)});

        for (int row = 0; row < height; row ++){

            bitBite = 0;

            for (int index = 0; index < width; index += 8 ){

                int offset = row * width + index;

                if (imageData[offset] < -8289918){
                    bitBite += 128;
                }

                if (imageData[offset + 1] < -8289918){
                    bitBite += 64;
                }

                if (imageData[offset + 2] < -8289918){
                    bitBite += 32;
                }

                if (imageData[offset + 3] < -8289918){
                    bitBite += 16;
                }

                if (imageData[offset + 4] < -8289918){
                    bitBite += 8;
                }

                if (imageData[offset + 5] < -8289918){
                    bitBite += 4;
                }

                if (imageData[offset + 6] < -8289918){
                    bitBite += 2;
                }

                if (imageData[offset + 7] < -8289918){
                    bitBite += 1;
                }

                byteArray.write(bitBite);

                bitBite = 0;

            }
        }

        byteArray.write(new byte[]{27,'V', 0, 0});
        byteArray.write(new byte[]{27,'P','#'});

        return byteArray.toByteArray();

    }

    private BluetoothPrintService getBtServiceConnected(BluetoothDevice device,  int nWaitTime)
            throws Throwable{
        BluetoothPrintService btService = new BluetoothPrintService();
        btService.start();
        btService.connect(device);
        while (btService.getState() != BluetoothPrintService.STATE_CONNECTED)
        {
            Thread.sleep(1000);
            nWaitTime--;
            if (nWaitTime==0)
            {
                throw( new Throwable("Unable To connect to " + device.getName()+ "!"));
            }
        }
        return btService;

    }

    int heightPaper = 1000;
    private void doPrintJob(Bitmap bitmap, BluetoothPrintService btService) throws Exception{
        final Bitmap printBitmap = Bitmap.createBitmap(576, bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(printBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        /*
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        int res = printBitmap.getHeight() % heightPaper;
        int parts = printBitmap.getHeight() / heightPaper;
        for(int i = 0; i < parts; i++){
            bitmaps.add(Bitmap.createBitmap(printBitmap, 0, i*heightPaper, 576, heightPaper));
        }
        if(res > 0) {
            bitmaps.add(Bitmap.createBitmap(printBitmap, 0, (heightPaper*parts), 576, res));
        }

        for(Bitmap b : bitmaps){
            byte[] bytes = getPrintBytes(b);
            btService.write(bytes);
        }
        */
        btService.write(getPrintBytes(printBitmap));
    }

    private int convertToPx(int dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        Log.d("Scale: ", String.valueOf(scale));
        return (int) (dp * scale + 0.5f);
    }

    public static String renderTemplate(Context context, String path,
                                        HashMap<String, String> values) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(path);
            BufferedReader in= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str=in.readLine()) != null) {
                builder.append(str);
            }
            in.close();
            String toRender = builder.toString();
            for(Map.Entry<String, String> entry : values.entrySet()){
                toRender = toRender.replace(entry.getKey(), entry.getValue());
            }
            Log.d("render"," red "+toRender);
            return toRender;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseBlock(Context context,String block,HashMap<String,String> values){
        try {
        String toRender = block;
        for (Map.Entry<String,String> entry : values.entrySet()){
            toRender = toRender.replace(entry.getKey(),entry.getValue());
        }
        Log.d("renderblock"," red "+toRender);
        return toRender;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private class JavaScriptInterface{

        Context mContext;
        public JavaScriptInterface(Context context){
            this.mContext = context;
        }


        @JavascriptInterface
        public void showHTML(String webMessage){
            try{
                String[] array = webMessage.split(";");
                width = Integer.parseInt(array[0]);
                height = Integer.parseInt(array[1]);
                print();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class PrintWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:window.HtmlViewer.showHTML(getSize());");
        }
    }
}
