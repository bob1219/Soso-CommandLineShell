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

import soso_cmd.CommandProcessor;

class Main {
	public static void main(String[] args) {
		try {
			if(args.length == 1) {
				CommandLine();
			} else if(args.length == 2) {
				CommandProcessor.script(args[1]);
			} else {
				System.err.println("Usage: java shell.jar <script-filename>");
				System.exit(1);
			}
		} catch(soso_cmd.Exception e) {
			System.err.println("Error: " + e);
		}
	}
}
