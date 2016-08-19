package com.miqtech.master.client.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileManager {
	private static final String ASSETS_FOLDER_NAME = "WYMaster";
	private static final String CACHE_FOLDER_NAME = "Cache";
	public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString() + "/" + ASSETS_FOLDER_NAME + "/";

	public static final String TEMP = ".tmp";
	public static final int BUFFER_SIZE = 16 * 1024;
	public static final String TEXT_FILE_ENCODING = "GBK";
	public static final String USER_FOLDER = "User";

	public static final String INVITE_CODING_NAME = "inviteCodingImage.png";

	/**
	 * 创建文件夹
	 */
	public static File createDir(String dirPath) {
		File dir = new File(dirPath);
		dir.mkdirs();
		return dir;
	}

	public static File createFile(String filePath) {
		File file = new File(filePath);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 判断文件夹或文件是否存在
	 */
	public static boolean exists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}


	public static File getFile(Context context, String folderName, String fileName) {
		String myFolderName = FILE_PATH + folderName;
		createDir(myFolderName);
		return createFile(myFolderName + "/" + fileName);
	}

	public static File getCacheFile(Context context, String fileName) {
		String cacheFolderName = FILE_PATH + CACHE_FOLDER_NAME;
		createDir(cacheFolderName);
		return createFile(cacheFolderName + "/" + fileName);
	}

	public static File createAndGetFolder(Context context, String folderName) {
		return createDir(FILE_PATH+ folderName);
	}

	public static String[] getFileArray(Context context, String folderName) {
		String[] array = new String[] {};
		File dir = new File(FILE_PATH  + folderName);
		if (dir.exists()) {
			array = dir.list();
		}
		return array;
	}

	public static void deleteFile(Context context, String folderName, String fileName) {
		File file = new File(FILE_PATH  + folderName + "/" + fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete();
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]);
				}
			}
			file.delete();
		}
	}

	public static String getData(Context context, String folderName, String fileName, String encoding) {
		String data = "";
		if (exists(FILE_PATH+ folderName)) {
			try {
				File file = new File(FILE_PATH+ folderName + "/" + fileName);
				FileInputStream fis = new FileInputStream(file);
				byte[] bytes = new byte[fis.available()];
				fis.read(bytes);
				if (!TextUtils.isEmpty(encoding)) {
					data = new String(bytes, encoding);
				} else {
					data = new String(bytes);
				}
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public static String getData(Context context, String folderName, String fileName) {
		return getData(context, folderName, fileName, null);
	}

	/**
	 * 保存文本
	 */
	public static void saveData( String data, String folderName, String fileName) {
		String myFolderName = FILE_PATH  + folderName;
		createDir(myFolderName);
		try {
			File file = createFile(myFolderName + "/" + fileName);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void copyFile(InputStream is, String coypFolder, String fileName) throws IOException {
		createDir(FILE_PATH + coypFolder);
		File tempFile = new File(FILE_PATH + coypFolder + "/" + fileName + TEMP);
		File file = new File(FILE_PATH + coypFolder + "/" + fileName);
		OutputStream os = null;
		if (!file.exists()) {
			try {
				tempFile.createNewFile();
				os = new FileOutputStream(tempFile);
				copyStream(is, os);
				os.flush();
				os.close();
				tempFile.renameTo(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		int count = is.read(bytes);
		while (count != -1) {
			os.write(bytes, 0, count);
			count = is.read(bytes);
		}
	}

}
