package com.blue.sky.mobile.manager.music.ui.adapters.base;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import com.blue.sky.mobile.manager.R;
import com.blue.sky.control.dragsort.SimpleDragSortCursorAdapter;
import com.blue.sky.mobile.manager.music.cache.ImageInfo;
import com.blue.sky.mobile.manager.music.cache.ImageProvider;
import com.blue.sky.mobile.manager.music.helpers.utils.MusicUtils;
import com.blue.sky.mobile.manager.music.views.ViewHolderList;

import java.lang.ref.WeakReference;

import static com.blue.sky.mobile.manager.music.Constants.*;

public abstract class DragSortListViewAdapter extends SimpleDragSortCursorAdapter{

    private AnimationDrawable mPeakOneAnimation, mPeakTwoAnimation;

    private WeakReference<ViewHolderList> holderReference;
    
    protected Context mContext;
    
    private ImageProvider mImageProvider;

    public String mLineOneText = null, mLineTwoText = null;
    
    public String[] mImageData = null;
    
    public long mPlayingId = 0, mCurrentId = 0;

    private final View.OnClickListener showContextMenu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.showContextMenu();
        }
    };

    public DragSortListViewAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    	mContext = context;
    	mImageProvider = ImageProvider.getInstance( (Activity) mContext );
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);

        Cursor mCursor = (Cursor) getItem(position);
        setupViewData(mCursor);

        final ViewHolderList viewholder;
        if ( view != null ) {
            viewholder = new ViewHolderList(view);
            holderReference = new WeakReference<ViewHolderList>(viewholder);
            view.setTag(holderReference.get());
        } else {
            viewholder = (ViewHolderList)convertView.getTag();
        }

        holderReference.get().mViewHolderLineOne.setText(mLineOneText);
        
        holderReference.get().mViewHolderLineTwo.setText(mLineTwoText);
        
        ImageInfo mInfo = new ImageInfo();
        mInfo.type = TYPE_ARTIST;
        mInfo.size = SIZE_THUMB;
        mInfo.source = SRC_FIRST_AVAILABLE;
        mInfo.data = mImageData;        
        mImageProvider.loadImage( viewholder.mViewHolderImage, mInfo ); 
       
        holderReference.get().mQuickContext.setOnClickListener(showContextMenu);        	
       
        if ( mPlayingId ==  mCurrentId ) {
            holderReference.get().mPeakOne.setImageResource(R.anim.music_peak_meter_1);
            holderReference.get().mPeakTwo.setImageResource(R.anim.music_peak_meter_2);
            mPeakOneAnimation = (AnimationDrawable)holderReference.get().mPeakOne.getDrawable();
            mPeakTwoAnimation = (AnimationDrawable)holderReference.get().mPeakTwo.getDrawable();
            try {
                if ( MusicUtils.mService.isPlaying() ) {
                    mPeakOneAnimation.start();
                    mPeakTwoAnimation.start();
                } else {
                    mPeakOneAnimation.stop();
                    mPeakTwoAnimation.stop();
                }
            } catch ( RemoteException e ) {
                e.printStackTrace();
            }
        } else {
            holderReference.get().mPeakOne.setImageResource(0);
            holderReference.get().mPeakTwo.setImageResource(0);
        }
        return view;
    }
    
    public abstract void setupViewData( Cursor mCursor );
}
