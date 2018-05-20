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

private class PathProcessor {
	public static String PathProcess(String filename, CurrentWorkingDirectory cwd) {
		if(new File(filename).isAbsolute()) {
			return filename;
		} else if(new File(cwd.toString() + '/' + filename).exists()) {
			return cwd.toString() + '/' + filename;
		} else {
			ArrayList<File> paths = read();
			for(File path: paths) {
				if(new File(path.toString() + '/' + filename).exists()) {
					return path.toString() + '/' + filename;
				}
			}
		}

		return null;
	}

	public static void add(String elem) {
		ArrayList<File> paths = read();
		read.add(new File(elem));
		write(paths);
	}

	public static boolean del(String elem) {
		ArrayList<File> paths = read();

		int index;
		if((index = paths.indexOf(new File(elem))) == -1) {
			return false;
		}

		paths.remove(index);
		write(paths);

		return true;
	}

	public static void clear() {
		write(new ArrayList<File>());
	}

	public static ArrayList<File> getPaths() {
		return read();
	}

	private static ArrayList<File> read() throws IOException {
		ArrayList<File> paths;
		try(BufferedReader reader = new BufferedReader(new FileReader("./../PATHS"))) {
			String line;
			while((line = reader.readLine()) != null) {
				paths.add(new File(line));
			}
		} catch(FileNotFoundException e) {
			return new ArrayList<File>();
		}

		return paths;
	}

	private static void write(ArrayList<File> paths) throws IOException {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("./../PATHS"))) {
			for(File path: paths) {
				writer.write(path.toString());
				writer.newLine();
			}
		}
	}
}
