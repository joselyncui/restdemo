package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUtil {
	public static void createFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static String convertSpace(String uri) {
		return uri.replace(" ", "%20");
	}

	public static String convertTwentyPercent(String uri) {
		return uri.replace("%20", " ");
	}

	public static File[] read(String path) {
		File file = new File(path);
		return file.listFiles();
	}

	public static String getExtension(String fileName) {
		if (fileName.lastIndexOf("/") != -1) {
			return fileName.substring(fileName.lastIndexOf("/"));
		}
		return "";
	}

	public static void copyFolder(String sourcePath, String targetPath) {
		File in = new File(sourcePath);
		File out = new File(targetPath);
		if (!in.exists()) {
			return;
		}
		if (!out.exists())
			out.mkdirs();
		File[] file = in.listFiles();
		FileInputStream fin = null;
		FileOutputStream fout = null;
		if (file == null) {
			return;
		}
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				try {
					fin = new FileInputStream(file[i]);
					fout = new FileOutputStream(new File(targetPath + "/"
							+ file[i].getName()));
					int c;
					byte[] b = new byte[1024 * 5];
					while ((c = fin.read(b)) != -1) {
						fout.write(b, 0, c);
					}
					fin.close();
					fout.flush();
					fout.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				copyFolder(sourcePath + "/" + file[i].getName(), targetPath
						+ "/" + file[i].getName());
		}
	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File newFile = new File(newPath);
			newFile.mkdirs();
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	public static void deleteFolder(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			deleteDirectory(path);
		} else {
			deleteFile(path);
		}
	}

	public static void deleteDirectory(String path) {
		File dirFile = new File(path);
		File[] files = dirFile.listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				deleteFile(files[i].getAbsolutePath());
			} else {
				deleteDirectory(files[i].getAbsolutePath());
			}
		}
		dirFile.delete();
	}

	public static boolean deleteFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		file.delete();
		return true;
	}
	
	public static boolean deleteFile(File file) {
		if (!file.exists()) {
			return false;
		}
		file.delete();
		return true;
	}

	public static ByteArrayOutputStream getBytesByFile(String filePath) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			File file = new File(filePath);
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(file), 50 * 1024 * 1024);
			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			in.close();
			return out;
		} catch (Exception e) {
			return null;
		}
	}

	public static void writeProperties(String filePath, String parameterName,
			String parameterValue) {
		Properties prop = new Properties();
		try {
			InputStream fis = new FileInputStream(filePath);
			prop.load(fis);
			OutputStream fos = new FileOutputStream(filePath);
			
			System.out.println("parameterName: " + parameterName + "\t parameterValue: " + parameterValue);
			prop.setProperty(parameterName, parameterValue);
			prop.store(fos, "Update '" + parameterName + "' value");
		} catch (IOException e) {
			System.err.println("Visit " + filePath + " for updating "
					+ parameterName + " value error");
		}
	}

	public static Map<String, String> readProperties(String filePath) {
		Map<String, String> properties = new HashMap<String, String>();
		Properties p = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(filePath));
			p.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Object key : p.keySet()) {
			properties.put(key.toString(), p.getProperty(key.toString()));
		}
		return properties;
	}

	public static ByteArrayOutputStream getBytesByFile(int length,
			String filePath) {
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		byte[] temp = new byte[1024];
		try {
			out = new ByteArrayOutputStream();
			File file = new File(filePath);
			in = new BufferedInputStream(
					new FileInputStream(file), 50 * 1024 * 1024);
			in.skip(length);
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				if (out.size() == 200 * 1024 * 1024) {
					break;
				}
				out.write(temp, 0, size);
			}
			in.close();
			return out;
		} catch (Exception e) {
			return null;
		}finally{
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			temp = null;
		}
	}

	public static List<String> upload(String path, HttpServletRequest request) {
		List<String> filePaths = new ArrayList<String>();
		
		try {
			request.setCharacterEncoding("utf-8");
			DiskFileItemFactory factory = new DiskFileItemFactory();
			createFile(path);
			factory.setSizeThreshold(1024 * 1024);
			ServletFileUpload upload = new ServletFileUpload(factory);
			@SuppressWarnings("unchecked")
			List<FileItem> list = (List<FileItem>) upload.parseRequest(request);
			
			System.out.println("list.size: " + list.size());
			for (FileItem item : list) {
				String name = item.getFieldName();
				
				if (item.isFormField()) {
					String value = item.getString();
					request.setAttribute(name, value);
				} else {
					String value = item.getName();
					int start = value.lastIndexOf("\\");
					String filename = value.substring(start + 1);
					request.setAttribute(name, filename);
					File outFile = new File(path,filename);
					OutputStream out = new FileOutputStream(outFile);
					InputStream in = item.getInputStream();
					int length = 0;
					byte[] buf = new byte[1024];
					while ((length = in.read(buf)) != -1) {
						out.write(buf, 0, length);
					}
					in.close();
					out.close();
					
					filePaths.add(outFile.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filePaths;
	}

	public static void upload(String fileName, String path,
			HttpServletRequest request) {
		try {
			request.setCharacterEncoding("utf-8");
			DiskFileItemFactory factory = new DiskFileItemFactory();
			createFile(path);
			factory.setSizeThreshold(1024 * 1024);
			ServletFileUpload upload = new ServletFileUpload(factory);
			@SuppressWarnings("unchecked")
			List<FileItem> list = (List<FileItem>) upload.parseRequest(request);
			for (FileItem item : list) {
				String name = item.getFieldName();
				if (item.isFormField()) {
					String value = item.getString();
					request.setAttribute(name, value);
				} else {
					request.setAttribute(name, fileName);
					OutputStream out = new FileOutputStream(new File(path,
							fileName));
					InputStream in = item.getInputStream();
					int length = 0;
					byte[] buf = new byte[1024];
					while ((length = in.read(buf)) != -1) {
						out.write(buf, 0, length);
					}
					in.close();
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param sourcePath
	 * @param saveDir
	 * @param saveName
	 * @return boolean, true if copy success else false
	 * @throws IOException
	 */
	public static boolean copyFile(String sourcePath, String saveDir,
			String saveName, String charset) throws IOException {
		boolean flag = true;
		String fileName = saveName;
		if (null == saveName || "".equals(saveDir.trim())) {
			fileName = sourcePath.substring(sourcePath
					.lastIndexOf(File.separator) + 1);
		}
		File saveDirFile = new File(saveDir);
		if (!saveDirFile.exists()) {
			saveDirFile.mkdirs();
		}

		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			if (charset.equals("")) {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(sourcePath)));
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(saveDir + fileName)));
			} else {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(sourcePath), charset));
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(saveDir + fileName), charset));
			}
			char[] cbuf = new char[1024 * 5];
			int len = cbuf.length;
			int off = 0;
			int ret = 0;
			while ((ret = br.read(cbuf, off, len)) > 0) {
				off += ret;
				len -= ret;
			}
			bw.write(cbuf, 0, off);
			bw.flush();
		} finally {
			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
		}
		return flag;
	}
	
	public static void copyFile(String sourcePath, String targetPath, String name)
			throws IOException {
		File sourceFile = new File(sourcePath);
		File targetFile = new File(targetPath);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}
		targetFile = new File(targetPath + name);
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
	
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
	
	
	
	public static String getProjectNameByPath(String path){
		path = path.substring(0, path.lastIndexOf("\\"));
		path = path.substring(path.lastIndexOf("\\") + 1);
		return path;
	}
	
	public static String getServerPathByPath(String path){
		path = path.substring(0, path.lastIndexOf("\\"));
		path = path.substring(0, path.lastIndexOf("\\"));
		return path;
	}
	
	public static String getTomcatPathByPath(String path){
		path = path.substring(0, path.lastIndexOf("\\"));
		path = path.substring(0, path.lastIndexOf("\\"));
		path = path.substring(0, path.lastIndexOf("\\"));
		return path;
	}
	
	public static String getServerByPath(String path){
		path = path.substring(0, path.lastIndexOf("\\"));
		path = path.substring(0, path.lastIndexOf("\\"));
		path = path.substring(0, path.lastIndexOf("\\"));
		path = path.substring(path.lastIndexOf("\\") + 1);
		System.out.println("getServerByPath: " +path);
		return path;
	}
	
	public static double getFileSizeM(String path){
		double size = 0d;
		File file = new File(path);
		if (file.exists()) {
			size = 1.0d * file.length()/(1024*1024);
			 
		}
		return size;
	}
	
	
	
	public static String readFileByChars(String fileName) {
		String text="";
		URL url;
		try {
			url = new URL(fileName);
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
			char[] tempchars = new char[30];
            int charread = 0;
            while ((charread = reader.read(tempchars)) != -1) {
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    text+=String.valueOf(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                        	
                            text+=String.valueOf(tempchars[i]);
                        }
                    }
                }
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return text;
    }
	
	public static String readTxtFile(String filePath){
		String result="";
        try {
                String encoding="utf-8";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ 
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                        result+=lineTxt+"\r";
                    }
                    read.close();
		        }else{
		            System.out.println("No such file!");
		        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
	
	public static HttpServletResponse download(String path,HttpServletResponse response) {
		try {
			File file = new File(path);
			if (file.exists()) {
				String filename = file.getName();
				String ext = filename.substring(filename.lastIndexOf(".") + 1)
						.toUpperCase();

				InputStream fis = new BufferedInputStream(new FileInputStream(path));
				byte[] buffer = new byte[fis.available()];
				fis.read(buffer);
				fis.close();
				response.reset();
				response.addHeader("Content-Disposition", "attachment;filename="
						+ new String(filename.getBytes()));
				response.addHeader("Content-Length", "" + file.length());
				OutputStream toClient = new BufferedOutputStream(
						response.getOutputStream());
				response.setContentType("application/octet-stream");
				toClient.write(buffer);
				toClient.flush();
				toClient.close();
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return response;
	}
	
	
	public static void saveFile(InputStream in, String path) {
		File outFile = new File(path);
		try {
			OutputStream out = new FileOutputStream(outFile);
			int length = 0;
			byte[] buf = new byte[1024];
			while ((length = in.read(buf)) != -1) {
				out.write(buf, 0, length);
			}
			in.close();
			out.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}