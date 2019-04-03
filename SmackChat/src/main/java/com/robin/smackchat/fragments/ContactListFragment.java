package com.robin.smackchat.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.robin.smackchat.ChatActivity;
import com.robin.smackchat.ContactRequestListActivity;
import com.robin.smackchat.MainActivity;
import com.robin.smackchat.R;
import com.robin.smackchat.adapters.ContactCursorAdapter;
import com.robin.smackchat.databases.ChatContract.ContactTable;

public class ContactListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, OnClickListener, MenuItemCompat.OnActionExpandListener {

    private TextView newContactsText;
    private View contactsDivider;

    private ContactCursorAdapter adapter;
    private String query;

    private ListView listView;

    private AdapterView.OnItemClickListener onItemClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= listView.getHeaderViewsCount();
                if (position < 0) {
                    return;
                }

                Cursor cursor = (Cursor) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_DATA_NAME_TO, cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME_JID)));
                intent.putExtra(ChatActivity.EXTRA_DATA_NAME_NICKNAME, cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME_NICKNAME)));
                startActivity(intent);
            }
        };

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        listView.setEmptyView(view.findViewById(R.id.empty));//如果现在没有好友显示未找到好友
        listView.setOnItemClickListener(onItemClickListener);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.contact_list_header, listView, false);
        listView.addHeaderView(headerView);
        newContactsText = (TextView) headerView.findViewById(R.id.tv_new_contacts);
        newContactsText.setOnClickListener(this);
        contactsDivider = headerView.findViewById(R.id.contacts_divider);

        adapter = new ContactCursorAdapter(getActivity(), null, ((MainActivity) getActivity()).getImageFetcher());
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        restartLoader(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        restartLoader(newText);

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                ContactTable._ID,
                ContactTable.COLUMN_NAME_JID,
                ContactTable.COLUMN_NAME_NICKNAME,
                ContactTable.COLUMN_NAME_STATUS
        };

        String selection = null;
        String[] selectionArgs = null;
        if (hasQueryText()) {
            selection = ContactTable.COLUMN_NAME_NICKNAME + " like ?";
            selectionArgs = new String[]{query + "%"};
        }
        //从内容提供器区获取数据
        return new CursorLoader(getActivity(), ContactTable.CONTENT_URI, projection, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void restartLoader(String query) {
        this.query = query;
        getLoaderManager().restartLoader(0, null, this);
    }

    private boolean hasQueryText() {
        return query != null && !query.equals("");
    }

    @Override
    public void onClick(View v) {
        if (v == newContactsText) {
            startActivity(new Intent(getActivity(), ContactRequestListActivity.class));
        }
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        setNewContactsVisibility(View.GONE);

        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        setNewContactsVisibility(View.VISIBLE);

        if (hasQueryText()) {
            restartLoader(null);
        }

        return true;
    }

    private void setNewContactsVisibility(int visibility) {
        newContactsText.setVisibility(visibility);
        contactsDivider.setVisibility(visibility);
    }
}