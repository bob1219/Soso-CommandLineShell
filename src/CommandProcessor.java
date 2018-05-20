// Copyright 2018 Daiki Yoshida. All rights reserved.
//
// This file is part of Soso-CommandLineShell.
//
// Soso-CommandLineShell is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Soso-CommandLineShell is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Soso-CommandLineShell. If not, see <http://www.gnu.org/licenses/>.

package soso_cmd;
import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;

private class CommandProcessor {
	public static void CommandProcess(String[] cmdarray, CurrentWorkingDirectory cwd) throws soso_cmd.Exception, IOException {
		try {
			switch(cmdarray[0]) {
			case "mkfile":
				command_mkfile(cmdarray[1], cwd);
				break;

			case "rmfile":
				command_rmfile(cmdarray[1], cwd);
				break;

			case "cpfile":
				command_cpfile(cmdarray[1], cmdarray[2], cwd);
				break;

			case "mkdir":
				command_mkdir(cmdarray[1], cwd);
				break;

			case "rmdir":
				command_rmdir(cmdarray[1], cwd);
				break;

			case "cpdir":
				command_cpdir(cmdarray[1], cmdarray[2], cwd);
				break;

			case "list":
				if(cmdarray.length == 1) {
					command_list(cwd.toString());
				} else {
					command_list(cmdarray[1]);
				}
				break;

			case "tview":
				command_tview(cmdarray[1], cwd);
				break;

			case "bview":
				command_bview(cmdarray[1], cwd);
				break;

			case "app":
				// appArgs = {cmdarray[1], cmdarray[2], ..., cmdarray[cmdarray.length - 1]}
				String[] appArgs = new String[cmdarray.length - 1];
				for(int i = 1; i < cmdarray.length; ++i) {
					appArgs[i - 1] = cmdarray[i];
				}
				command_app(appArgs);
				break;

			case "path":
				switch(cmdarray[1]) {
				case "add":
					command_path_add(cmdarray[2]);
					break;

				case "del":
					command_path_del(cmdarray[2]);
					break;

				case "clear":
					command_path_clear();
					break;

				case "list":
					command_path_list();
					break;

				default:
					throw new soso_cmd.Exception("unknown option of path command");
				}
				break;

			case "cwdir":
				if(cmdarray.length == 1) {
					command_cwdir_get();
				} else {
					cwd = command_cwdir_set(cmdarray[1]);
				}
				break;

			case "exit":
				System.exit(0);
				break;

			case "script":
				script(cmdarray[1], cwd);
				break;

			case "now":
				command_now();
				break;

			default:
				throw new soso_cmd.Exception("unknown command");
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new soso_cmd.Exception("few or many args");
		}
	}

	private static void command_mkfile(String filename, CurrentWorkingDirectory cwd) throws IOException {
		cwd.getAbsolutePath(new File(filename)).createNewFile();
	}

	private static void command_rmfile(String filename) throws soso_cmd.Exception {
		if(!new File(filename).delete()) {
			throw new soso_cmd.Exception("failed remove a file");
		}
	}

	private static void command_cpfile(String source, String dest) throws IOException {
		Files.copy(new File(source).toPath(), new File(dest).toPath(), REPLACE_EXISTING);
	}

	private static void command_mkdir(String dirname) throws soso_cmd.Exception {
		if(!new File(dirname).mkdir()) {
			throw new soso_cmd.Exception("failed make a directory");
		}
	}

	private static void command_rmdir(String dirname) throws soso_cmd.Exception {
		if(!deleteDirectory(new File(dirname))) {
			throw new soso_cmd.Exception("failed remove a directory");
		}
	}

	// helper of command_rmdir method
	private static boolean deleteDirectory(File file) {
		if(!file.exists()) {
			return false;
		}

		if(file.isFile()) {
			return file.delete();
		} else {
			for(File FileInTheDir: file.listFiles()) {
				return deleteDirectory(FileInTheDir);
			}
		}
	}

	private static void command_cpdir(String source, String dest) throws soso_cmd.Exception, IOException {
		if(!copyDirectory(new File(source), new File(dest))) {
			throw new soso_cmd.Exception("failed copy directory");
		}
	}

	// helper of command_cpdir method
	private static boolean copyDirectory(File source, File dest) throws IOException {
		if(!source.exists() || (!dest.exists() && !dest.mkdir())) {
			return false;
		}

		for(File FileInTheDir: source.listFiles()) {
			if(FileInTheDir.isFile()) {
				Files.copy(FileInTheDir.toPath(), new File(dest.toString() + '/' + FileInTheDir.getName()).toPath(), REPLACE_EXISTING);
			} else {
				return copyDirectory(FileInTheDir, new File(dest.toString() + '/' + FileInTheDir.getName));
			}
		}

		return true;
	}

	private static void command_list(String dirname) throws soso_cmd.Exception, IOException {
		File file = new File(dirname);

		if(!file.exists()) {
			throw new soso_cmd.Exception("directory \"" + dirname + "\" do not exists");
		}

		try {
			for(File FileInTheDir: file.listFiles()) {
				if(FileInTheDir.isFile()) {
					System.out.println("File:\t" + FileInTheDir.toString());
				} else {
					System.out.println("Dir:\t" + FileInTheDir.toString());
				}
			}
		} catch(NullPointerException e) {
			if(file.isFile()) {
				throw new soso_cmd.Exception("it is not a directory");
			} else {
				throw new IOException();
			}
		}
	}

	private static void command_tview(String filename) throws soso_cmd.Exception, IOException {
		try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			for(int i = 1; (line = reader.readLine()) != null; ++i) {
				System.out.println(i + ":\t" + line);
			}
		} catch(FileNotFoundException e) {
			throw new soso_cmd.Exception("file \"" + filename + "\" not found");
		}
	}

