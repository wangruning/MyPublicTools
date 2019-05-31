package io.l0neman.utils.stay.file;

import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import io.l0neman.utils.stay.io.Closer;
import io.l0neman.utils.stay.io.IOUtils;

/**
 * Created by l0neman on 2019/05/13.
 */
public class ZipUtils {

  public static void unzip(File zip, String outDir, boolean isForce) throws ZipException {
    final File out = new File(outDir);
    if (out.isFile()) {
      throw new ZipException("outDir invalid: it's a file.");
    }

    try {
      EasyFile.createDir(out);
    } catch (EasyFile.EasyFileException e) {
      throw new ZipException("outDir invalid: mkdir error: " + e.getMessage());
    }

    final boolean isClose = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    Closer closer = isClose ? new Closer() : null;
    try {
      ZipFile zipFile = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT ?
          closer.register(new ZipFile(zip)) : new ZipFile(zip);

      final Enumeration<? extends ZipEntry> entries = zipFile.entries();

      while (entries.hasMoreElements()) {
        final ZipEntry zipEntry = entries.nextElement();

        if (zipEntry.isDirectory()) {
          continue;
        }

        final String entryFileName = EasyFile.makeFileName(outDir, zipEntry.getName());
        final File file = new File(entryFileName);

        // 不覆盖已存在文件。
        if (!isForce && file.exists()) {
          continue;
        }

        File entryFile = EasyFile.createFile(file);
        unzipEntry(zipFile, zipEntry.getName(), entryFile);
      }
    } catch (Exception e) {
      throw new ZipException("unzip error: " + e.getMessage());
    } finally {
      if (isClose) { closer.close(); }
    }
  }

  private static void unzipEntry(ZipFile zip, String entryName, File outFile) throws IOException {
    ZipEntry entry = new ZipEntry(entryName);

    final InputStream zipIs = zip.getInputStream(entry);
    final FileOutputStream out = new FileOutputStream(outFile);

    IOUtils.transfer(zipIs, out, true);
  }

  public static void unzipEntry(File zip, String entryName, File outFile) throws ZipException {
    if (outFile.exists() && outFile.isDirectory()) {
      throw new ZipException("unzip error: outFile invalid.");
    }

    try {
      EasyFile.createFile(outFile);
    } catch (EasyFile.EasyFileException e) {
      throw new ZipException("unzip error: outFile create error: " + e.getMessage());
    }

    final boolean isClose = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    Closer closer = isClose ? new Closer() : null;
    try {
      ZipFile zipFile = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
          closer.register(new ZipFile(zip)) : new ZipFile(zip);

      ZipEntry entry = new ZipEntry(entryName);

      final InputStream zipIs = zipFile.getInputStream(entry);
      final FileOutputStream out = new FileOutputStream(outFile);

      if (zipIs == null) {
        throw new ZipException("entry not exist: " + entryName);
      }

      IOUtils.transfer(zipIs, out, true);

    } catch (IOException e) {
      throw new ZipException("unzip error: " + e.getMessage());

    } finally {
      if (isClose) { closer.close(); }
    }
  }

}
