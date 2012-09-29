/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com> Description:
 * methods for reading/writing files
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.libv2.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jacob
 */
public class FileIO {

	public static List<String[]> loadCSVFile(File toLoad) throws FileNotFoundException, IOException {
		ArrayList<String[]> ret = new ArrayList<String[]>();
		if (toLoad.exists() && toLoad.isFile() && toLoad.canRead()) {
			FileReader fstream = null;
			fstream = new FileReader(toLoad.getAbsolutePath());
			BufferedReader in = new BufferedReader(fstream);
			try {
				int n = 0;
				for (String line = null; (line = in.readLine()) != null && line.length() > 0; ++n) {
					// if was edited in openoffice, will instead have semicolins..
					ret.add(line.replace(";", ",").replace(",,", ", ,").split(","));
				}
			} finally {
				in.close();
			}
		}
		return ret;
	}

	public static List<String> loadFile(File toLoad) throws FileNotFoundException, IOException {
		ArrayList<String> ret = new ArrayList<String>();
		if (toLoad.exists() && toLoad.isFile() && toLoad.canRead()) {
			FileReader fstream = null;
			fstream = new FileReader(toLoad.getAbsolutePath());
			BufferedReader in = new BufferedReader(fstream);
			try {
				int n = 0;
				for (String line = null; (line = in.readLine()) != null && line.length() > 0; ++n) {
					ret.add(line);
				}
			} finally {
				in.close();
			}
		}
		return ret;
	}

	public static boolean saveFile(File toSave, String[] lines) throws IOException {
		if (!toSave.exists() && !toSave.createNewFile()) {
			return false;
		}
		if (toSave.canWrite()) {
			FileWriter fstream = null;
			fstream = new FileWriter(toSave.getAbsolutePath());
			//System.out.println("writing to " + tosave.getAbsolutePath());
			BufferedWriter out = new BufferedWriter(fstream);
			for (String line : lines) {
				out.write(line);
				out.newLine();
			}
			out.flush();
			out.close();
			return true;
		}
		return false;
	}

