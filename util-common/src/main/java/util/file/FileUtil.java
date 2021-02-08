package util.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class is used to deal with files, provides lots of methods. [for JavaSE
 * programs]
 */
public class FileUtil {

	private static final Log LOG = LogFactory.getLog(FileUtil.class);

	/**
	 * append the content of a file to the end of another.
	 *
	 * @param to   the file which will be appended to
	 * @param from the file whose content will be appended
	 */
	public static void append(String to, String from) {
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fr = new FileReader(from);
			br = new BufferedReader(fr);
			fw = new FileWriter(to, true);
			bw = new BufferedWriter(fw, 8192 * 100);
			while (true) {
				String line = br.readLine();
				if (line != null) {
					bw.write(line);
					bw.newLine();
				} else
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
				if (br != null) {
					br.close();
				}
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void checkSpecialChar(String filePath) {
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			LineNumberReader lnr = new LineNumberReader(fileReader);
			while (true) {
				String line = lnr.readLine();
				if (line != null) {
					char[] chars = line.toCharArray();
					for (char c : chars) {
						if (c < 32 || c > 126) {
							LOG.info("special char in line#"
									+ lnr.getLineNumber() + ": ");
							LOG.info(line);
							break;
						}
					}
				} else
					break;
			}
			lnr.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkStringSet(String filePath,
									  Collection<String> checkSet) {
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fileReader);
			while (true) {
				String line = br.readLine();
				if (line != null) {
					for (String checkItem : checkSet) {
						if (line.contains(checkItem)) {
							System.out.println(line);
							continue;
						}
					}
				} else
					break;
			}
			br.close();
			fileReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void cleanDuplicateLines(String filePath) {
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		HashSet<String> all = new HashSet<String>();
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);

			String[] part = filePath.split("\\.");
			fw = new FileWriter(part[0] + "_clean." + part[1], false);
			bw = new BufferedWriter(fw, 8192 * 100);

			while (true) {
				String line = br.readLine();
				if (line != null) {
					line = line.trim();
					if (!all.contains(line)) {
						bw.write(line);
						bw.newLine();
						all.add(line);
					}
				} else
					break;
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void compareEntities(String path1, String path2) {
		try {
			FileReader fileReader;
			fileReader = new FileReader(path1);
			BufferedReader br = new BufferedReader(fileReader);
			Set<String> set = new HashSet<String>();
			while (true) {
				String line = br.readLine();
				if (line != null) {
					String[] entity = line.split(" ");
					set.add(entity[0]);
					set.add(entity[2]);
				} else
					break;
			}
			br.close();
			fileReader.close();

			FileReader fileReader2;
			fileReader2 = new FileReader(path2);
			BufferedReader br2 = new BufferedReader(fileReader2);
			Set<String> set2 = new HashSet<String>();
			while (true) {
				String line = br2.readLine();
				if (line != null) {
					String[] entity = line.split(" ");
					set2.add(entity[0]);
					set2.add(entity[2]);
				} else
					break;
			}
			br2.close();
			fileReader2.close();

			for (String string : set2) {
				if (!set.contains(string)) {
					System.out.println(string);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Set<String> countEntities(String aaa) {
		try {
			FileReader fileReader;
			fileReader = new FileReader(aaa);
			BufferedReader a = new BufferedReader(fileReader);
			Set<String> result = new HashSet<String>();
			while (true) {
				String line = a.readLine();
				if (line != null) {
					String[] entity = line.split(" ");
					result.add(entity[0]);
					result.add(entity[2]);
				} else
					break;
			}
			a.close();
			fileReader.close();
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static long countLines(List<?> paths) {
		long totalLines = 0L;
		FileReader fileReader = null;
		BufferedReader a = null;
		// long begin = System.currentTimeMillis();
		try {
			for (Object path : paths) {
				long currentFileLines = 0L;
				System.out.println("counting file: " + path);

				if (path instanceof String) {
					fileReader = new FileReader((String) path);
				} else if (path instanceof File) {
					fileReader = new FileReader((File) path);
				}

				a = new BufferedReader(fileReader);

				while (true) {
					String line = a.readLine();
					if (line != null) {
						currentFileLines++;
					} else {
						break;
					}
				}

				System.out.println("current file lines: " + currentFileLines);
				System.out.println();
				totalLines += currentFileLines;
			}
			a.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("total lines: " + totalLines);
		// long end = System.currentTimeMillis();
		// System.out.println("\ntotal count time:" + (end - begin) + "ms");
		return totalLines;
	}

	public static long countLines2(List<?> paths) {
		long totalLines = 0L;
		// long begin = System.currentTimeMillis();
		for (Object path : paths) {
			FileInputStream fis = null;
			InputStreamReader isr = null;
			LineNumberReader lnr = null;
			try {
				System.out.println("counting file: " + path);

				long fileLength = 0;
				if (path instanceof String) {
					fileLength = (new File((String) path)).length();
					fis = new FileInputStream((String) path);
				} else if (path instanceof File) {
					fileLength = ((File) path).length();
					fis = new FileInputStream((File) path);
				}
				isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
				lnr = new LineNumberReader(isr);
				lnr.skip(fileLength);
				long currentFileLines = lnr.getLineNumber();

				System.out.println("current file lines: " + currentFileLines);
				// System.out.println();
				totalLines += currentFileLines;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (lnr != null) {
					try {
						lnr.close();
					} catch (IOException e) {
					}
				}
				if (isr != null) {
					try {
						isr.close();
					} catch (IOException e) {
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}

		System.out.println("total lines: " + totalLines);
		// long end = System.currentTimeMillis();
		// System.out.println("\ntotal count time:" + (end - begin) + "ms");

		return totalLines;
	}

	public static void countSpecifiedContent(String filePath, String content) {
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fileReader);

			long count = 0;

			while (true) {
				String line = br.readLine();
				if (line != null) {
					if (line.contains(content)) {
						count++;
					}
				} else
					break;
			}
			System.out.println(count);
			br.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	public static void divideByLines(String filePath, long linePerPiece) {
		FileWriter fw = null;
		BufferedWriter bw = null;

		int pointIndex = filePath.lastIndexOf(".");
		String fileSuffix = filePath.substring(pointIndex);
		String filePathTruncated = filePath.substring(0, pointIndex);
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fileReader);

			String line = null;
			int part = 1;
			out:
			while (true) {
				fw = new FileWriter(filePathTruncated + "_part" + part
						+ fileSuffix, false);
				bw = new BufferedWriter(fw, 8192 * 1000);

				for (int i = 0; i < linePerPiece; i++) {
					line = br.readLine();
					if (line != null) {
						bw.write(line);
						bw.newLine();
					} else {
						bw.flush();
						break out;
					}
				}
				bw.flush();
				part++;
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void divideInto(String filePath, int pieces) {
		long totalLines = countLines2(Arrays.asList(filePath));
		long linePerPiece = totalLines / pieces + 1;
		System.out.println("lines per piece: " + linePerPiece);
		divideByLines(filePath, linePerPiece);
	}

	public static void filterKeywords(String filePath, HashSet<String> keywords) {
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);

			String[] part = filePath.split("\\.");
			fw = new FileWriter(part[0] + "_filtered." + part[1], false);
			bw = new BufferedWriter(fw, 8192 * 100);

			while (true) {
				String line = br.readLine();
				if (line != null) {
					line = line.trim();
					for (String keyword : keywords) {
						if (!line.contains(keyword)) {
							bw.write(line);
							bw.newLine();
						}
					}

				} else
					break;
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void findDuplicateCities(String path) {
		HashSet<String> all = new HashSet<String>();
		try {
			FileReader fileReader;
			fileReader = new FileReader(path);
			BufferedReader a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (line.startsWith("US.")) {
						String city = line.substring(0, 9);
						if (all.contains(city)) {
							System.out.println(city);
						} else {
							all.add(city);
						}
					}
				} else {
					break;
				}
			}

			a.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void findDuplicateLines(String path) {
		HashSet<String> all = new HashSet<String>();
		try {
			FileReader fileReader;
			fileReader = new FileReader(path);
			BufferedReader a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (all.contains(line)) {
						System.out.println(line);
					} else {
						all.add(line);
					}
				} else {
					break;
				}
			}
			a.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void findLostLines(String pathCheckSetFile,
									 String pathTargetFile) {
		HashSet<String> checkSet = new HashSet<String>();
		try {
			FileReader fileReader;
			fileReader = new FileReader(pathCheckSetFile);
			BufferedReader a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					checkSet.add(line);
				} else {
					break;
				}
			}
			a.close();
			fileReader.close();

			fileReader = new FileReader(pathTargetFile);
			a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (checkSet.contains(line)) {
						checkSet.remove(line);
					}
				} else {
					break;
				}
			}
			a.close();
			fileReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String string : checkSet) {
			System.out.println(string);
		}
	}

	public static void findNewLines(String pathCheckSetFile,
									String pathTargetFile) {
		HashSet<String> checkSet = new HashSet<String>();
		try {
			FileReader fileReader;
			fileReader = new FileReader(pathCheckSetFile);
			BufferedReader a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					checkSet.add(line);
				} else {
					break;
				}
			}
			a.close();
			fileReader.close();

			fileReader = new FileReader(pathTargetFile);
			a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (!checkSet.contains(line)) {
						System.out.println(line);
					}
				} else {
					break;
				}
			}
			a.close();
			fileReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void findSameLines(String path1, String path2) {
		HashSet<String> lines1 = new HashSet<String>();
		try {
			FileReader fileReader;
			fileReader = new FileReader(path1);
			BufferedReader a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					lines1.add(line);
				} else {
					break;
				}
			}
			a.close();
			fileReader.close();

			fileReader = new FileReader(path2);
			a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (lines1.contains(line)) {
						System.out.println(line);
					}
				} else {
					break;
				}
			}
			a.close();
			fileReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void findSpecifiedContent(String filePath, String content,
											String output) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			BufferedReader a = new BufferedReader(fileReader);

			fw = new FileWriter(output, true);
			bw = new BufferedWriter(fw, 8192 * 100);
			long count = 0;

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (line.contains(content)) {
						bw.write(line);
						bw.newLine();
						count++;
					}
				} else
					break;
			}
			System.out.println(count);
			bw.flush();
			a.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void findSpecifiedLinebyLineNumber(String filePath,
													 int lineNumber) {
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fileReader);

			for (int i = 1; i < lineNumber; i++) {
				br.readLine();
			}

			String line = br.readLine();
			System.out.println(line);

			System.out.println();
			System.out.println("Next 100 lines:");
			for (int i = 0; i < 100; i++) {
				line = br.readLine();
				if (line != null) {
					System.out.println(line);
				}

			}

			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}

	}

	public static ArrayList<String> getChildrenFilePath(File file) {
		ArrayList<String> result = new ArrayList<String>();
		File[] children = file.listFiles();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isFile()) {
				result.add(children[i].getAbsolutePath());
			} else {
				result.addAll(getChildrenFilePath(children[i]));
			}
		}
		return result;
	}

	public static long getSpecificLineCount(String path, String str) {
		long result = 0;
		try {
			FileReader fileReader;
			fileReader = new FileReader(path);
			BufferedReader a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (line.contains(str))
						result++;
				} else
					break;
			}
			a.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static ArrayList<File> listAllFiles(File directory) {
		ArrayList<File> files = new ArrayList<File>();
		LinkedList<File> waitingLine = new LinkedList<File>();
		waitingLine.addAll(Arrays.asList(directory.listFiles()));
		while (!waitingLine.isEmpty()) {
			File file = waitingLine.poll();
			if (file != null) {
				if (file.isFile()) {
					files.add(file);
				} else {
					waitingLine.addAll(Arrays.asList(file.listFiles()));
				}
			}
		}
		return files;
	}

	public static ArrayList<File> listChildFiles(File directory) {
		ArrayList<File> files = new ArrayList<File>();
		ArrayList<File> checkList = new ArrayList<File>();
		checkList.addAll(Arrays.asList(directory.listFiles()));
		for (File file : checkList) {
			if (file.isFile()) {
				files.add(file);
			}
		}
		return files;
	}

	public static void pretreatmentForFactualCities(String path) {
		try {
			FileReader fileReader;
			fileReader = new FileReader(path);
			BufferedReader a = new BufferedReader(fileReader);

			while (true) {
				String line = a.readLine();
				if (line != null) {
					if (!line.isEmpty()) {
						line = line.trim();
						String firstLetter = line.substring(0, 1);
						line = line.replaceFirst(firstLetter,
								firstLetter.toUpperCase());

						int indexOfBlank = 0;
						while (line.substring(indexOfBlank + 1).contains(" ")) {
							int newBlank = line.substring(indexOfBlank + 1)
									.indexOf(" ");
							indexOfBlank = indexOfBlank + 1 + newBlank;
							String afterBlank = line.substring(
									indexOfBlank + 1, indexOfBlank + 2);
							line = line.substring(0, indexOfBlank) + " "
									+ afterBlank.toUpperCase()
									+ line.substring(indexOfBlank + 2);
						}

						int indexOfBracket = line.indexOf("(");
						if (indexOfBracket != -1) {
							line = line.substring(0, indexOfBracket)
									+ line.substring(indexOfBracket).replace(
									",", "");
						}
						line = line.replace("(", "\t");

						if (line.endsWith(")")) {
							line = line.substring(0, line.length() - 1);
						}

						System.out.println(line);
					}

				} else {
					break;
				}
			}
			a.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void preview(Object fileObj) {
		File file = null;
		FileReader fileReader = null;
		BufferedReader br = null;
		try {
			if (fileObj instanceof String) {
				file = new File((String) fileObj);
			} else if (fileObj instanceof File) {
				file = (File) fileObj;
			} else {
				LOG.error("only String or File accepted for parameter fileObj");
				return;
			}
			fileReader = new FileReader(file);
			br = new BufferedReader(fileReader);
			int previewLines = 10;
			eof:
			while (true) {
				for (int i = 0; i < previewLines; i++) {
					String line = br.readLine();
					if (line != null) {
						System.out.println(line);
					} else {
						break eof;
					}
				}

				BufferedReader brConsole = new BufferedReader(
						new InputStreamReader(System.in));
				String command = brConsole.readLine();
				if (command.equals(" ")) {
					previewLines = 10;
				} else if (command.isEmpty()) {
					previewLines = 1;
				} else if (command.equalsIgnoreCase("q")) {
					System.out.println("<quit preview..>");
					break eof;
				} else {
					LOG.debug("unexpected input: "
							+ command
							+ ", please input blank+enter or enter to continue, or q+enter to quit");
					previewLines = 10;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				br.close();
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String readFile(Object fileObj) {
		File file = null;
		FileReader fileReader = null;
		BufferedReader br = null;
		try {
			// LOG.debug("fileObj: " + fileObj);
			if (fileObj instanceof String) {
				file = new File((String) fileObj);
			} else if (fileObj instanceof File) {
				file = (File) fileObj;
			} else {
				LOG.error("only String or File accepted for parameter fileObj");
				return null;
			}
			// LOG.debug("file: " + fileObj);
			long length = file.length();
			// LOG.debug("file length: " + length);
			// System.out.println(length);
			fileReader = new FileReader(file);
			br = new BufferedReader(fileReader);

			if (length <= Integer.MAX_VALUE) {
				int lengthI = (int) length;
				char[] cbuf = new char[lengthI];
				br.read(cbuf);
				return new String(cbuf).trim();
			} else {
				StringBuffer sb = new StringBuffer();
				while (true) {
					String line = br.readLine();
					if (line != null) {
						sb.append(line);
						sb.append("\n");
					} else
						break;
				}
				return null;
			}
		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
			return null;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static ArrayList<String> readAsLines(Object file) {
		ArrayList<String> lines = new ArrayList<String>();
		FileReader fileReader = null;
		BufferedReader br = null;
		try {
			if (file instanceof String) {
				fileReader = new FileReader((String) file);
			} else if (file instanceof File) {
				fileReader = new FileReader((File) file);
			}
			br = new BufferedReader(fileReader);
			while (true) {
				String line = br.readLine();
				if (line != null) {
					lines.add(line);
				} else
					break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return lines;
	}

	public static void separateToWords(String filePath, String regex) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			BufferedReader a = new BufferedReader(fileReader);
			TreeSet<String> words = new TreeSet<String>();
			while (true) {
				String line = a.readLine();
				if (line != null) {
					words.addAll(Arrays.asList(line.split(regex)));
				} else
					break;
			}
			a.close();
			fileReader.close();

			String[] part = filePath.split("\\.");
			fw = new FileWriter(part[0] + "_words." + part[1], true);
			bw = new BufferedWriter(fw, 8192 * 100);
			for (String word : words) {
				bw.write(word);
				bw.newLine();
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sortLines(String filePath) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			FileReader fileReader;
			fileReader = new FileReader(filePath);
			BufferedReader a = new BufferedReader(fileReader);
			TreeSet<String> lines = new TreeSet<String>();
			while (true) {
				String line = a.readLine();
				if (line != null) {
					lines.add(line);
				} else
					break;
			}
			a.close();
			fileReader.close();

			String[] part = filePath.split("\\.");
			fw = new FileWriter(part[0] + "_sorted." + part[1], false);
			bw = new BufferedWriter(fw, 8192 * 100);
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static LinkedHashMap<String, Long> statisticsPredicate(
			List<String> paths) {
		LinkedHashMap<String, Long> result = new LinkedHashMap<String, Long>();
		// HashSet<String> all = new HashSet<String>();
		FileReader fileReader = null;
		BufferedReader br = null;
		try {
			for (String path : paths) {
				System.out.println("statistics file: " + path);
				fileReader = new FileReader(path);
				br = new BufferedReader(fileReader);
				while (true) {
					String line = br.readLine();
					if (line != null) {
						// if
						// (line.startsWith("<http://data.samsung.com/ontology/dining/person/"))
						// {
						// continue;
						// }

						// if (all.contains(line)) {
						// System.out.println("Duplicate line: " + line);
						// } else {
						// all.add(line);
						String[] parts = line.split(" ");
						if (result.keySet().contains(parts[1])) {
							result.put(parts[1], result.get(parts[1]) + 1);
						} else {
							result.put(parts[1], 1L);
						}
						// }
					} else {
						break;
					}
				}
			}
			br.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String predicate : result.keySet()) {
			System.out.println(predicate + "\t" + result.get(predicate));
		}

		return result;
	}

	public static void tail(Object fileObj, long lines) {
		File file = null;
		RandomAccessFile raf = null;
		try {
			if (fileObj instanceof String) {
				file = new File((String) fileObj);
			} else if (fileObj instanceof File) {
				file = (File) fileObj;
			} else {
				LOG.error("only String or File accepted for parameter fileObj");
				return;
			}

			raf = new RandomAccessFile(file, "r");
			long len = raf.length();
			if (len != 0L) {
				long tailLines = 0;
				long pos = len - 1;
				while (pos > 0 && tailLines < lines) {
					pos--;
					raf.seek(pos);
					/*
					 * if we still get here when the pos is 0, no need to check
					 * if it is '\n' at the first byte, and avoid to lost the
					 * byte by readByte()
					 */
					if (pos != 0 && raf.readByte() == '\n') {
						tailLines++;
					}
				}
			}

			for (int i = 0; i < lines; i++) {
				String line = raf.readLine();
				if (line != null) {
					System.out.println(new String(line.getBytes("8859_1"),
							"UTF-8"));
				} else {
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeObjectSerializationToFile(String filePath,
													  Serializable serializableObject) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filePath);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(serializableObject);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void writeStringToFile(String filePath, String content,
										 boolean append) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(filePath, append);
			osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
			bw = new BufferedWriter(osw, 8192 * 1000);
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void wirteMapToFile(LinkedHashMap<String, Long> result,
									  String resultFile) {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;

		try {
			fos = new FileOutputStream(resultFile, false);
			osw = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
			bw = new BufferedWriter(osw, 8192 * 1000);

			for (Entry<String, Long> entry : result.entrySet()) {
				bw.write(entry.getKey() + "\t" + entry.getValue());
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
