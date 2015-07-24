package contactsTool;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import entity.Contacts;
import entity.Message;
import sqlite.BlackSQLiteHelper;
import sqlite.InterceptSqLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * project:com.example.android_day0701_venn_homework.contactsTool
 * user:VennUser
 * date:2015/7/1
 */
public class ContactsTool {
	private static SQLiteDatabase blackDatabase;

	//get contacts name and phone number
	public static List<Contacts> getConContactsNumber(Context context) {

		List<Contacts> contactses = new ArrayList<Contacts>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String rawName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
		String rawPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;
		Cursor cursor = resolver.query(uri, new String[]{rawName, rawPhone}, null, null, rawName);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Contacts contacts = new Contacts();

				//get name
				int indexName = cursor.getColumnIndex(rawName);
				if (indexName > -1) {
					String name = cursor.getString(indexName);
					contacts.setName(name);
				}

				//get phone number
				int indexPhone = cursor.getColumnIndex(rawPhone);
				if (indexPhone > -1) {
					String phoneNumber = cursor.getString(indexPhone);
					contacts.setPhoneNumber(phoneNumber);
				}
				contactses.add(contacts);
			}
			cursor.close();
		}
		return contactses;
	}

	//get email

	//get message
	public static List<Message> getMessage(Context context) {
		List<Message> messages = new ArrayList<Message>();
		List<Contacts> contactsList = getConContactsNumber(context);
		Uri uri = Telephony.Sms.CONTENT_URI;
		ContentResolver resolver = context.getContentResolver();
		String rawAddress = Telephony.Sms.ADDRESS;
		String rawBody = Telephony.Sms.BODY;
		String rawTime = Telephony.Sms.DATE;
		Cursor cursor = resolver.query(uri, new String[]{rawAddress, rawBody, rawTime}, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Message message = new Message();

				//get person
				int indexAddress = cursor.getColumnIndex(rawAddress);
				if (indexAddress > -1) {
					String address = cursor.getString(indexAddress);
					message.setNumber(address);
					//String name = getMessageSender(contactsList, address);
					String name2 = getMessageName(context, address);
					message.setPerson(name2);
				}

				//get body
				int indexBody = cursor.getColumnIndex(rawBody);
				if (indexBody > -1) {
					String body = cursor.getString(indexBody);
					message.setContent(body);
				}

				//get time
				int indexTime = cursor.getColumnIndex(rawTime);
				if (indexTime > -1) {
					long time = cursor.getLong(indexTime);
					message.setTime(time);
				}
				messages.add(message);
			}
			cursor.close();
		}
		return messages;
	}

	//TODO bug
	public static String getMessageName(Context context, String Address) {
		String name = null;
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String rawAddress = ContactsContract.CommonDataKinds.Phone.NUMBER;
		String rawName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
		try {
			Cursor cursor = resolver.query(uri, new String[]{rawName}, rawAddress + "= ?", new String[]{Address}, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					int indexName = cursor.getColumnIndex(rawName);
					if (indexName > -1) {
						//TODO bug: index = 0
						name = cursor.getString(indexName);
					}
				}
				cursor.close();
			}
		} catch (Exception e) {
			name = "android";
		}
		return name;
	}

	private static String getMessageSender(List<Contacts> list, String number) {
		String name = null;
		for (Contacts contact : list) {
			if (contact.getPhoneNumber().equals(number)) {
				name = contact.getName();
			}
		}
		return name;
	}

	public static void addContacts(Context context, Contacts contacts) {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();

		//insert data into raw_contacts table and return the Uri of the table
		values.put(ContactsContract.Contacts.DISPLAY_NAME, "insert");
		Uri uri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);

		//insert name into data table
		long raw_contact_id = ContentUris.parseId(uri);
		ContentValues valuesName = new ContentValues();

		//inset raw_contacts_id into data first
		valuesName.put(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id);

		//insert name into data table
		valuesName.put("data1", contacts.getName());

		//assign mime_type to name in data table
		valuesName.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);

		//insert name into data table
		resolver.insert(ContactsContract.Data.CONTENT_URI, valuesName);

		//insert number into data
		ContentValues valuesNumber = new ContentValues();
		valuesNumber.put(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id);
		valuesNumber.put("data1", contacts.getPhoneNumber());
		valuesNumber.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		resolver.insert(ContactsContract.Data.CONTENT_URI, valuesNumber);
	}

	public static int deleteContacts(Context context, Contacts contacts) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = ContactsContract.RawContacts.CONTENT_URI;
		int count = resolver.delete(uri, "display_name = ?", new String[]{contacts.getName()});
		return count;
	}

	public static void addBlack(Context context, Contacts contacts) {
		if (blackDatabase == null) {
			blackDatabase = new BlackSQLiteHelper(context, "black.db", null, 1).getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		List<Contacts> contactsList = selectBlack();
		if (contacts != null) {
//			for (Contacts contact : contactsList) {
//				if (!contact.getName().equals(contacts.getName())) {
//					values.put("name", contacts.getName());
//					values.put("number", contacts.getPhoneNumber());
//					blackDatabase.insert("black", null, values);
//				}
//			}
			values.put("name", contacts.getName());
			values.put("number", contacts.getPhoneNumber());
			blackDatabase.insert("black", null, values);
		}
	}

	public static List<Contacts> selectBlack() {
		List<Contacts> contactsList = new ArrayList<Contacts>();
		Cursor cursor = blackDatabase.query("black", null, null, null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Contacts contacts = new Contacts();

				//get name
				int indexName = cursor.getColumnIndex("name");
				if (indexName > -1) {
					String name = cursor.getString(indexName);
					contacts.setName(name);
				}

				//get phone number
				int indexPhone = cursor.getColumnIndex("number");
				if (indexPhone > -1) {
					String phoneNumber = cursor.getString(indexPhone);
					contacts.setPhoneNumber(phoneNumber);
				}
				contactsList.add(contacts);
			}
			cursor.close();
		}
		return contactsList;
	}

	public static int deleteBlack(Contacts contacts) {
		int count = blackDatabase.delete("black", "name = ?", new String[]{contacts.getName()});
		return count;
	}

	public static long addIntercept(Context context, Message message) {
		if (blackDatabase == null) {
			blackDatabase = new BlackSQLiteHelper(context, "intercept.db", null, 1).getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		long id = -1;
		if (message != null) {
			values.put("sender", message.getPerson());
			values.put("number", message.getNumber());
			values.put("content", message.getContent());
			values.put("time", message.getTime());
			id = blackDatabase.insert("intercept", null, values);
		}
		return id;
	}

	public static List<Message> selectIntercept() {
		List<Message> interceptList = new ArrayList<Message>();
		Cursor cursor = blackDatabase.query("intercept", null, null, null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Message message = new Message();

				int indexName = cursor.getColumnIndex("sender");
				if (indexName > -1) {
					String name = cursor.getString(indexName);
					message.setPerson(name);
				}

				int indexPhone = cursor.getColumnIndex("number");
				if (indexPhone > -1) {
					String phoneNumber = cursor.getString(indexPhone);
					message.setNumber(phoneNumber);
				}

				int indexContent = cursor.getColumnIndex("content");
				if (indexContent > -1) {
					String content = cursor.getString(indexContent);
					message.setContent(content);
				}

				int indexTime = cursor.getColumnIndex("time");
				if (indexTime > -1) {
					long time = cursor.getLong(indexTime);
					message.setTime(time);
				}
				interceptList.add(message);
			}
			cursor.close();
		}
		return interceptList;
	}
}