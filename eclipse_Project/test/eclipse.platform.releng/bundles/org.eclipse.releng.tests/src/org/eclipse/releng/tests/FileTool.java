/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.releng.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * A tool for performing operations on files.
 */
public class FileTool {

	/**
	 * A zip filter which is used to filter out unwanted entries while extracting a zip file.
	 *
	 * @see FileTool#unzip
	 */
	public interface IZipFilter {
		/**
		 * Returns a boolean indicating whether the entry with the
		 * specified name should be extracted from the zip file.
		 *
		 * @param fullEntryName the full entry name; includes full
		 * path segments for nested zip entries
		 * @param entryName the partial entry name; only includes
		 * path segments from the currect zip entry
		 * @param depth a number greater than or equal to zero
		 * which specifies the depth of the current nested zip
		 * entry
		 * @return a boolean indicating whether the entry with the
		 * specified name should be extracted from the zip file
		 */
		boolean shouldExtract(String fullEntryName, String entryName, int depth);
		/**
		 * Returns a boolean indicating whether the entry (which
		 * is a zip/jar file) with the specified name should be
		 * extracted from the zip file and then unzipped.
		 *
		 * @param fullEntryName the full entry name; includes full
		 * path segments for nested zip entries
		 * @param entryName the partial entry name; only includes
		 * path segments from the currect zip entry
		 * @param depth a number greater than or equal to zero
		 * which specifies the depth of the current nested zip
		 * entry
		 * @return a boolean indicating whether the entry (which
		 * is a zip/jar file) with the specified name should be
		 * extracted from the zip file and then unzipped
		 */
		boolean shouldUnzip(String fullEntryName, String entryName, int depth);
	}
	/**
	 * A buffer.
	 */
	private static byte[] buffer = new byte[8192];
	/**
	 * Returns the given file path with its separator
	 * character changed from the given old separator to the
	 * given new separator.
	 *
	 * @param path a file path
	 * @param oldSeparator a path separator character
	 * @param newSeparator a path separator character
	 * @return the file path with its separator character
	 * changed from the given old separator to the given new
	 * separator
	 */
	public static String changeSeparator(String path, char oldSeparator, char newSeparator){
		return path.replace(oldSeparator, newSeparator);
	}
	/**
	 * Returns a boolean indicating whether the given files
	 * have the same content.
	 *
	 * @param file1 the first file
	 * @param file2 the second file
	 * @return a boolean indicating whether the given files
	 * have the same content
	 */
	public static boolean compare(File file1, File file2) throws IOException {
		if(file1.length() != file2.length()){
			return false;
		}
		try (InputStream is1 = new BufferedInputStream(new FileInputStream(file1));
				InputStream is2 = new BufferedInputStream(new FileInputStream(file2))) {
			int a = 0;
			int b = 0;
			boolean same = true;
			while (same && a != -1 && b != -1) {
				a = is1.read();
				b = is2.read();
				same = a == b;
			}
			return same;
		}
	}
	/**
	 * Copies the given source file to the given destination file.
	 *
	 * @param src the given source file
	 * @param dst the given destination file
	 */
	public static void copy(File src, File dst) throws IOException {
		copy(src.getParentFile(), src, dst);
	}
	/**
	 * Copies the given source file to the given destination file.
	 *
	 * @param root
	 * @param src the given source file
	 * @param dst the given destination file
	 */
	public static void copy(File root, File src, File dst) throws IOException {
		if(src.isDirectory()){
			String[] children = src.list();
			if (children == null) {
				throw new IOException("Content from directory '" + src.getAbsolutePath() + "' can not be listed."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			for (String element : children) {
				File child = new File(src, element);
				copy(root, child, dst);
			}
		} else {
			String rootString = root.toString();
			String srcString = src.toString();
			File dstFile = new File(dst, srcString.substring(rootString.length() + 1));
			transferData(src, dstFile);
		}
	}
	/**
	 * Delete the given file or directory. Directories are
	 * deleted recursively. If the file or directory can
	 * not be deleted, a warning message is written to
	 * stdout.
	 *
	 * @param file a file or directory
	 */
	public static void delete(File file) {
		if(file.exists()){
			if(file.isDirectory()){
				String[] children = file.list();
				if(children == null) {
					children = new String[0];
				}
				for (String element : children) {
					File child = new File(file, element);
					delete(child);
				}
			}
			if(!file.delete()){
				System.out.println("WARNING: could not delete " + file);
			}

		}
	}
	/**
	 * Returns a new <code>File</code> from the given path
	 * name segments.
	 *
	 * @param segments the given path name segments
	 * @return a new <code>File</code> from the given path
	 * name segments
	 */
	public static File getFile(String[] segments) {
		File result = new File(segments[0]);
		for(int i = 1; i < segments.length; ++i){
			result = new File(result, segments[i]);
		}
		return result;
	}
	/**
	 * Returns a list of all files below the given directory
	 * that end with a string in the given include list and
	 * do not end with a string in the given exclude list.
	 * If include is <code>null</code> all files are included
	 * except those that are explicitly excluded. If exclude
	 * is <code>null</code> no files are excluded except those
	 * that are not included.
	 *
	 * @param dir the given directory
	 * @param include a list of filenames to include
	 * @param exclude a list of filenames to exclude
	 * @return a list of all files below the given directory
	 * that are included and not explicitly excluded
	 */
	public static File[] getFiles(File dir, String[] include, String[] exclude) {
		List<File> list = new ArrayList<>();
		String[] children = dir.list();
		if(children == null){
			return new File[0];
		}
		for (String element : children) {
			File child = new File(dir, element);
			String name = child.getName();
			if(child.isDirectory()){
				File[] result = getFiles(child, include, exclude);
				for (File element2 : result) {
					list.add(element2);
				}
			} else {
				boolean includeFile = include == null;
				if(include != null){
					for (String element2 : include) {
						if (name.endsWith(element2)) {
							includeFile = true;
							break;
						}
					}
				}
				boolean excludeFile = exclude != null;
				if(exclude != null){
					for (String element2 : exclude) {
						if (name.endsWith(element2)) {
							excludeFile = true;
							break;
						}
					}
				}
				if(includeFile && !excludeFile){
					list.add(child);
				}
			}
		}
		return list.toArray(new File[0]);
	}
	/**
	 * Breaks the given file into its path name segments
	 * and returns the result.
	 *
	 * @param file a file or directory
	 * @return the path name segments of the given file
	 */
	public static String[] getSegments(File file) {
		return getSegments(file.toString(), File.separatorChar);
	}
	/**
	 * Breaks the given string into segments and returns the
	 * result.
	 *
	 * @param s a string
	 * @param separator the segment separator
	 * @return the segments of the given string
	 */
	public static String[] getSegments(String s, char separator){
		List<String> result = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(s, "" + separator);
		while(tokenizer.hasMoreTokens()){
			result.add(tokenizer.nextToken());
		}
		return result.toArray(new String[0]);
	}
	/**
	 * Returns a vector of <code>File</code> paths parsed from
	 * the given paths string.
	 *
	 * @param paths a paths string
	 * @return a vector of <code>File</code> paths parsed from
	 * the given paths string
	 */
	public static File[] parsePaths(String paths){
		List<File> result = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(paths, ";");
		while(tokenizer.hasMoreTokens()){
			result.add(new File(tokenizer.nextToken()));
		}
		return result.toArray(new File[0]);
	}
	/**
	 * Copies all bytes in the given source file to
	 * the given destination file.
	 *
	 * @param source the given source file
	 * @param destination the given destination file
	 */
	public static void transferData(File source, File destination) throws IOException {
		destination.getParentFile().mkdirs();
		try (InputStream is = new BufferedInputStream(new FileInputStream(source));
				OutputStream os = new BufferedOutputStream(new FileOutputStream(destination));) {

			transferData(is, os);
		}
	}
	/**
	 * Copies all bytes in the given source stream to
	 * the given destination stream. Neither streams
	 * are closed.
	 *
	 * @param source the given source stream
	 * @param destination the given destination stream
	 */
	public static void transferData(InputStream source, OutputStream destination) throws IOException {
		int bytesRead = 0;
		while(bytesRead != -1){
			bytesRead = source.read(buffer, 0, buffer.length);
			if(bytesRead != -1){
				destination.write(buffer, 0, bytesRead);
			}
		}
	}
	/**
	 * Unzips the given zip file to the given destination directory
	 * extracting only those entries the pass through the given
	 * filter.
	 *
	 * @param filter filters out unwanted zip entries
	 * @param zipFile the zip file to unzip
	 * @param dstDir the destination directory
	 */
	public static void unzip(IZipFilter filter, ZipFile zipFile, File dstDir) throws IOException {
		unzip(filter, zipFile, dstDir, dstDir, 0);
	}

	private static void unzip(IZipFilter filter, ZipFile zipFile, File rootDstDir, File dstDir, int depth) throws IOException {

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		try {
			while(entries.hasMoreElements()){
				ZipEntry entry = entries.nextElement();
				if(entry.isDirectory()){
					continue;
				}
				String entryName = entry.getName();
				File file = new File(dstDir, FileTool.changeSeparator(entryName, '/', File.separatorChar));
				String destCanPath = dstDir.getCanonicalPath();
				String fileCanPath = file.getCanonicalPath();
				if (!fileCanPath.startsWith(destCanPath + File.separatorChar)) {
					throw new IOException("Entry is out side of target dir: " + entryName);
				}
				String fullEntryName = FileTool.changeSeparator(file.toString().substring(rootDstDir.toString().length() + 1), File.separatorChar, '/');
				if(!(filter == null || filter.shouldExtract(fullEntryName, entryName, depth))){
					continue;
				}
				file.getParentFile().mkdirs();
				try (InputStream src = zipFile.getInputStream(entry);
					OutputStream dst = new FileOutputStream(file)){
					transferData(src, dst);
				}
				if((entryName.endsWith(".zip") || entryName.endsWith(".jar")) && (filter == null || filter.shouldUnzip(fullEntryName, entryName, depth))) {
					String fileName = file.getName();
					String dirName = fileName.substring(0, fileName.length() - 4) + "_" + fileName.substring(fileName.length() - 3);
					ZipFile innerZipFile = null;
					try {
						innerZipFile = new ZipFile(file);
						File innerDstDir = new File(file.getParentFile(), dirName);
						unzip(filter, innerZipFile, rootDstDir, innerDstDir, depth + 1);
						file.delete();
					} catch (IOException e) {
						if(innerZipFile != null){
							try {
								innerZipFile.close();
								System.out.println("Could not unzip: " + fileName + ". InnerZip = " + innerZipFile.getName() + ". Lenght: " + innerZipFile.getName().length());
							} catch(IOException e2){
							}
						} else
							System.out.println("Could not unzip: " + fileName + ". InnerZip = <null>");
						e.printStackTrace();
					}

				}
			}
		} finally {
			try {
				zipFile.close();
			} catch(IOException e){
			}
		}
	}
	/**
	 * Unzips the inner zip files in the given destination directory
	 * extracting only those entries the pass through the given
	 * filter.
	 *
	 * @param filter filters out unwanted zip entries
	 * @param dstDir the destination directory
	 */
	public static void unzip(IZipFilter filter, File dstDir) {
		unzip(filter, dstDir, dstDir, 0);
	}

	private static void unzip(IZipFilter filter, File rootDstDir, File dstDir, int depth) {

		File[] entries = rootDstDir.listFiles();
		if (entries == null) {
			entries = new File[0];
		}
		for (File entry : entries) {
			if (entry.isDirectory()) {
				unzip(filter, entry, dstDir, depth);
			}
			String entryName = entry.getName();
			File file = new File(dstDir, FileTool.changeSeparator(entryName, '/', File.separatorChar));
			if (entryName.endsWith(".zip") || entryName.endsWith(".jar")) {
				String fileName = file.getName();
				String dirName = fileName.substring(0, fileName.length() - 4) + "_"
						+ fileName.substring(fileName.length() - 3);
				ZipFile innerZipFile = null;
				try {
					innerZipFile = new ZipFile(entry);
					File innerDstDir = new File(entry.getParentFile(), dirName);
					unzip(filter, innerZipFile, rootDstDir, innerDstDir, depth + 1);
					// entry.delete();
				} catch (IOException e) {
					if (innerZipFile != null) {
						try {
							innerZipFile.close();
							System.out.println("Could not unzip: " + fileName + ". InnerZip = " + innerZipFile.getName()
									+ ". Lenght: " + innerZipFile.getName().length());
						} catch (IOException e2) {
						}
					} else
						System.out.println("Could not unzip: " + fileName + ". InnerZip = <null>");
					e.printStackTrace();
				}

			}
		}
	}
	/**
	 * Zips the given directory to the given zip file.
	 * Directories are zipped recursively. Inner zip files are
	 * created for directories that end with "_zip" or "_jar".
	 * If verbose is true, progress information is logged.
	 *
	 * @param dir the directory to zip
	 * @param zipFile the resulting zip file
	 * @param verbose a boolean indicating whether progress
	 * information is logged
	 */
	public static void zip(File dir, File zipFile) throws IOException {
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFile));
				ZipOutputStream zos = new ZipOutputStream(bos);) {
			zip(dir, dir, zos);
		}
	}
	private static void zip(File root, File file, ZipOutputStream zos) throws IOException {
		if(file.isDirectory()){
			String name = file.getName();
			String[] list = file.list();
			if (list == null) {
				throw new IOException("Content from directory '" + file.getAbsolutePath() + "' can not be listed."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if(name.endsWith("_zip") || name.endsWith("_jar")){
				String rootString = root.toString();
				String fileString = file.toString();
				String zipEntryName = fileString.substring(rootString.length() + 1);
				int underscoreIndex = zipEntryName.lastIndexOf("_");
				zipEntryName = zipEntryName.substring(0, underscoreIndex) + "." + zipEntryName.substring(underscoreIndex + 1);
				ZipEntry zipEntry = new ZipEntry(changeSeparator(zipEntryName, File.separatorChar, '/'));
				zos.putNextEntry(zipEntry);
				ZipOutputStream zos2 = new ZipOutputStream(zos);
				for (String element : list) {
					File item = new File(file, element);
					zip(file, item, zos2);
				}
				zos2.finish();
				zos.closeEntry();
			} else {
				for (String element : list) {
					File item = new File(file, element);
					zip(root, item, zos);
				}
			}
		} else {
			String rootString = root.toString();
			String fileString = file.toString();
			String zipEntryName = fileString.substring(rootString.length() + 1);
			ZipEntry zipEntry = new ZipEntry(changeSeparator(zipEntryName, File.separatorChar, '/'));
			zos.putNextEntry(zipEntry);
			try (FileInputStream fos = new FileInputStream(file)){
				transferData(fos, zos);
			}
			zos.closeEntry();
		}
	}
}
