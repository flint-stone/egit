package edu.uiuc.cs.cs425.unittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author lexu1 wwang84
 * testify Xgrep results for different patterns
 *
 */
public class RunningTest {


	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		//command="";
	}

	/**
	 * Test rare pattern
	 */
	@Test
	public void testRare() {
		//fail("Not yet implemented")
		int count=0;
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest.config", "Xgrep -value Hello" };
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 1497);
	}
	@Test
	public void testSomewhat() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest.config",  "Xgrep -value This" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 49773 );
	}
	
	@Test
	public void testFrequent() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest.config",  "Xgrep -value The" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
					count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 95841 );
	}
	
	
	@Test
	public void testRegExp2() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest.config",  "Xgrep -key '^2013/09/15--19-15-00'" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 4669+1 );
	}
	
	
	@Test
	public void testRegExp3() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest.config",  "Xgrep -value 'kill youself. $'" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 47982 );
	}

	
	
	@Test
	public void testRegExp4() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest.config",  "Xgrep -value '.ello'" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 1497);
	}
	
	@Test
	public void testRare2() {
		//fail("Not yet implemented")
		int count=0;
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config", "Xgrep -value Hello" };
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
					count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(count, 1497);
	}
	@Test
	public void testSomewhat2() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config",  "Xgrep -value This" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 49773*2);
	}
	
	@Test
	public void testFrequent2() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config",  "Xgrep -value The" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 95841*2);
	}
	@Test
	public void testRegExp12() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config",  "Xgrep -key '2013'" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 145614*2 );
	}
	
	@Test
	public void testRegExp22() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config",  "Xgrep -key '^2013/09/15--19-15-00'" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 4669*2+1 );
	}
	
	
	@Test
	public void testRegExp32() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config",  "Xgrep -value 'kill youself. $'" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 47982+47983 );
	}

	
	
	@Test
	public void testRegExp42() {
		//fail("Not yet implemented")
		
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config",  "Xgrep -value '.ello'" };
		int count=0;
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 1497);
	}
	
	/**
	 * Test rare pattern
	 */
	@Test
	public void testDiff() {
		//fail("Not yet implemented")
		int count=0;
		String[] command={"java","-cp","./bin", "edu.uiuc.cs.cs425.Client","server-unittest-diff.config", "Xgrep -value Hello" };
		try {
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains(":")&&line.contains("--"))
				//System.out.println(line);
				count++;
            }
			process.destroy();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(count, 1497);
	}
}
