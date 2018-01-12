package com.example.dmitriysamoilov.randomchat.database;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by dmitriysamoilov on 11.01.18.
 */

public class SaveMsgHelper {
    private static final String LOG_TAG = "SaveMsgHelper";
    private Context context;

    final String GLOBAL_HISTORY = "global_history.txt";
    final String SENDER_HISTORY = "sender_history.txt";

    final String DIR_SD = "ChatHistory";

    public SaveMsgHelper(Context context) {
        this.context = context;
    }

    private String readFile(String mFileName) {
        FileInputStream stream = null;
        StringBuilder sb = new StringBuilder();
        String line = "";

        try {
            stream = context.openFileInput(mFileName);

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                stream.close();
            }

            Log.d(LOG_TAG, "Data from file: " + sb.toString());

        } catch (Exception e) {
            Log.d(LOG_TAG, "Файла нет или произошла ошибка при чтении");
        }
        return sb.toString();
    }

    public void saveToGlobalHistory(String text, String nick) {
        Log.d(LOG_TAG, "writeToFile");
        String cryptS = "";
        String history = "";
        String string = "";
        CryptHelper cryptHelper = new CryptHelper();

        try {
            cryptS = cryptHelper.encrypt(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        history = readFile(GLOBAL_HISTORY);

        string = history + cryptS + " ,nick - " + nick + " |END|\n ";
        try {
            FileOutputStream outputStream = context.openFileOutput(GLOBAL_HISTORY, context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();

        } catch (Exception e) {
            Log.d(LOG_TAG, "Произошла ошибка при записи");
        }


    }

    public void saveToSenderHistory(String text) {
        Log.d(LOG_TAG, "writeToFile");
        String cryptS = "";
        String history = "";
        String string = "";
        CryptHelper cryptHelper = new CryptHelper();

        try {
            cryptS = cryptHelper.encrypt(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        history = readFile(SENDER_HISTORY);

        string = history + cryptS + " |END| \n";
        try {
            FileOutputStream outputStream = context.openFileOutput(SENDER_HISTORY, context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();

        } catch (Exception e) {
            Log.d(LOG_TAG, "Произошла ошибка при записи");
        }
    }


}
