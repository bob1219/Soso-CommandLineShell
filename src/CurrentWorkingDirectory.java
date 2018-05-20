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

private class CurrentWorkingDirectory {
	private File cwd;

	public CurrentWorkingDirectory() {
		cwd = new File(".");
	}

	public CurrentWorkingDirectory(String dirname) {
		cwd = new File(dirname);
	}

	public CurrentWorkingDirectory(File directory) {
		cwd = directory;
	}

	public File getAbsolutePath(File file) {
		return file.isAbsolute() ? file : new File(cwd.toString() + '/' + file.toString());
	}

	public File getCurrentWorkingDirectory() {
		return cwd;
	}

	@Override
	public String toString() {
		return cwd.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && (this == obj || (getClass() == obj.getClass() && cwd.equals(obj.cwd)));
	}

	@Override
	public int hashCode() {
		return cwd.hashCode();
	}

	public void setCurrentWorkingDirectory(File file) {
		cwd = file;
	}
}
