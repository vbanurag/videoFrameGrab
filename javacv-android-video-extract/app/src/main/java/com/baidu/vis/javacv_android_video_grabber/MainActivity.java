package com.baidu.vis.javacv_android_video_grabber;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.vis.javacv_android_video_grabber.filechooser.ChooserDialog;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


//@EActivity(R.layout.sample_video)
public class MainActivity extends AppCompatActivity  {

    @BindView(R.id.image_view)
    ImageView img;

    @BindView(R.id.parse_video)
    Button video;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_video);
        ButterKnife.bind(this);

//        video.setOnClickListener(this);
    }

    private static final String TAG = "SampleVideo";

    @OnClick(R.id.parse_video)
    public void onButtonClick(View v) {
        Log.d(TAG, "onButtonClick: ");
        new ChooserDialog().with(MainActivity.this)
                .withStartFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath())
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(final String path, File pathFile) {
                        startVideoParsing(path);
                    }
                })
                .build()
                .show();
    }

    @BindView(R.id.output)
    TextView outputTv;

//    @AfterViews
//    void initPredictor() {
////        Predictor.init(this);
//    }


    private void startVideoParsing(final String path) {
        Toast.makeText(MainActivity.this,
                "分析视频 " + path,
                Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doConvert(path);
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doConvert(String path) throws
            FrameGrabber.Exception,
            FrameRecorder.Exception,
            IOException {
        final LinearLayout layout = (LinearLayout) findViewById(R.id.test);
        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(path);
        Frame frame;
        int count = 0;
        videoGrabber.start();
        AndroidFrameConverter bitmapConverter = new AndroidFrameConverter();
        while (true) {
            long startRenderImage = System.nanoTime();
            frame = videoGrabber.grabImage();
            if (frame == null) {
                break;
            }
            if (frame.image == null) {
                continue;
            }
            count++;

            final Bitmap currentImage = bitmapConverter.convert(frame);
//            final ArrayList<GestureBean> rst = Predictor.predict(currentImage, this);
            long endRenderImage = System.nanoTime();
            final Float renderFPS = 10000000000.0f / (endRenderImage - startRenderImage + 1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    outputTv.setText(String.format("读取数据FPS："));
                    ImageView image = new ImageView(getApplicationContext());
                    image.setLayoutParams(new android.view.ViewGroup.LayoutParams(400,400));
                    image.setMaxHeight(400);
                    image.setMaxWidth(400);
                    image.setId((int) Math.random());
                    image.setImageBitmap(currentImage);

                    // Adds the view to the layout
                    layout.addView(image);
                    img.setImageBitmap(currentImage);
                }
            });
        }
    }

//    @Override
//    public void onClick(View view) {
//        Log.d(TAG, "onButtonClick: ");
//        new ChooserDialog().with(MainActivity.this)
//                .withStartFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath())
//                .withChosenListener(new ChooserDialog.Result() {
//                    @Override
//                    public void onChoosePath(final String path, File pathFile) {
//                        startVideoParsing(path);
//                    }
//                })
//                .build()
//                .show();
//    }
}