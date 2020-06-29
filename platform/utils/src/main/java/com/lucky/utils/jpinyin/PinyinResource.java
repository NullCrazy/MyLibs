package com.lucky.utils.jpinyin;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * 资源文件加载类
 *
 * @author stuxuhai (dczxxuhai@gmail.com)
 * @version 1.0
 */
public class PinyinResource {

	private static Properties getResource(Context context, String resourceName) {
		AssetManager assetManager = context.getAssets() == null? context.getResources().getAssets():context.getAssets();
		InputStream is = null;
		try {
			is = assetManager.open("data/" + resourceName, AssetManager.ACCESS_STREAMING);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (is!=null){
					is.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return props;
	}

	protected static Properties getPinyinTable(Context context) {
		String resourceName = "pinyin.db";
		return getResource(context,resourceName);
	}

	protected static Properties getMutilPintinTable(Context context) {
		String resourceName = "mutil_pinyin.db";
		return getResource(context,resourceName);
	}

	protected static Properties getChineseTable(Context context) {
		String resourceName = "chinese.db";
		return getResource(context,resourceName);
	}
}
