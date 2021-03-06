
package com.blue.sky.mobile.manager.music.ui.fragments.list;

import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.blue.sky.mobile.manager.R;
import com.blue.sky.mobile.manager.music.helpers.utils.ApolloUtils;
import com.blue.sky.mobile.manager.music.helpers.utils.MusicUtils;
import com.blue.sky.mobile.manager.music.ui.adapters.list.ArtistAlbumAdapter;
import com.blue.sky.mobile.manager.music.ui.fragments.base.ListViewFragment;

import static com.blue.sky.mobile.manager.music.Constants.*;

public class ArtistAlbumsFragment extends ListViewFragment{

    public ArtistAlbumsFragment(Bundle args) {
        setArguments(args);
    }    

    public void setupFragmentData(){
        mAdapter =new ArtistAlbumAdapter(getActivity(), R.layout.music_listview_items, null,
                									new String[] {}, new int[] {}, 0);
    	mProjection = new String[] {
                BaseColumns._ID, AlbumColumns.ALBUM, AlbumColumns.NUMBER_OF_SONGS,
                AlbumColumns.ARTIST
        };
        mSortOrder =  Audio.Albums.DEFAULT_SORT_ORDER;
        long artistId = getArguments().getLong((BaseColumns._ID));
        mUri = Audio.Artists.Albums.getContentUri(EXTERNAL, artistId);
        mFragmentGroupId = 7;
        mType = TYPE_ALBUM;     
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    	ApolloUtils.startTracksBrowser(mType, id, mCursor, getActivity());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(mFragmentGroupId, PLAY_SELECTION, 0, getResources().getString(R.string.play_all));
        menu.add(mFragmentGroupId, ADD_TO_PLAYLIST, 0, getResources().getString(R.string.add_to_playlist));
        menu.add(mFragmentGroupId, SEARCH, 0, getResources().getString(R.string.search));
        mCurrentId = mCursor.getString( mCursor.getColumnIndexOrThrow( BaseColumns._ID ) );       
        menu.setHeaderView( ApolloUtils.setHeaderLayout( mType, mCursor, getActivity() ) );
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	if(item.getGroupId() == mFragmentGroupId){
	        switch (item.getItemId()) {
	            case PLAY_SELECTION: {
	                long[] list = MusicUtils.getSongListForAlbum(getActivity(),
	                        Long.parseLong(mCurrentId));
	                MusicUtils.playAll(getActivity(), list, 0);
	                break;
	            }
	            case ADD_TO_PLAYLIST: {
	                Intent intent = new Intent(INTENT_ADD_TO_PLAYLIST);
	                long[] list = MusicUtils.getSongListForAlbum(getActivity(),
	                        Long.parseLong(mCurrentId));
	                intent.putExtra(INTENT_PLAYLIST_LIST, list);
	                getActivity().startActivity(intent);
	                break;
	            }
	            case SEARCH: {
	                MusicUtils.doSearch(getActivity(), mCursor, mCursor.getColumnIndexOrThrow(AlbumColumns.ALBUM));
	                break;
	            }
	            default:
	                break;
	        }
	        return true;
    	}
        return super.onContextItemSelected(item);
    }
}
