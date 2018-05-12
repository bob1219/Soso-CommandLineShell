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

public class Main {
	public static void main(String[] args) {
		try {
			if(args.length == 1) {
				CommandLine();
			} else if(args.length == 2) {
				CommandProcessor.script(args[1]);
			} else {
				System.err.println("Usage: java -jar shell.jar <script-filename>");
				System.exit(1);
			}
		} catch(soso_cmd.Exception e) {
			System.err.println("Error: " + e);
		}
	}

	private static void CommandLine() throws soso_cmd.Exception {
		System.out.println("Soso-CommandLineShell");
		System.out.println("Copyright 2018 Daiki Yoshida. All rights reserved.");
		System.out.println();
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
		System.out.println("This is free software, and you are welcome to redistribute it");
		System.out.println("under certain conditions.");
		System.out.println();

		while(true) {
			System.out.print('>');

			try {
				String command = new BufferedReader(new InputStreamReader(System.in)).readLine();
			} catch(IOException e) {
				throw new soso_cmd.Exception("standard-input input error");
			}

			if(command == null || command.equals("")) {
				continue;
			}

			CommandProcessor.CommandProcess(ArgsSpliter.split(command));

			System.out.println();
		}
	}
}
