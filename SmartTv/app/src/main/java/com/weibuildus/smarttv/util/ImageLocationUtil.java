package com.weibuildus.smarttv.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 本地图片处理工具
 * Created by Administrator on 2016/5/10.
 */
public class ImageLocationUtil {

    /**
     * 根据本地图片文件路径获取文件名，失败返回当前时间毫秒值为文件名.jpg为后缀
     *
     * @param path
     * @return
     */
    public static String getLocationImageFileName(String path) {
        if (path != null) {
            int start = path.lastIndexOf("/");
            if (start != -1 && path.length() > start) {
                return path.substring(start, path.length());
            }
        }
        return System.currentTimeMillis() + ".jpg";
    }

    /**
     * 保存Bitmap到文件
     *
     * @param file
     * @param bitmap
     */
    public static void saveBitmapToFile(File file, Bitmap bitmap) {
        FileOutputStream fOut = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
                fOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 图片压缩，将图片压缩到指定大小以下
     *
     * @param source               原文件
     * @param target               目标存储文件
     * @param compress_target_size 目标大小左右
     * @return
     */
    public static void compressImage(File source, File target, int compress_target_size) {

        try {
            // 不存在则创建目录
            if (!target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
            }
            // 如果源文件存在，并且大小小于限制值
            if (source.exists() && source.length() < compress_target_size * 1024) {
                //拷贝源文件到目标文件
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream(source));
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(target));
                int length = 0;
                byte[] buffer = new byte[1024 * 10];
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                    out.flush();
                }
                in.close();
                out.close();
                return;
            }
            //否则开始进行压缩
            //压缩前的初始化
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //读取图片信息
            BitmapFactory.decodeFile(source.getAbsolutePath(), options);
            options.inJustDecodeBounds = false;
            //获取图片质量参数，确定像素点站位数
            int onePxBit = 1;
            if (options.inPreferredConfig == Bitmap.Config.ALPHA_8) {
                onePxBit = 1;
            } else if (options.inPreferredConfig == Bitmap.Config.ARGB_4444
                    || options.inPreferredConfig == Bitmap.Config.RGB_565) {
                onePxBit = 2;
            } else {
                onePxBit = 4;
            }
            //计算目标宽度
            int target_w = (int) Math.sqrt((compress_target_size * 10.0f * 1024 / onePxBit) * options.outWidth / options.outHeight);
            //粗略计算缩放倍数
            options.inSampleSize = options.outWidth / target_w + 1;
            Logger.d("compressImage", "图片压缩: 缩放比列" + options.inSampleSize + "; 压缩前文件大小：" + source.length() / 1024 + "KB");
            //读取缩放比例图片
            Bitmap bitmap = BitmapFactory.decodeFile(source.getAbsolutePath(), options);
            //创建输出流
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(target));
            //用质量80压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            stream.flush();
            stream.close();
            Logger.d("compressImage", "图片压缩: 压缩结束，文件大小：" + target.length() / 1024 + "KB");
            //压缩结束
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
