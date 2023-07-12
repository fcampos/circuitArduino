/*
 * Copyright 2018
 * Kollins Lima (kollins.lima@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kollins.project.sofia.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kollins.project.sofia.UCModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kollins on 3/20/18.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static String dbName = "instruction_db";
    private static String dbPath = "";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase mDataBase;
    private final Context MY_CONTEXT;
    private boolean mNeedUpdate = false;

    public DataBaseHelper(Context context) {
        super(context, dbName, null, DB_VERSION);
        dbPath = context.getApplicationInfo().dataDir + "/databases/";
        this.MY_CONTEXT = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(dbPath + dbName);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(dbPath + dbName);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException e) {
                Log.e(UCModule.MY_LOG_TAG,"ErrorCopyingDataBase",e);
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = null;
        OutputStream mOutput = null;
        try {
            mInput = MY_CONTEXT.getAssets().open(dbName);
            mOutput = new FileOutputStream(dbPath + dbName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
        } finally {
            if (mInput != null) {
                mInput.close();
            }
            if (mOutput != null) {
                mOutput.close();
            }
        }
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(dbPath + dbName, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }
}
