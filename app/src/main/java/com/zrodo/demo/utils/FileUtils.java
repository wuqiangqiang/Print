package com.zrodo.demo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {


    /**
     * 新建多级文件夹
     *
     * @param aPath 完整路径
     * @return
     */
    public static boolean MakeDirs(String aPath) {
        boolean nIsExist = false;
        File nPath = new File(aPath);
        nIsExist = nPath.exists();
        if (!nIsExist)
            nIsExist = nPath.mkdirs();
        return nIsExist;
    }

    /**
     * 由文件全路径得到文件名
     *
     * @param aFileName
     * @return
     */
    public static String GetShortFileName(String aFileName) {
        int nPos = aFileName.lastIndexOf("/");
        if (nPos >= 0)
            return aFileName.substring(nPos + 1);
        else {
            nPos = aFileName.lastIndexOf("\\");
            if (nPos >= 0)
                return aFileName.substring(nPos + 1);
            else
                return "0";
        }
    }

    /**
     * 删除文件
     */
    public static boolean delFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            boolean result = file.delete();
            return result;
        }
        return false;
    }

    private List<String> filePaths = new ArrayList<String>();

    /**
     * 获得目录下所有文件
     */
    public List<String> getFiles(File dir, String extension) {
        if (!dir.isDirectory())
            return filePaths;

        File[] allFile = dir.listFiles();
        if (allFile == null)
            return filePaths;

        for (int i = 0; i < allFile.length; i++) {
            if (allFile[i].isDirectory()) {
                String tmp = allFile[i].getName();
                if (tmp != null) {
                    if (tmp.length() > 0) {
                        if (tmp.charAt(0) != '.') {
                            getFiles(allFile[i], extension);
                        }
                    }
                }
            } else if (allFile[i].isFile()) {
                String path = allFile[i].getAbsolutePath();
                if (checkExtension(
                        path.substring(path.length() - extension.length()),
                        extension))
                    filePaths.add(path);
            } else
                return filePaths;
        }

        return filePaths;

    }


    /**
     * delete all files or dir
     */
    public static boolean deleteFiles(String delpath) throws Exception {
        try {
            File file = new File(delpath);
            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(delpath + "/" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        //is dir go on delete files
                        deleteFiles(delpath + "\\" + filelist[i]);
                    }
                }
                file.delete();
            }
        } catch (FileNotFoundException e) {
            Log.e("File", "deleteFiles() Exception = " + e.getMessage());
        }
        return true;
    }

    /**
     * 只删除文件或者文件夹下的文件
     * 不删除文件夹
     */
    public static void RecursionDeleteFile(File file) throws Exception {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }


    /**
     * Check if OS version has built-in external cache dir method.
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 获得目录下所有文件
     */
    public List<String> getFiles(File dir, String[] extensions) {
        if (!dir.isDirectory())
            return filePaths;

        File[] allFile = dir.listFiles();
        if (allFile == null)
            return filePaths;

        for (int i = 0; i < allFile.length; i++) {
            if (allFile[i].isDirectory()) {
                String tmp = allFile[i].getName();
                if (tmp != null) {
                    if (tmp.length() > 0) {
                        if (tmp.charAt(0) != '.') {
                            getFiles(allFile[i], extensions);
                        }
                    }
                }

            } else if (allFile[i].isFile()) {
                String path = allFile[i].getAbsolutePath();
                for (int j = 0; j < extensions.length; j++)
                    if (checkExtension(
                            path.substring(path.length()
                                    - extensions[j].length()), (extensions[j])))
                        filePaths.add(path);
            } else
                return filePaths;
        }

        return filePaths;

    }

    private boolean checkExtension(String extension1, String extension2) {
        if (extension1.length() != extension2.length())
            return false;
        else {
            String tmp = "";
            char tmpc;

            for (int i = 0; i < extension1.length(); i++) {
                tmpc = extension1.charAt(i);
                if (tmpc >= 'A' && tmpc <= 'Z')
                    tmp += (tmpc + 32);
                else
                    tmp += tmpc;
            }
            return tmp.equals(extension2);
        }
    }

    /**
     * 读取文件 并且进行Base64转码
     *
     * @param path
     * @throws IOException
     */
    public static String getFileToBase64(String path) throws IOException {
        File file = new File(path);
        @SuppressWarnings("resource")
        FileInputStream in = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length() + 100];
        int length = in.read(buffer);
        String data = Base64.encodeToString(buffer, 0, length, Base64.DEFAULT);
        return data;
    }

    /**
     * 吧base64的string值转化为byte[]然后保存
     *
     * @param path
     * @throws IOException
     */
    public static boolean getBase64ToFile(String imgStr, String imgFilePath) {
        if (imgStr == null) // 图像数据为空
            return false;

        try {
            // Base64解码
            byte[] bytes = Base64.decode(imgStr, Base64.DEFAULT);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//	/**
//	 * 把文件转化为文件流并进行base64编码成string
//	 * sun 公司的方法不安全，无doc
//	 * 可以在Project->Properties->Libraries->add Libraries(jdk1.7:JRE System Library)->access **
//	 * */
//	public static String GetImageStr(String imgFilePath) {
//		byte[] data = null;  
//		  
//		// 读取图片字节数组  
//		try {  
//			InputStream in = new FileInputStream(imgFilePath);  
//			data = new byte[in.available()];  
//			in.read(data);  
//			in.close();  
//		} catch (IOException e) {  
//			e.printStackTrace();  
//		}  
//		  
//		// 对字节数组Base64编码  
//		BASE64Encoder encoder = new BASE64Encoder();  
//		return encoder.encode(data);// 返回Base64编码过的字节数组字符串  
//	}  
//		  
//	/**
//	 * 把string进行base64解码后保存成图片
//	 * sun 公司的方法不安全，无doc
//	 * 可以在Project->Properties->Libraries->add Libraries(jdk1.7:JRE System Library)->access **
//	 * */
//	public static boolean GenerateImage(String imgStr, String imgFilePath) {
//		if (imgStr == null) // 图像数据为空  
//		return false;  
//		
//		BASE64Decoder decoder = new BASE64Decoder();  
//		try {  
//			// Base64解码  
//			byte[] bytes = decoder.decodeBuffer(imgStr);  
//			for (int i = 0; i < bytes.length; ++i) {  
//				if (bytes[i] < 0) {// 调整异常数据  
//					bytes[i] += 256;  
//				}  
//			}  
//			// 生成jpeg图片  
//			OutputStream out = new FileOutputStream(imgFilePath);  
//			out.write(bytes);  
//			out.flush();  
//			out.close();  
//			return true;  
//		} catch (Exception e) {  
//			return false;  
//		}  
//	}  


    /**
     * scale bitmap:you want whatsize ,get what size
     *
     * @param bitmap
     * @param sacleWidth
     * @param sacleHeight
     */
    public static Bitmap justScaleBitmap(Bitmap bmp, int scaleWidth, int scaleHeight) {
        bmp = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, false);
        return bmp;
    }

    /**
     * 写文件 aFileName 完整文件名 abtyes字节数组 aIsAppendFile 是否为追加的方式
     */
    public static void WriteFile(String aFileName, byte[] abtyes, boolean aIsAppendFile) {
        try {
            // 2.1的时候为：/sdcard，所以使用静态方法得到路径会好一点。
            File saveFile = new File(aFileName);
            FileOutputStream outStream = new FileOutputStream(saveFile, aIsAppendFile);
            outStream.write(abtyes);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取指定的文件 aFileName 绝对路径 返回String 值
     */
    public static String ReadFile(String aFileName) throws Exception {
        try {
            byte[] result = null;
            result = ReadFileEx(aFileName);
            String nStr = new String(result);
            return nStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 读取指定的文件 aFileName 绝对路径 返回byte数组
     */
    public static byte[] ReadFileEx(String aFileName) {
        try {
            byte[] result = null;
            FileInputStream fis = null;
            try {
                File file = new File(aFileName);
                fis = new FileInputStream(file);
                result = new byte[fis.available()];
                fis.read(result);
            } catch (Exception e) {
            } finally {
                fis.close();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制asset目录先的文件 到指定目录
     *
     * @param assetFileName asset目录先的文件名
     * @param dstPath       复制文件的目标路径
     * @param isCreateNew   文件存在 是否 也需要复制 ？ true 文件存在 删掉 原来的 再复制 false 文件存在 则不复制
     * @return
     */
    public static boolean copyFile(Context ctx, String assetFileName, String dstPath, boolean isCreateNew) {
        AssetManager asset = ctx.getAssets();
        try {
            File mFile = new File(dstPath + assetFileName);
            if (!mFile.exists() || isCreateNew) {
                mFile.createNewFile();
            } else {
                return true;
            }
            InputStream inputstream = asset.open(assetFileName);
            OutputStream outputstream = new FileOutputStream(mFile);
            byte[] temp = new byte[1024];
            int len = 0;
            while ((len = inputstream.read(temp)) > 0) {
                outputstream.write(temp, 0, len);
            }
            inputstream.close();
            outputstream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 复制asset目录先的文件 到指定目录
     *
     * @param assetFileName asset目录先的文件名
     * @param dstPath       复制文件的目标路径
     * @param isCreateNew   文件存在 是否 也需要复制 ？ true 文件存在 删掉 原来的 再复制 false 文件存在 则不复制
     * @return
     */
    public static boolean copyFileN(String SrcFile, String dstFile, boolean isCreateNew) {
        try {
            File mFile = new File(dstFile);
            if (!mFile.exists() || isCreateNew) {
                mFile.createNewFile();
            } else {
                return true;
            }
            InputStream inputstream = new FileInputStream(SrcFile);
            OutputStream outputstream = new FileOutputStream(mFile);
            byte[] temp = new byte[1024];
            int len = 0;
            while ((len = inputstream.read(temp)) > 0) {
                outputstream.write(temp, 0, len);
            }
            inputstream.close();
            outputstream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解密
     *
     * @param fileUrl   源文件
     * @param tempUrl   临时文件
     * @param ketLength 密码长度
     * @return
     * @throws Exception
     */
    public static String decrypt(String fileUrl, String tempUrl, int keyLength) throws Exception {
        File file = new File(fileUrl);
        if (!file.exists()) {
            return null;
        }
        File dest = new File(tempUrl);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        InputStream is = new FileInputStream(fileUrl);
        OutputStream out = new FileOutputStream(tempUrl);
        byte[] buffer = new byte[1024];
        byte[] buffer2 = new byte[1024];
        byte bMax = (byte) 255;
        long size = file.length() - keyLength;
        int mod = (int) (size % 1024);
        int div = (int) (size >> 10);
        int count = mod == 0 ? div : (div + 1);
        int k = 1, r;
        while ((k <= count && (r = is.read(buffer)) > 0)) {
            if (mod != 0 && k == count) {
                r = mod;
            }
            for (int i = 0; i < r; i++) {
                byte b = buffer[i];
                buffer2[i] = b == 0 ? bMax : --b;
            }
            out.write(buffer2, 0, r);
            k++;
        }
        out.close();
        is.close();
        return tempUrl;
    }


    /**
     * 截图保存
     *
     * @param fileName
     * @param bmp
     */
    public static void savePic(String fileName, Bitmap bmp, Bitmap.CompressFormat format) {
        if (fileName != null && bmp != null) {
            FileOutputStream out = null;
            try {
                File file = new File(fileName);
                out = new FileOutputStream(file);
                if (format == null) {
                    format = Bitmap.CompressFormat.JPEG;
                }
                bmp.compress(format, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * spin/rotate bitmap：旋转图像
     *
     * @param original bmp
     * @param degress  notice:when spin is not 90,180,270,360.though the bmp will be shrink from original bmp's square.
     *                 spin into shrink to small will be outofmemary
     */
    public static Bitmap spinBmp(Bitmap bmp, float degress) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degress);   //spin = rotate,turn right 90 degress
        //matrix.preRotate(degress);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return bmp;
    }

    /**
     * 截图保存:质量
     *
     * @param fileName
     * @param bmp
     */
    public static void savePic(String fileName, Bitmap bmp, Bitmap.CompressFormat format, int Quality) {
        if (fileName != null && bmp != null) {
            FileOutputStream out = null;
            try {
                File file = new File(fileName);
                out = new FileOutputStream(file);
                if (format == null) {
                    format = Bitmap.CompressFormat.JPEG;
                }
                bmp.compress(format, Quality, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 保存byte[]到图片中
     * 一般用于预览的byte
     */
    public static void saveByte2Pic(String filename, byte[] data) {
        File jpgFile = new File(filename);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(jpgFile);
            outputStream.write(data);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 裁剪 图片
     *
     * @param src  原始图片
     * @param rect 裁剪区域
     * @return 裁剪后的图片
     */
    public static Bitmap getClipBitamap(Bitmap src, Rect rect) {
        if (src == null || src.isRecycled()) {
            return null;
        }
        int org_width = src.getWidth();
        int org_hight = src.getHeight();
        int new_left = rect.left, new_right = rect.right, new_top = rect.top, new_bottom = rect.bottom;
        if (rect.left > org_width || rect.right < 0 || rect.top > org_hight || rect.bottom < 0) {
            return null;
        }
        if (new_left < 0) {
            new_left = 0;
        }
        if (new_right > org_width) {
            new_right = org_width;
        }
        if (new_top < 0) {
            new_top = 0;
        }
        if (new_bottom > org_hight) {
            new_bottom = org_hight;
        }
        Rect src_rect = new Rect(new_left, new_top, new_right, new_bottom);
        Rect new_Rect = new Rect(0, 0, new_right - new_left, new_bottom - new_top);
        Bitmap bitmap = Bitmap.createBitmap(new_Rect.width(), new_Rect.height(), Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src, src_rect, new_Rect, null);
        src_rect = null;
        new_Rect = null;
        canvas = null;
        return bitmap;
    }


}
