package shay.murray.imagecacheviewer;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ImageViewerActivity extends AppCompatActivity {

    private Activity appContext;

    protected EditText InputPhotoLinkLocationEdit;
    protected Spinner CacheTypeSpinner;
    protected Button DownloadImageBtn;
    protected ImageView ShowDownloadImageViewer;
    protected TextView ShowImageCacheLocationText;

    private String[] cacheTypeArray = {"MemoryCache","DiskCache","DoubleCache"};
    private ImageLoader mImageLoader = new ImageLoader();
    private StringBuilder diskCacheLocationPath ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        this.appContext = this;

        InputPhotoLinkLocationEdit = (EditText) findViewById(R.id.editPhotoLinkLocation);
        DownloadImageBtn = (Button)findViewById(R.id.btnDownloadandCache);
        CacheTypeSpinner = (Spinner)findViewById(R.id.spinnerCacheType);
        ShowDownloadImageViewer = (ImageView)findViewById(R.id.imgViewer);
        ShowImageCacheLocationText = (TextView)findViewById(R.id.txtCacheLocation);

        ArrayAdapter<String> cacheAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,cacheTypeArray);
        CacheTypeSpinner.setAdapter(cacheAdapter);
        CacheTypeSpinner.setSelection(0,true);
        CacheTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectCacheTypeItem = parent.getSelectedItem().toString();
                if(selectCacheTypeItem.equals(cacheTypeArray[0])){
                    mImageLoader.setImageCache(new MemoryCache());
                    CacheTypeSpinner.setSelection(position);
                    Toast.makeText(appContext,selectCacheTypeItem,Toast.LENGTH_SHORT).show();
                }else if(selectCacheTypeItem.equals(cacheTypeArray[1])){
                    mImageLoader.setImageCache(new DiskCache(appContext));
                    CacheTypeSpinner.setSelection(position);
                    Toast.makeText(appContext,selectCacheTypeItem,Toast.LENGTH_SHORT).show();
                }else if(selectCacheTypeItem.equals(cacheTypeArray[2])){
                    mImageLoader.setImageCache(new DoubleCache(appContext));
                    CacheTypeSpinner.setSelection(position);
                    Toast.makeText(appContext,selectCacheTypeItem,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DownloadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDownloadImageViewer.setImageDrawable(getResources().getDrawable(R.drawable.imggallery));
                String imageUrl = InputPhotoLinkLocationEdit.getText().toString();
                mImageLoader.displayImage(imageUrl, ShowDownloadImageViewer);
            }
        });

        ShowImageCacheLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImageCacheLocationText.setText(imageDiskCacheLocation(appContext).toString());
            }
        });
    }

    private StringBuilder imageDiskCacheLocation(Context context) {
        try {
            diskCacheLocationPath = new StringBuilder();
            File dir = context.getFilesDir();
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    diskCacheLocationPath.append(dir.getAbsolutePath()+"\n"+children[i]+"\n");
                }
            }
            return diskCacheLocationPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
