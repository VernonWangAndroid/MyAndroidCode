package com.example.android_day0701_venn_homework.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android_day0701_venn_homework.Adapter.ContactsAdapter;
import com.example.android_day0701_venn_homework.R;
import com.example.android_day0701_venn_homework.contactsTool.ContactsTool;
import com.example.android_day0701_venn_homework.entity.Contacts;

import java.util.List;

/**
 * project:com.example.android_day0701_venn_homework.fragment
 * user:VennUser
 * date:2015/7/1
 */
public class ContactsFragment extends Fragment {
	private static Context context;
	private MenuInflater menuInflater;
	private ListView listView;
	private ContactsAdapter adapter;
	private List<Contacts> contactsList;

	public void ContactsFragment() {

	}

	public static void getContext(Context con) {
		context = con;
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		menuInflater = activity.getMenuInflater();
	}


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contacts, container, false);
		listView = (ListView) view.findViewById(R.id.list_contacts);
		contactsList = ContactsTool.getConContactsNumber(context);
		adapter = new ContactsAdapter(context, contactsList);
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		return view;
	}

	private void updateAdapter() {
		ContactsAdapter.updateList(ContactsTool.getConContactsNumber(context));
		adapter.notifyDataSetChanged();
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		menuInflater.inflate(R.menu.menu_context, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_edit:
				break;
			case R.id.menu_delete:
				ContextMenu.ContextMenuInfo contextMenuInfo = item.getMenuInfo();
				AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = null;
				if (contextMenuInfo != null && contextMenuInfo instanceof AdapterView.AdapterContextMenuInfo) {
					adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
					Contacts contacts = contactsList.get(adapterContextMenuInfo.position);
					ContactsTool.deleteContacts(context, contacts);
					updateAdapter();
				}
				break;
		}
		return true;
	}
}