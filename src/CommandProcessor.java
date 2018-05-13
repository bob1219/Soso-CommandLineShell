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
				if(!deleteDirectory(FileInTheDir)) {
					return false;
				}
			}
		}
	}

	private static void command_cpdir(String source, String dest) throws soso_cmd.Exception {
		if(!copyDirectory(new File(source), new File(dest))) {
			throw new soso_cmd.Exception("failed copy directory");
		}
	}

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

		for(File f: source.listFiles()) {
			if(f.isFile()) {
				try {
					Files.copy(f.toPath(), new File(dest.toString() + '/' + f.getName()).toPath(), REPLACE_EXISTING);
				} catch(IOException e) {
					return false;
				}
			} else {
				return copyDirectory(f, new File(dest.toString() + '/' + f.getName));
			}
		}

		return true;
	}

	private static void list(String dirname) throws soso_cmd.Exception {
		File file = new File(dirname);

		if(!file.exists()) {
			throw new soso_cmd.Exception("directory \"" + dirname + "\" do not exists");
		}

		for(File f: file) {
			if(f.isFile()) {
				System.out.println("File:\t" + f.toString());
			} else {
				System.out.println("Dir:\t" + f.toString());
			}
		}
	}
}
