package com.mytrackerapp.myapplication.tech;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mytrackerapp.myapplication.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class QRGeneratorActivity extends Activity {
    ImageView qrCodeImageview;
    String QRcode;
    public final static int WIDTH = 500;
    public final static int HEIGHT = 500;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        getID();
        //
        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();

        if(!extBundle.isEmpty()) {
            boolean hasGpsCords = extBundle.containsKey("cords");
            if (hasGpsCords) {
                String[] cords = extBundle.getStringArray("cords");
                QRcode = cords[0] + " " + cords[1];
                System.out.println(QRcode);
            }
            //

            // create thread to avoid ANR Exception
            Thread t = new Thread(new Runnable() {
                public void run() {
//                    QRcode = "!@#gtn^&05";
                    System.out.println(QRcode);
                    try {
                        synchronized (this) {
                            wait(1);
                            // runOnUiThread method used to do UI task in main thread.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        bitmap = encodeAsBitmap(QRcode);
                                        SaveImage(bitmap);
                                        qrCodeImageview.setImageBitmap(bitmap);
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    } // end of catch block
                                } // end of run method
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    private void getID() {
        qrCodeImageview = (ImageView) findViewById(R.id.img_qr_code_image);
    }

    /**
     * save qr at the tech device
     * @param finalBitmap
     */
    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        //if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(QRGeneratorActivity.this, "QR has been saved in your device.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is method call from on create and return bitmap image of QRCode.
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }
}