	private static void command_bview(String filename) throws soso_cmd.Exception, IOException {
		final int fileSizeMax = 20480;
		final int byteUnitSizeMax = 16;
		try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filename))) {
			// Read file
			byte[] bytes = new byte[fileSizeMax];
			final int allBytesNum = stream.read(bytes);
			if(allBytesNum == -1) {
				return;
			}

			// Split bytes by 16 bytes
			final int byteUnitsNum = allBytesNum / byteUnitSizeMax;
			byte[][] byteUnits = new byte[byteUnitSizeMax][byteUnitsNum];
			int[] bytesNumsInByteUnits = new byte[byteUnitsNum];
			for(int i = 0; i < byteUnitsNum; ++i) {
				final int byteIndex = byteUnitSizeMax * i + j;
				int j = 0;
				for(; byteIndex < allBytesNum; ++i) {
					byteUnits[i][j] = bytes[byteIndex];
				}
				bytesNumsInByteUnits[i] = j + 1;
			}

			// Print
			System.println("\t+0 +1 +2 +3 +4 +5 +6 +7 +8 +9 +A +B +C +D +E +F 0123456789ABCDEF");
			for(int i = 0; i < byteUnits.length; ++i) {
				System.print(Integer.toHexString(0x10 * i).toUpperCase());
				System.print(":\t");

				// Print binary
				for(int j = 0; j < byteUnitSizeMax && j < bytesNumsInByteUnits[i]; ++j) {
					System.print(Integer.toHexString(byteUnits[i][j]).toUpperCase() + ' ');
				}

				// Print space for view control
				if(bytesNumsInByteUnits[i] < byteUnitSizeMax) {
					for(int j = 1; j <= byteUnitSizeMax - bytesNumsInByteUnits[i]; ++j) {
						for(int k = 1; k <= 3; ++k) {
							System.out.print(' ');
						}
					}
				}

				// Print ASCII character
				for(int j = 0; j < byteUnitSizeMax && j < bytesNumsInByteUnits[i]; ++j) {
					System.out.println(Character.isISOControl((int)byteUnits[i][j]) ? '.' : byteUnits[i][j]);
				}

				System.out.println();
			}
		} catch(FileNotFoundException e) {
			throw new soso_cmd.Exception("file \"" + filename + "\" not found");
		}
	}

	private static void command_app(String[] cmdarray) throws IOException, soso_cmd.Exception {
		cmdarray[0] = PathProcessor.PathProcess(cmdarray[0]);
		try {
			Runtime.getRuntime().exec(cmdarray);
		} catch(IndexOutOfBoundsException e) {
			throw new soso_cmd.Exception("few or many args");
		}
	}

	private static void command_path_add(String pathElem) {
		PathProcessor.add(pathElem);
	}

	private static void command_path_del(String pathElem) {
		PathProcessor.del(pathElem);
	}

	private static void command_path_clear() {
		PathProcessor.clear();
	}

	private static void command_path_list() {
		PathProcessor.list();
	}
