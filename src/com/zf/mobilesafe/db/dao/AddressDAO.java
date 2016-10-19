package com.zf.mobilesafe.db.dao;

import android.database.sqlite.SQLiteDatabase;

public class AddressDAO {

	/**
	 * 根据号码得到归属地
	 */
	private static final String PATH = "data/data/com.zf.mobilesafe/files/address.db";

	public static String getAddress(String number) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null,
				SQLiteDatabase.OPEN_READONLY);
		db.rawQuery(
				"select location from data2 where id = (select outkey from data1 where id = ?)",
				new String[] {number.substring(0, 7)});
		return null;
	}
}
