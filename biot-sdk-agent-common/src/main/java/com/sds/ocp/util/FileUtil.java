
package com.sds.ocp.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File, InputStream 및 Directory 관련 유틸 클래스. (디렉터리 생성, 파일 이동, 삭제 등)
 * 
 * @author 김태호 <th71.kim@samsung.com>
 */
public final class FileUtil {
	
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	private static final Logger		LOG				= LoggerFactory.getLogger(FileUtil.class);

	private static final String		LINE_SEPARATOR	= System.lineSeparator();					// System.getProperty("line.separator");

	/** default parent directory File when a file has a null parent */
	public static final File		DEFAULT_PARENT	= new File(".");							// XXX user.dir?

	private static final String		FILE_REGEX		= "([\\w\\:\\\\w ./-]+\\w+(\\.)?\\w+)";
	private static final Pattern	FILE_PATTERN	= Pattern.compile(FILE_REGEX);

//	private FileUtil() {
//		throw new AssertionError();
//	}

	public static String getLineSeparator() {
		return LINE_SEPARATOR;
	}

	/**
	 * @brief make directory if doesn't exist.
	 * @param dirStr
	 * @return false only if failed to make a directory; true otherwise. (also true if the directory already exists.)
	 */
	public static boolean checkAndMakeDir(String dirStr) {
		return checkAndMakeDir(new File(dirStr));
	}

	/**
	 * @brief make directory if doesn't exist.
	 * @param dir
	 * @return false only if failed to make a directory; true otherwise. (also true if the directory already exists.)
	 */
	public static boolean checkAndMakeDir(File dir) {
		boolean isExists = dir.exists();
		if (!isExists) {
			isExists = dir.mkdirs();
		}

		return isExists;
	}

	/**
	 * @param fromPath
	 * @param toPath
	 * @return
	 */
	public static boolean moveFile(String fromPath, String toPath) {
		File from = new File(fromPath);
		File to = new File(toPath);
		return from.renameTo(to);
	}

	/**
	 * Delete File or Directory.
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			if (!file.delete()) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 지정한 directory 하위의 파일 삭제 (바로 하위 레벨의 파일만)
	 * 
	 * @param dirPath
	 * @return
	 */
	public static void deleteFileInDirectory(String dirPath) {
		File dir = new File(dirPath);
		deleteFileInDirectory(dir);
	}

	/**
	 * 지정한 directory 하위의 파일 삭제 (바로 하위 레벨의 파일만)
	 * 
	 * @param dir
	 */
	public static void deleteFileInDirectory(File dir) {
		deleteFileInDirectory(dir, null);
	}

