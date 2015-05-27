package lib.lennken.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class PrintUtility {

    public static final int BLUETOOTH_ERROR = 1;
    public static final int PAIR_ERROR = 2;
    public static final int DEVICE_ERROR = 3;
    public static final int DONE = 3;
    public static final int ERROR = 4;
    private final String bluetoothDevice;
    private final WebView mWebView;
    private final String mHtmlString;
    private Context mContext;
    private PrintListener mPrintListener;
    private int width;
    private int height;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    public PrintUtility(Context context, String htmlString, String bluetoothDevice, PrintListener callback) {
        this.mContext = context;
        this.bluetoothDevice = bluetoothDevice;
        this.mPrintListener = callback;
        this.mHtmlString = htmlString;
        mWebView = new WebView(context);
        mWebView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        mContext.getResources().getDisplayMetrics().widthPixels,
                        mContext.getResources().getDisplayMetrics().heightPixels));
        mWebView.addJavascriptInterface(new JavaScriptInterface(mContext), "HtmlViewer");
        mWebView.setWebViewClient(new PrintWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    public void startPrinting(){
        mWebView.loadDataWithBaseURL(null, mHtmlString, "text/html", "utf-8", null);
    }

    private void print(){
        int widthHtml = 580;
        int heightHtml = convertToPx(height + 50);

        Log.d("Width and Height: ", "" + width + "," + height);
        Log.d("Width and Height HTML: ", ""+widthHtml+","+heightHtml);

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
            doPrintJob(printBitmap, btService);
            mPrintListener.onChangeStatus(DONE);
        } catch (Throwable e){
            e.printStackTrace();
            mPrintListener.onChangeStatus(ERROR);
        } finally {
            if (btService != null)
                btService.stop();
        }
    }

    private byte[] getPrintBytes(Bitmap src) throws IOException{

        int width = src.getWidth();
        int height = src.getHeight();
        int []imageData =  new int[width * height];
        int bitBite;

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream(1000);
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
        byteArray.write(new byte[]{27,'P','#', 4});

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

    private void doPrintJob(Bitmap bitmap, BluetoothPrintService btService) throws Exception{
        final Bitmap printBitmap = Bitmap.createBitmap(576, bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(printBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);

        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        int res = printBitmap.getHeight() % 576;
        int parts = printBitmap.getHeight() / 576;
        for(int i = 0; i < parts; i++){
            bitmaps.add(Bitmap.createBitmap(printBitmap, 0, i*576, 576, 576));
        }
        if(res > 0) {
            bitmaps.add(Bitmap.createBitmap(printBitmap, 0, (576*parts), 576, res));
        }
        for(Bitmap b : bitmaps){
            byte[] bytes = getPrintBytes(b);
            btService.write(bytes);
        }
    }

    private int convertToPx(int dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        Log.d("Scale: ", String.valueOf(scale));
        return (int) (dp * scale + 0.5f);
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
