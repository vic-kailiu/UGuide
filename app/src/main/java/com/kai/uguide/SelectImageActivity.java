package com.kai.uguide;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.FixedTabsView;
import com.astuetz.viewpager.extensions.TabsAdapter;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kai.uguide.fragment.CurrentWeatherFragment;
import com.kai.uguide.fragment.WeatherFragment;
import com.kai.uguide.utils.FileUtils;
import com.kai.uguide.viewpageradapter.ExamplePagerAdapter;
import com.kai.uguide.viewpageradapter.FixedIconTabsAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SelectImageActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, WeatherFragment.WeatherEventListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = false;
    private final int MAP_ZOOM = 16;
    private View mToolbar;
    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private int deviceHeightHalf;
    private int lastScrollY;

    private RelativeLayout introView;
    private LinearLayout.LayoutParams introParams;

    private int sectionViewMaxHeight;
    private int sectionViewMinHeight;

    private View weatherView;

    private View text_overlay;
    private RelativeLayout.LayoutParams text_overlay_params;

    private FloatingActionsMenu mFab;
    private AddFloatingActionButton mAddbutton;
    private FloatingActionButton profileButton;
    private FloatingActionButton cameraButton;
    private FloatingActionButton galleryeButton;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
    private boolean mFabIsShown;
    // Google Map
    private MapFragment mapFragment;
    private View mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private LinearLayout.LayoutParams mapParams;

    private ViewPager mPager;
    private FixedTabsView mFixedTabs;
    private PagerAdapter mPagerAdapter;
    private TabsAdapter mFixedTabsAdapter;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        initializeScrollView();
        initializeFloatingActionButton();

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();

        initializeWeatherFrag();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)//result is not correct
            return;
        //TODO: keep here for future options
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            galleryAddPic();
//        }
        if ((resultCode == RESULT_OK) && (requestCode == REQUEST_IMAGE_GALLERY)) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                mCurrentPhotoPath = FileUtils.getPath(this, uri);
            }
        }

        Intent i = new Intent(this, CropImageActivity.class);
        i.putExtra("filePath", mCurrentPhotoPath);
        startActivity(i);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY)));
        ViewHelper.setTranslationY(mImageView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY / 2)));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, Math.max(0, Math.min(1, (float) scrollY / flexibleRange)));
        ViewHelper.setAlpha(weatherView, 1 - Math.max(0, Math.min(1, (float) scrollY / flexibleRange * 2)));

        // Scale title text
        float scale = 1 + Math.max(0, Math.min(MAX_TEXT_SCALE_DELTA, (flexibleRange - scrollY) / flexibleRange));
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        mFab.collapse();

        int maxFabTranslationY = mFlexibleSpaceImageHeight;// - mFab.getHeight() / 2;
        int fabTranslationY = Math.max(mActionBarSize,// - mFab.getHeight() / 2,
                Math.min(maxFabTranslationY, -scrollY + mFlexibleSpaceImageHeight));// - mFab.getHeight() / 2));
        ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
        ViewHelper.setTranslationY(mFab, fabTranslationY - mFab.getHeight() + 56);

        // Show/hide FAB