	/**
	 * @param dir
	 * @param containsStr
	 */
	public static void deleteFileInDirectory(File dir, String containsStr) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files == null) {
				LOG.debug("no list files : {}", dir.getAbsolutePath());
				return;
			}
			for (File f : files) {
				if (containsStr == null || f.getName().contains(containsStr)) {
					LOG.debug("delete : {}", f.getAbsolutePath());
					f.delete();
				}
			}
		} else {
			LOG.debug("not a directory : {}", dir.getAbsolutePath());
			return;
		}
	}

	/**
	 * read to token and return byte[]
	 * 
	 * @param inputStream
	 *        : read target inputStream
	 * @param token
	 *        : read token
	 * @return
	 */
	public static byte[] tokenRead(InputStream inputStream, byte[] token) throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream(8192);

		try {
			int byteRead = -1;
			int matchCount = 0;
			int tokenCount = 0;
			while (true) {
				byteRead = inputStream.read();
				if (byteRead == -1) {
					break;
				}
				out.write(byteRead);
				//tokenCount++;	// 기존로직
				if ((int) token[tokenCount] == byteRead) {
					tokenCount++;	// 변경로직(token은 인덱스 0부터 비교해야함)
					matchCount++;
				} else {
					matchCount = 0;
					tokenCount = 0;
				}
				if (matchCount == token.length) {
					break;
				}
			}
			out.flush();
			return out.toByteArray();

		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	/**
	 * read input size and return byte[]
	 * 
	 * @param inputStream
	 *        : read target inputStream
	 * @param size
	 *        : read size
	 * @return
	 */
	public static byte[] sizeRead(InputStream inputStream, int size) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);

		try {
			byte[] buffer = new byte[size];
			int bytesRead = -1;
			bytesRead = inputStream.read(buffer);
			out.write(buffer, 0, bytesRead);
			out.flush();
			return out.toByteArray();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static int countLines(FileReader fileReader) throws IOException {
		LineNumberReader reader = new LineNumberReader(fileReader);
		int cnt = 0;
		String s = null;
		do {
			s = reader.readLine();
		} while (s != null);

		cnt = reader.getLineNumber();
		reader.close();
		return cnt;
	}



	/**
	 * 파일 또는 리소스에서 한 줄에 문자열 하나로 읽어서 문자열 목록 리턴
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static List<String> readLinesFromFile(String filePath) throws IOException {
		return readLinesFromFile(filePath, false);
	}

	/**
	 * 파일 또는 리소스에서 한 줄에 문자열 하나로 읽어서 문자열 목록 리턴 <br/>
	 * 단, 빈 줄을 제외할지 옵션 설정 가능
	 * 
	 * @param filePath
	 * @param withoutEmptyLines
	 * @return
	 * @throws IOException
	 */
	public static List<String> readLinesFromFile(String filePath, boolean withoutEmptyLines) throws IOException {
		return readLinesFromFile(filePath, withoutEmptyLines, null);
	}

	/**
	 * 파일 또는 리소스에서 한 줄에 문자열 하나로 읽어서 문자열 목록 리턴 <br/>
	 * 단, 빈 줄을 제외할지 옵션 설정 가능 <br/>
	 * 주석 문자가 있는 경우 제외할지 옵션 설정 가능 <br/>
	 * 
	 * @param filePath
	 * @param withoutEmptyLines
	 * @param lineCommentStr
	 * @return
	 * @throws IOException
	 * @note JDK7의 Files.readAllLines() 메서드보다 약간 더 빠름
	 */
	public static List<String> readLinesFromFile(String filePath, boolean withoutEmptyLines, String lineCommentStr)
			throws IOException {
		InputStream fis = FileUtil.getFileInputStream(filePath);
		if (fis == null) {
			LOG.debug("no file : " + filePath);
			return null;
		}
		InputStreamReader fileReader = new InputStreamReader(fis, CHARSET_UTF8);

		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(fileReader);

			List<String> list = new ArrayList<String>();
			String s = reader.readLine();
			while (s != null) {
				if (!withoutEmptyLines || !StringUtil.isEmpty(s.trim())) {
					if (lineCommentStr == null || !s.startsWith(lineCommentStr)) {
						list.add(s);
					}
				}
				s = reader.readLine();
			}
			return list;

		} finally {
			if (reader != null) {
				reader.close();
			}
			fis.close();
		}
	}

	/**
	 * File to String Util
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromFile(String filePath) throws IOException {
		//return readStringFromPath(Paths.get(filePath));

		InputStream fis = FileUtil.getFileInputStream(filePath);
		if (fis == null) {
			LOG.debug("no file : " + filePath);
			return null;
		}
		return readStringFromInputStream(fis);
	}

	/**
	 * InputStream to String
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromInputStream(InputStream is) throws IOException {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(is, CHARSET_UTF8));
			boolean isFirst = true;
			while ((line = br.readLine()) != null) {
				// (참고) 마지막 라인에 line separator 가 존재하는지는 알 수 없음
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(LINE_SEPARATOR);
				}
				sb.append(line);
			}
		} finally {
			if (br != null) {
				br.close();
			}
			if (is != null) {
				is.close();
			}
		}
		return sb.toString();
	}

	/**
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromPath(Path path) throws IOException {
		byte[] b = Files.readAllBytes(path);
		return new String(b, CHARSET_UTF8);
	}

	/**
	 * File Object to String
	 * 
	 * @param fileObj
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromFile(File fileObj) throws IOException {
		//return readStringFromPath(fileObj.toPath());

		InputStream fis = new FileInputStream(fileObj);
		return readStringFromInputStream(fis);

		//		String result = null;
		//		DataInputStream in = null;
		//		try {
		//			byte[] buffer = new byte[(int) fileObj.length()];
		//			in = new DataInputStream(new FileInputStream(fileObj));
		//			in.readFully(buffer);
		//			result = new String(buffer, OcpConstants.CHARSET_UTF8);
		//		} finally {
		//			if (in != null) {
		//				in.close();
		//			}
		//
		//		}
		//		return result;
	}

	/**
	 * @param filePath
	 * @return
	 */
	public static Path getPath(String filePath) {
		return Paths.get(filePath);
	}

	/**
	 * @param lines
	 *        : List, Set
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeFileFromLines(Collection<String> lines, String filePath) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append(LINE_SEPARATOR);
		}
		writeFileFromString(sb.toString(), filePath);
	}

	/**
	 * String to File Util
	 * 
	 * @param str
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeFileFromString(String str, String filePath) throws IOException {
		writeFileFromString(str, new File(filePath));
	}

	/**
	 * String to File Util
	 * 
	 * @param str
	 * @param fileObj
	 * @throws IOException
	 * @note JDK7의 Files.write() 메서드보다 약간 더 빠름
	 */
	public static void writeFileFromString(String str, File fileObj) throws IOException {
		// 상위 directory 생성
		File dir = fileObj.getParentFile();
		boolean dirExists = dir.exists();
		if (!dirExists) {
			LOG.debug("mkdirs : {}", dir.getAbsolutePath());
			dir.mkdirs();
		}

		// 파일 생성
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter out = null;

		try {
			fos = new FileOutputStream(fileObj);
			osw = new OutputStreamWriter(fos, CHARSET_UTF8);
			out = new BufferedWriter(osw);
			out.write(str);
			out.flush();

		} finally {
			if (out != null) {
				out.close();
			}
			if (osw != null) {
				osw.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 실제 파일 경로 또는 classpath 에 있는 파일,리소스 경로를 구분하지 않고, <br/>
	 * 파일 또는 리소스를 찾아서 InputStream 객체 리턴.
	 * 
	 * @param filePath
	 * @return 해당 경로에 파일 또는 리소스가 존재하는 경우 InputStream 객체 리턴, 존재하지 않는 경우 null 리턴
	 */
	public static InputStream getFileInputStream(String filePath) {
		// 파일로부터 FileInputStream 객체를 얻음
		File file = new File(filePath);
		if (file.exists()) {
			LOG.debug("file exists : {}", file.getAbsolutePath());
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				LOG.error(e.toString());
				return null;
			}
		}

		// URL Path 는 \ 가 아닌 / 로만 처리 가능함
		if (filePath.contains("\\")) {
			filePath = StringUtil.replace(filePath, "\\", "/");
		}

		// URL Resource 로부터 InputStream 객체를 얻음
		URL url = FileUtil.class.getResource("/" + filePath);
		if (url != null) {
			LOG.debug("resource exists : {}", url.getPath());
			try {
				return url.openStream();
			} catch (IOException e) {
				LOG.error(e.toString());
				return null;
			}
		}
		return null;
	}

	/**
	 * 실제 파일 경로 또는 classpath 에 있는 파일 경로를 구분하지 않고, 파일을 찾아서 File 객체 리턴. <br/>
	 * 단, 실제 파일시스템으로 접근이 가능해야 함 (jar 로 묶여있으면 처리 불가) <br/>
	 * 클래스패스에 있는 파일을 읽기 위해 File object 를 얻으려고 할때 사용
	 * 
	 * @param filePath
	 * @return 해당 경로에 파일 또는 리소스가 존재하는 경우 File 객체 리턴, 존재하지 않는 경우 null 리턴
	 */
	public static File getFileObject(String filePath) {
		// 파일 객체를 얻음
		File file = new File(filePath);
		if (file.exists()) {
			LOG.debug("file exists : {}", file.getAbsolutePath());
			return file;
		}

		// URL Path 는 \ 가 아닌 / 로만 처리 가능함
		if (!"/".equals(File.separator) && filePath.contains(File.separator)) {
			filePath = StringUtil.replace(filePath, File.separator, "/");
		}

		// URL Resource 로부터 파일 객체를 얻음
		URL url = FileUtil.class.getResource("/" + filePath);
		if (url != null) {
			File urlFile = new File(url.getPath());
			if (urlFile.exists()) {
				LOG.debug("resource exists as a file : {}", url.getPath());
				return urlFile;
			} else {
				LOG.debug("This resource cannot access as a file [" + url.getPath() + "]");		// fortify 조치로 warn->debug 수정 향후 보완 필요
			}
		}
		return null;
	}

	/**
	 * 파일의 내용을 replace
	 * 
	 * @param filePath
	 * @param oldPattern
	 * @param newPattern
	 * @return
	 * @throws IOException
	 */
	public static boolean replaceString(String filePath, String oldPattern, String newPattern) throws IOException {

		File fileObj = getFileObject(filePath);
		if (fileObj == null) {
			LOG.warn("No file : {}", filePath);
			return false;
		}

		String strBefore = readStringFromFile(fileObj);
		//LOG.debug("strBefore={}", strBefore);

		String strAfter = StringUtil.replace(strBefore, oldPattern, newPattern);
		//LOG.debug("strAfter={}", strAfter);

		writeFileFromString(strAfter, fileObj);

		boolean changed = !strBefore.equals(strAfter);

		return changed;
	}

	/**
	 * @param fileName
	 * @return
	 * @note Path Manipulation 조치를 위한 처리로직
	 */
	public static boolean isFileMatched(String fileName) {
		return FILE_PATTERN.matcher(fileName).matches();
	}

	/**
	 * Copy contents of fromDir into toDir
	 * 
	 * @param fromDir
	 *        must exist and be readable
	 * @param toDir
	 *        must exist or be creatable and be writable
	 * @return the total number of files copied
	 */
	public static int copyDir(File fromDir, File toDir) throws IOException {
		return copyDir(fromDir, toDir, null, null);
	}

	/**
	 * Recursively copy files in fromDir (with any fromSuffix) to toDir, replacing fromSuffix with toSuffix if any. This silently
	 * ignores dirs and files that are not readable but throw IOException for directories that are not writable. This does not clean
	 * out the original contents of toDir. (subdirectories are not renamed per directory rules)
	 * 
	 * @param fromSuffix
	 *        select files with this suffix - select all if null or empty
	 * @param toSuffix
	 *        replace fromSuffix with toSuffix in the destination file name - ignored if null or empty, appended to name if
	 *        fromSuffix is null or empty
	 * @return the total number of files copied
	 */
	public static int copyDir(File fromDir, File toDir, final String fromSuffix, String toSuffix) throws IOException {
		return copyDir(fromDir, toDir, fromSuffix, toSuffix, (FileFilter) null);
	}

	/**
	 * Recursively copy files in fromDir (with any fromSuffix) to toDir, replacing fromSuffix with toSuffix if any. This silently
	 * ignores dirs and files that are not readable but throw IOException for directories that are not writable. This does not clean
	 * out the original contents of toDir. (subdirectories are not renamed per directory rules) This calls any delegate
	 * FilenameFilter to collect any selected file.
	 * 
	 * @param fromSuffix
	 *        select files with this suffix - select all if null or empty
	 * @param toSuffix
	 *        replace fromSuffix with toSuffix in the destination file name - ignored if null or empty, appended to name if
	 *        fromSuffix is null or empty
	 * @return the total number of files copied
	 */
	public static int copyDir(File fromDir, File toDir, final String fromSuffix, final String toSuffix, final FileFilter delegate)
			throws IOException {

		if (null == fromDir || !fromDir.canRead()) {
			return 0;
		}

		boolean haveSuffix = null != fromSuffix && 0 < fromSuffix.length();
		int slen = !haveSuffix ? 0 : fromSuffix.length();

		if (!toDir.exists()) {
			toDir.mkdirs();
		}

		String[] fromFiles;
		if (!haveSuffix) {
			fromFiles = fromDir.list();
		} else {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return new File(dir, name).isDirectory() || name.endsWith(fromSuffix);
				}
			};
			fromFiles = fromDir.list(filter);
		}

		int result = 0;
		int MAX = null == fromFiles ? 0 : fromFiles.length;
		for (int i = 0; i < MAX; i++) {
			String filename = fromFiles[i];
			File fromFile = new File(fromDir, filename);
			if (fromFile.canRead()) {
				if (fromFile.isDirectory()) {
					result += copyDir(fromFile, new File(toDir, filename), fromSuffix, toSuffix, delegate);
				} else if (fromFile.isFile()) {
					if (haveSuffix) {
						filename = filename.substring(0, filename.length() - slen);
					}
					if (null != toSuffix) {
						filename = filename.concat(toSuffix);
					}
					File targetFile = new File(toDir, filename);
					if (null == delegate || delegate.accept(targetFile)) {
						copyFile(fromFile, targetFile);
					}
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * Copy fromFile to toFile, handling file-file, dir-dir, and file-dir copies.
	 * 
	 * @param fromFile
	 *        the File path of the file or directory to copy - must be readable
	 * @param toFile
	 *        the File path of the target file or directory - must be writable (will be created if it does not exist)
	 */
	public static void copyFile(File fromFile, File toFile) throws IOException {
		throwIaxIfNull(fromFile, "fromFile");
		throwIaxIfNull(toFile, "toFile");
		throwIaxIfFalse(!toFile.equals(fromFile), "same file");
		if (toFile.isDirectory()) { // existing directory
			//throwIaxUnlessCanWriteDir(toFile, "toFile");
			if (fromFile.isFile()) { // file-dir
				File targFile = new File(toFile, fromFile.getName());
				copyValidFiles(fromFile, targFile);
			} else if (fromFile.isDirectory()) { // dir-dir
				copyDir(fromFile, toFile);
			} else {
				throwIaxIfFalse(false, "not dir or file: " + fromFile);
			}
		} else if (toFile.isFile()) { // target file exists
			if (fromFile.isDirectory()) {
				throwIaxIfFalse(false, "can't copy to file dir: " + fromFile);
			}
			copyValidFiles(fromFile, toFile); // file-file
		} else { // target file is a non-existent path -- could be file or dir
			/* File toFileParent = */ensureParentWritable(toFile);
			if (fromFile.isFile()) {
				copyValidFiles(fromFile, toFile);
			} else if (fromFile.isDirectory()) {
				toFile.mkdirs();
				throwIaxUnlessCanWriteDir(toFile, "toFile");
				copyDir(fromFile, toFile);
			} else {
				throwIaxIfFalse(false, "not dir or file: " + fromFile);
			}
		}
	}

	/**
	 * Copy file to file.
	 * 
	 * @param fromFile
	 *        the File to copy (readable, non-null file)
	 * @param toFile
	 *        the File to copy to (non-null, parent dir exists)
	 * @throws IOException
	 */
	public static void copyValidFiles(File fromFile, File toFile) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(fromFile);
			out = new FileOutputStream(toFile);
			copyStream(in, out);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOG.warn(e.toString());
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.warn(e.toString());
				}
			}
		}
	}

	/**
	 * Ensure that the parent directory to path can be written. If the path has a null parent, DEFAULT_PARENT is tested. If the path
	 * parent does not exist, this tries to create it.
	 * 
	 * @param path
	 *        the File path whose parent should be writable
	 * @return the File path of the writable parent directory
	 * @throws IllegalArgumentException
	 *         if parent cannot be written or path is null.
	 */
	public static File ensureParentWritable(File path) {
		throwIaxIfNull(path, "path");
		File pathParent = path.getParentFile();
		if (null == pathParent) {
			pathParent = DEFAULT_PARENT;
		}
		if (!pathParent.canWrite()) {
			pathParent.mkdirs();
		}
		throwIaxUnlessCanWriteDir(pathParent, "pathParent");
		return pathParent;
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
		int MAX = 4096;
		byte[] buf = new byte[MAX];
		for (int bytesRead = in.read(buf, 0, MAX); bytesRead != -1; bytesRead = in.read(buf, 0, MAX)) {
			out.write(buf, 0, bytesRead);
		}
	}

	/**
	 * Shorthand for "if null, throw IllegalArgumentException"
	 * 
	 * @throws IllegalArgumentException
	 *         "null {name}" if o is null
	 */
	public static void throwIaxIfNull(final Object o, final String name) {
		if (null == o) {
			String message = "null " + (null == name ? "input" : name);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Shorthand for "if false, throw IllegalArgumentException"
	 * 
	 * @throws IllegalArgumentException
	 *         "{message}" if test is false
	 */
	public static void throwIaxIfFalse(final boolean test, final String message) {
		if (!test) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @throws IllegalArgumentException
	 *         unless dir is a readable directory
	 */
	public static void throwIaxUnlessCanWriteDir(File dir, String label) {
		if (!canWriteDir(dir)) {
			throw new IllegalArgumentException(label + " not writable dir: " + dir);
		}
	}

	/**
	 * @param dir
	 * @return true if dir is a writable directory
	 */
	public static boolean canWriteDir(File dir) {
		return null != dir && dir.canWrite() && dir.isDirectory();
	}

	/**
	 * @param dir
	 * @return true if dir is a readable directory
	 */
	public static boolean canReadDir(File dir) {
		return null != dir && dir.canRead() && dir.isDirectory();
	}

}