	public static boolean saveFile(File toSave, ArrayList<String> lines) throws IOException {
		if (!toSave.exists()) {
			// TODO: first check if directory exists, then create the file
			File dir = new File(toSave.getAbsolutePath().substring(0, toSave.getAbsolutePath().lastIndexOf(File.separatorChar)));
			dir.mkdirs();
			try {
				if (!toSave.createNewFile()) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		if (toSave.canWrite()) {
			FileWriter fstream = null;
			fstream = new FileWriter(toSave.getAbsolutePath());
			//System.out.println("writing to " + tosave.getAbsolutePath());
			BufferedWriter out = new BufferedWriter(fstream);
			for (String line : lines) {
				out.write(line);
				out.newLine();
			}
			out.flush();
			out.close();
			return true;
		}
		return false;
	}

	public static boolean saveCSVFile(File toSave, ArrayList<String[]> lines) throws IOException {
		if (!toSave.exists()) {
			// TODO: first check if directory exists, then create the file
			File dir = new File(toSave.getAbsolutePath().substring(0, toSave.getAbsolutePath().lastIndexOf(File.separatorChar)));
			dir.mkdirs();
			try {
				if (!toSave.createNewFile()) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		if (toSave.canWrite()) {
			FileWriter fstream = null;
			fstream = new FileWriter(toSave.getAbsolutePath());
			//System.out.println("writing to " + tosave.getAbsolutePath());
			BufferedWriter out = new BufferedWriter(fstream);
			for (String line[] : lines) {
				for (int i = 0; i < line.length; ++i) {
					out.write(line[i]);
					if (i + 1 < line.length) {
						out.write(",");
					}
				}
				out.newLine();
			}
			out.flush();
			out.close();
			return true;
		}
		return false;
	}

	public static File getJarFile(Class jarClass) {
		return new File(jarClass.getProtectionDomain().getCodeSource().getLocation().getPath().
				replace("%20", " ").replace("%25", "%"));
	}

	/**
	 * parses the given filename string for the file extension
	 *
	 * @param filename string to parse
	 * @return extension, beginning with the dot (eg. ".jar")
	 */
	public static String getExtension(File file) {
		return getExtension(file.getAbsolutePath());
	}

	/**
	 * parses the given filename string for the file extension
	 *
	 * @param filename string to parse
	 * @return extension, beginning with the dot (eg. ".jar")
	 */
	public static String getExtension(String filename) {
		if (filename != null) {
			int dot = filename.lastIndexOf(".");
			if (dot > 0 && dot > filename.lastIndexOf(File.separator)) {
				return filename.substring(dot);
			}
		}
		return "";
	}

	public static enum OVERWRITE_CASE {

		NEVER, IF_NEWER, ALWAYS
	}

	public static void extractResource(String path, File writeTo, Class jarClass) throws Exception {
		extractResource(path, writeTo, jarClass, OVERWRITE_CASE.ALWAYS);
	}

	/**
	 * extract a file from the jar archive
	 *
	 * @param path path to the resource, relative to the jar root
	 * @param writeTo path to write to. if doesn't exist, will create directory
	 * if there is not a matching file extension
	 * @param jarClass class in the jar with the resource to extract
	 * @param overwrite what cases should the file be overwriten, if it exists
	 * @throws Exception
	 */
	public static void extractResource(String path, File writeTo, Class jarClass, OVERWRITE_CASE overwrite) throws Exception {
		if (!writeTo.exists()) {
			if (!getExtension(path).equalsIgnoreCase(getExtension(writeTo))) {
				writeTo.mkdirs();

			} else {
				// ensure parent dirs exist
				writeTo.getParentFile().mkdirs();
			}
		}
		if (writeTo.isDirectory()) {
			String fname = new File(path).getName();
			writeTo = new File(writeTo, fname);
		}
		// check if the file exists and is newer than the JAR
		File jarFile = getJarFile(jarClass);
		if (writeTo.exists()){
			if(overwrite == OVERWRITE_CASE.NEVER) {
				return;
			} else if (overwrite == OVERWRITE_CASE.IF_NEWER 
					&& writeTo.lastModified() >= jarFile.lastModified()) {
				return;
			}
		}

		Exception err = null;
//		// works 1-time, but not after reloading updated plugin...
//		InputStream input = jarClass.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
//		if (input == null) {
//			throw new java.io.FileNotFoundException("Could not find '" + path + "' in " + jarFile.getAbsolutePath());
//		}
//		FileOutputStream output = null;
//		try {
//			System.out.println("writing " + writeTo.getAbsolutePath());
//			output = new FileOutputStream(writeTo);
//			byte[] buf = new byte[8192];
//			int length;
//
//			while ((length = input.read(buf)) > 0) {
//				output.write(buf, 0, length);
//			}
//			
//		} catch (Exception e) {
//			err = e;
//		}
		OutputStream output = null;
		InputStream input = null;
		try {
			// Got to jump through hoops to ensure we can still pull messages from a JAR
			// file after it's been reloaded...
			URL res = jarClass.getResource(path.startsWith("/") ? path : "/" + path);
			if (res == null) {
				throw new java.io.FileNotFoundException("Could not find '" + path + "' in " + jarFile.getAbsolutePath());
			}
			URLConnection resConn = res.openConnection();
			resConn.setUseCaches(false);
			input = resConn.getInputStream();

			if (input == null) {
				throw new java.io.IOException("can't get input stream from " + res);
			} else {
				output = new FileOutputStream(writeTo);
				byte[] buf = new byte[8192];
				int len;
				while ((len = input.read(buf)) > 0) {
					output.write(buf, 0, len);
				}
			}
		} catch (Exception ex) {
			err = ex;
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
			}
			try {
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
			}
		}
		if (err != null) {
			throw err;
		}
	}
} // end class CSV

