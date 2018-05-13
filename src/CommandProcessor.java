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
	public static void CommandProcess(String[] args) throws soso_cmd.Exception {
		try {
			switch(args[0]) {
			case "mkfile":
				command_mkfile(args[1]);
				break;

			case "rmfile":
				command_rmfile(args[1]);
				break;

			case "cpfile":
				command_cpfile(args[1], args[2]);
				break;

			case "mkdir":
				command_mkdir(args[1]);
				break;

			case "rmdir":
				command_rmdir(args[1]);
				break;

			case "cpdir":
				command_cpdir(args[1], args[2]);
				break;

			case "list":
				if(args.length == 1) {
					command_list(".");
				} else {
					command_list(args[1]);
				}
				break;

			case "tview":
				command_tview(args[1]);
				break;

			case "bview":
				command_bview(args[1]);
				break;

			case "app":
				// appArgs = {args[1], args[2], ..., args[args.length - 1]};
				String[] appArgs = new String[args.length - 1];
				for(int i = 1; i < args.length; ++i) {
					appArgs[i - 1] = args[i];
				}
				command_app(appArgs);
				break;

			case "path":
				switch(args[1]) {
				case "add":
					command_path_add(args[2]);
					break;

				case "del":
					command_path_del(args[2]);
					break;

				case "clear":
					command_path_clear();
					break;

				default:
					throw new soso_cmd.Exception("unknown option of path command");
				}
				break;

			case "chdir":
				command_chdir(args[1]);
				break;

			case "cwdir":
				command_cwdir();
				break;

			case "exit":
				System.exit(0);

			case "script":
				script(args[1]);
				break;

			default:
				throw new soso_cmd.Exception("unknown command");
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new soso_cmd.Exception("few or many args");
		}
	}

	private static void command_mkfile(String filename) throws soso_cmd.Exception {
		try {
			new File(filename).createNewFile();
		} catch(IOException e) {
			throw new soso_cmd.Exception("I/O error");
		} catch(SecurityException e) {
			throw new soso_cmd.Exception("access denied");
		}
	}

	private static void command_rmfile(String filename) throws soso_cmd.Exception {
		try {
			if(!new File(filename).delete()) {
				throw new soso_cmd.Exception("failed remove a file");
			}
		} catch(SecurityException e) {
			throw new soso_cmd.Exception("access denied");
		}
	}

	private static void command_cpfile(String source, String dest) throws soso_cmd.Exception {
		try {
			Files.copy(new File(source).toPath(), new File(dest).toPath(), REPLACE_EXISTING);
		} catch(IOException e) {
			throw new soso_cmd.Exception("I/O error");
		}
	}

	private static void command_mkdir(String dirname) throws soso_cmd.Exception {
		try {
			if(!new File(dirname).mkdir()) {
				throw new soso_cmd.Exception("failed make a directory");
			}
		} catch(SecurityException e) {
			throw new soso_cmd.Exception("access denied");
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
			try {
				return file.delete();
			} catch(SecurityException e) {
				return false;
			}
		} else {
			for(File FileInTheDir: file.listFiles()) {
				return deleteDirectory(FileInTheDir);
			}
		}
	}

	private static void command_cpdir(String source, String dest) throws soso_cmd.Exception {
		if(!copyDirectory(new File(source), new File(dest))) {
			throw new soso_cmd.Exception("failed copy directory");
		}
	}

	// helper of command_cpdir method
	private static boolean copyDirectory(File source, File dest) {
		if(!source.exists()) {
			return false;
		}

		if(!dest.exists()) {
			try {
				if(!dest.mkdir()) {
					return false;
				}
			} catch(SecurityException e) {
				return false;
			}
		}

		for(File FileInTheDir: source.listFiles()) {
			if(FileInTheDir.isFile()) {
				try {
					Files.copy(FileInTheDir.toPath(), new File(dest.toString() + '/' + FileInTheDir.getName()).toPath(), REPLACE_EXISTING);
				} catch(IOException e) {
					return false;
				}
			} else {
				return copyDirectory(FileInTheDir, new File(dest.toString() + '/' + FileInTheDir.getName));
			}
		}

		return true;
	}

	private static void command_list(String dirname) throws soso_cmd.Exception {
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
				throw new soso_cmd.Exception("I/O error");
			}
		} catch(SecurityException e) {
			throw new soso_cmd.Exception("access denied");
		}
	}

	private static void command_tview(String filename) throws soso_cmd.Exception {
		try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			for(int i = 1; (line = reader.readLine()) != null; ++i) {
				System.out.println(i + ":\t" + line);
			}
		} catch(FileNotFoundException e) {
			throw new soso_cmd.Exception("file \"" + filename + "\" not found");
		} catch(IOException e) {
			throw new soso_cmd.Exception("I/O error");
		}
	}

	private static void command_bview(String filename) throws soso_cmd.Exception {
		final int FileSizeMax = 20480;
		try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filename))) {
			byte[] bytes = new byte[FileSizeMax];
			int allBytesNum = stream.read(bytes);
			if(allBytesNum == -1) {
				return;
			}

			byte[][] byteUnits = new byte[16][allBytesNum / 16];
			int[] bytesNums = new byte[allBytesNum / 16];
			for(int i = 0; i < allBytesNum / 16; ++i) {
				int j = 0;
				for(; j < 16 && 16 * i + j < allBytesNum; ++i) {
					byteUnits[i][j] = bytes[16 * i + j];
				}
				bytesNums[i] = j + 1;
			}

			System.println("\t+0 +1 +2 +3 +4 +5 +6 +7 +8 +9 +A +B +C +D +E +F 0123456789ABCDEF");
			for(int i = 0; i < byteUnits.length; ++i) {
				System.print(Integer.toHexString(0x10 * i).toUpperCase());
				System.print(":\t");

				for(int j = 0; j < 16 && j < bytesNums[i]; ++j) {
					System.print(Integer.toHexString(byteUnits[i][j]).toUpperCase() + ' ');
				}

				if(bytesNums[i] < 16) {
					for(int j = 1; j <= 16 - bytesNums[i]; ++j) {
						for(int k = 1; k <= 3; ++k) {
							System.out.print(' ');
						}
					}
				}

				for(int j = 0; j < 16 && j < bytesNums[i]; ++j) {
					System.out.println(Character.isISOControl((int)byteUnits[i][j]) ? '.' : byteUnits[i][j]);
				}

				System.out.println();
			}
		} catch(FileNotFoundException e) {
			throw new soso_cmd.Exception("file \"" + filename + "\" not found");
		} catch(IOException e) {
			throw new soso_cmd.Exception("I/O error");
		}
	}
}
