import java.io.*;
import java.net.*;

public class Client {
	

	public static void main(String[] pArgs) 
	{
		if(pArgs.length<3)
		{
			System.out.println("usage: java Client host port boardnum");
			return;
		}

		try
		{
			Socket lSocket=new Socket(pArgs[0],Integer.parseInt(pArgs[1]));
			PrintWriter lOut=new PrintWriter(lSocket.getOutputStream());
			BufferedReader lIn=new BufferedReader(new InputStreamReader(lSocket.getInputStream()));

			lOut.println(pArgs[2]);
			lOut.flush();

			String lLine=lIn.readLine();

			//read number of rows
			int lNumRows=Integer.parseInt(lLine);

			//read each row
			String[] rows = new String[lNumRows];
			int longestRow = 0;
			for(int i=0;i<lNumRows;i++)	{
				lLine=lIn.readLine();
//				System.out.println(lLine);
				if (longestRow < lLine.length())
					longestRow = lLine.length();
				rows[i] = lLine;
			}
			Surf board = new Surf (longestRow, rows);
			board.printMap();
			Agent bond = new Bond();
			//now, we should find a path from the player to any goal

			//we've found our solution
			//String lMySol="U R R U U L D L L U L L D R R R R L D D R U R U D L L U R";
			//these formats are also valid:
			//String lMySol="URRUULDLLULLDRRRRLDDRURUDLLUR";
			//String lMySol="0 3 3 0 0 2 1 2 2 0 2 2 1 3 3 3 3 2 1 1 3 0 3 0 1 2 2 0 3";

			//send the solution to the server
			String path = bond.solve(board);
			lOut.println(path);
			lOut.flush();

			//read answer from the server
			lLine=lIn.readLine();

			System.out.println(lLine);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
}
