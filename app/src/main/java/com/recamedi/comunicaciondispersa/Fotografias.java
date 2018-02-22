package com.recamedi.comunicaciondispersa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Renel01 on 21/02/2018.
 */

public class Fotografias extends ActivityCompat {
    private Uri output;
    private String foto;
    private File file;
    private Context contexto;
    private Activity actividadActual;
    public Fotografias(Uri output, String foto, File file, Context contexto, Activity actividadActual) {
        this.output = output;
        this.foto = foto;
        this.file = file;
        this.contexto = contexto;
        this.actividadActual = actividadActual;
    }

    public Uri getOutput() {
        return output;
    }

    public void setOutput(Uri output) {
        this.output = output;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public Activity getActividadActual() {
        return actividadActual;
    }

    public void setActividadActual(Activity actividadActual) {
        this.actividadActual = actividadActual;
    }

    public void ObtenerCamara(String NombreFoto){
        foto = Environment.getExternalStorageDirectory() +"/"
                +NombreFoto+".jpg";
        file=new File(foto);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        output = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        //startActivityForResult(intent, 1); Esto deberia ir cuando se le invoque

        //this.contexto.startActivityForResult(intent,1);
        //Activity act;
        actividadActual.startActivityForResult(intent,1);
        //return intent;

    }



    public void loadImageFromFile(ImageView contendorImagen) throws FileNotFoundException {
        ImageView view = contendorImagen;
        view.setVisibility(View.VISIBLE);
        int targetW = view.getWidth();
        int targetH = view.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(foto, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, 300, 300);
        bmOptions.inPurgeable = true;


        Bitmap bitmap = BitmapFactory.decodeStream(this.contexto.getContentResolver().openInputStream(output),null,bmOptions);
        //Bitmap bitmap = decodeSampledBitmapFromResource(output, 100, 100);

        view.setImageBitmap(bitmap);

        Bitmap.Config config = bitmap.getConfig();
        if(config == null){
            config = Bitmap.Config.ARGB_8888;
        }


        Bitmap newBitmap = null;
        newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas newCanvas = new Canvas(newBitmap);

        newCanvas.drawBitmap(bitmap, 0, 0, null);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
        String captionString = dateTime;
        Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(50);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

        Rect rectText = new Rect();
        paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

        newCanvas.drawText(captionString,
                newCanvas.getWidth()-rectText.right,
                newCanvas.getHeight()-rectText.bottom, paintText);


        FileOutputStream out = null;
        try {
            //int permissionCheck = ContextCompat.checkSelfPermission(contexto, Manifest.permission.WRITE_EXTERNAL_STORAGE);


            out = new FileOutputStream(foto);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out); // bmp is your Bitmap instance
            view.setImageBitmap(newBitmap);
            // PNG is a lossless format, the compression factor (100) is ignored
            Toast.makeText(getContexto(),"Fecha Estampada",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContexto(),"Fecha NO Estampada",Toast.LENGTH_LONG).show();

            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public Bitmap decodeSampledBitmapFromResource(Uri output2, int reqWidth, int reqHeight) throws FileNotFoundException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeStream(this.contexto.getContentResolver().openInputStream(output2));

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(this.contexto.getContentResolver().openInputStream(output2));
    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight  && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void verificarPermisosEscritura(){
        if (ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(actividadActual,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(actividadActual,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

}