//        if (ViewHelper.getTranslationY(mFab) < mFlexibleSpaceShowFabOffset) {
//            hideFab();
//        } else {
//            showFab();
//        }

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                setBackgroundAlpha(mToolbar, 1, mToolbarColor);
            } else {
                setBackgroundAlpha(mToolbar, 0, mToolbarColor);
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }

        updateView(introView, introParams, scrollY);
        //updateView(mapView, mapParams, scrollY - lastScrollY);

        lastScrollY = scrollY;
    }

    private void updateView(View view, LinearLayout.LayoutParams params, int scroll) {
        int startScroll = 0;
        int range = (int) getResources().getDimension(R.dimen.sectionViewMaxHeight)
                - (int) getResources().getDimension(R.dimen.sectionViewMinHeight);

        double factor = 1.0 * Math.abs(scroll - (startScroll + range))/ range;
        if (factor > 1)
            return;

        params.height = (int)getResources().getDimension(R.dimen.sectionViewMinHeight)
                + (int) (range * (1 - factor * factor));

//        if (delta > 0)
//            params.height = (int)getResources().getDimension(R.dimen.sectionViewMinHeight)
//                    + delta;
//        if (delta < 0)
//            params.height = (int)getResources().getDimension(R.dimen.sectionViewMaxHeight)
//                    - delta;

        view.setLayoutParams(params);

        text_overlay_params.height = (int) (factor * 100.0);
        text_overlay.setLayoutParams(text_overlay_params);
    }

    private void initializeScrollView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.primary);

        mToolbar = findViewById(R.id.toolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText("Singapore");
        setTitle(null);

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                //mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //mScrollView.scrollTo(0, 1);
                //mScrollView.scrollTo(0, 0);
            }
        });
    }

    private void initializeFloatingActionButton() {
        mFab = (FloatingActionsMenu) findViewById(R.id.fab);
        mAddbutton = (AddFloatingActionButton) findViewById(R.id.fab_expand_menu_button);
        profileButton = (FloatingActionButton) findViewById(R.id.profile);
        cameraButton = (FloatingActionButton) findViewById(R.id.camera);
        galleryeButton = (FloatingActionButton) findViewById(R.id.gallery);
        profileButton.setIcon(R.drawable.ic_profile);
        cameraButton.setIcon(R.drawable.ic_camera_b);
        galleryeButton.setIcon(R.drawable.ic_gallery);
//        mAddbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
        mAddbutton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                return true;
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        double foo = 1;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
        galleryeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            }
        });

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
//        ViewHelper.setScaleX(mFab, 0);
//        ViewHelper.setScaleY(mFab, 0);
    }

    private void initializeMap() {
        if (googleMap == null) {
            mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
            googleMap = mapFragment.getMap();
            mapView = (RelativeLayout) findViewById(R.id.mapView);
            mapParams = (LinearLayout.LayoutParams) mapView.getLayoutParams();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

            googleMap.setMyLocationEnabled(true); // false to disable
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location arg0) {
                    LatLng pos = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    if (marker != null)
                        marker.remove();
                    marker = googleMap.addMarker(
                            new MarkerOptions().position(pos).title("You are here!").flat(true));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, MAP_ZOOM));
                }
            });
        }
    }

    private void initializeViews() {
        deviceHeightHalf = (int) (getResources().getDisplayMetrics().heightPixels / 2);

        introView = (RelativeLayout) findViewById(R.id.textView);
        introParams = (LinearLayout.LayoutParams) introView.getLayoutParams();

        initViewPager(4, 0xFFFFFFFF, 0xFF000000);
        mFixedTabs = (FixedTabsView) findViewById(R.id.fixed_icon_tabs);
        mFixedTabsAdapter = new FixedIconTabsAdapter(this);
        mFixedTabs.setAdapter(mFixedTabsAdapter);
        mFixedTabs.setViewPager(mPager);

        text_overlay = findViewById(R.id.text_overlay);
        text_overlay_params = (RelativeLayout.LayoutParams) text_overlay.getLayoutParams();
    }

    private void initializeWeatherFrag() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransitionStyle(R.style.fragmentAnim);
        CurrentWeatherFragment cf = CurrentWeatherFragment.newInstance();
        ft.add(R.id.currentWeatherFrag, cf, "currentWeather") ;
        ft.commit();

        weatherView = findViewById(R.id.currentWeatherFrag);
    }

    private void initViewPager(int pageCount, int backgroundColor, int textColor) {
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ExamplePagerAdapter(this, pageCount, backgroundColor, textColor);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setPageMargin(1);
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }

    public void on_camera_click(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                double foo = 1;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void on_gallery_click(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //        File storageDir = Environment.getExternalStoragePublicDirectory(
        //                Environment.DIRECTORY_PICTURES);

        File storageDir;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = getExternalFilesDir(null);
        } else {
            storageDir = getFilesDir();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @Override
    public void requestCompleted() {

    }
}
