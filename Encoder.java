// ------------------------------
// Kevin Roesner and Vincent Nguy
// CS361 Assignment 2 
// Huffman Encoding
// Encoder Class (with main)
// ------------------------------

import java.io.*;
import java.util.*;
import java.lang.Math;


public class Encoder {

	public static void main(String[] args)throws FileNotFoundException, IOException {
		int[] countTracker = new int[26];
		ArrayList<TreeNode> symbolList = new ArrayList<TreeNode> ();
		int count = 0;
		double entropy = 0;
		double efficiency = 0;
		TreeNode root;
		String file = args[0];
		int testFileSize = Integer.parseInt(args[1]);
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		String currentLine = in.readLine();
		int numSymbols = 0;
		while(currentLine != null) {
			if(currentLine.equals("")){
				currentLine = in.readLine();
				continue;
			}
			int freq = Integer.parseInt(currentLine);
			countTracker[numSymbols++] = freq;
			count += freq;
			currentLine = in.readLine();
		}		
		entropy = calcEntropy(countTracker, numSymbols, count);
		System.out.println("Entropy = " + entropy);
		createSymbolList(1, countTracker, numSymbols, count, symbolList);
		PriorityQueue<TreeNode> queue = new PriorityQueue<TreeNode>(26, new TreeNodeComparator());
		int numEncodings = symbolList.size();
		for(int i = 0; i < numEncodings; ++i){
			queue.add(symbolList.get(i));
		}
		
		root = buildTree(queue);
		HashMap<String, String> keyMap = buildKey(root);
		HashMap<String, String> valueMap = buildValueMap(keyMap);
		
		int size = keyMap.size();
		for(int i = 0; i < numEncodings; ++i){
			String name = symbolList.get(i).name;
			System.out.println("Name: " + name + ", Encoding: " + keyMap.get(name));
		}
		
		generateTestFile(testFileSize, numSymbols, symbolList);
		efficiency = encodeFile(keyMap, "testText", testFileSize);
		decodeFile(valueMap, "testText");
		
		System.out.println("Efficiency = " + (entropy / efficiency));
		
	}
	
	// ---------------
	// createSymbolList
	// ----------------
	
	public static void createSymbolList(int mode, int[] c, int numSymbols, int count, ArrayList<TreeNode> symbolList) {
		if(mode == 1) {
			for(int i = 0; i < numSymbols; i++) {
				String symbol = ""+(char)(i+65);
				float currentFreq = (float)c[i] / count;
				TreeNode currentNode = new TreeNode(symbol, currentFreq);
				symbolList.add(currentNode);
			}
		}		
	}
	
	// ---------
	// buildTree
	// ---------
	
	public static TreeNode buildTree(PriorityQueue<TreeNode> q){
		TreeNode root;
		TreeNode left;
		TreeNode right;
		while(q.size() > 1){
			left = q.poll();
			right = q.poll();
			q.add(new TreeNode(null, left.probability + right.probability, left, right));
		}
		root = q.poll();
		return root;
	}
	
	// --------
	// buildKey
	// --------
	
	public static HashMap<String,String> buildKey(TreeNode root) {
		HashMap<String,String> result = new HashMap<String,String>();
		buildKeyb(root, "", result);
		return result;
	}
	
	// ---------
	// buildKeyb
	// ---------
	
	// Helper function for buildKey
	
	public static void buildKeyb(TreeNode node, String prefix, HashMap<String, String> result){
		if(node.isLeaf()){
			result.put(node.name, prefix);
		}else{
			buildKeyb(node.left, prefix + "0", result);
			buildKeyb(node.right, prefix + "1", result);
		}
	}
	
	// -------------
	// buildValueMap
	// -------------
	
	public static HashMap<String, String> buildValueMap(HashMap<String, String> key){
		HashMap<String, String> result = new HashMap<String,String>();
		for(String current : key.keySet()){
			result.put(key.get(current), current);
		}
		return result;
	}
	
	// -----------
	// calcEntropy
	// ----------
	
	public static double calcEntropy(int[] countTracker, int numSymbols, int totalCount){
		double entropy = 0;
		for(int i = 0; i < numSymbols; ++i){
			double temp = (double) countTracker[i] / totalCount;
			entropy += (temp * Math.log(temp));
		}
		return -entropy;
	}
	
	// ----------
	// encodeFile
	// ----------
	
	public static double encodeFile(HashMap<String, String> key, String file, int size) throws IOException{
		int totalBits = 0;
		BufferedReader in;
		try{
			in = new BufferedReader(new FileReader(file));
		}catch(FileNotFoundException e){
			System.out.println("Error: can't open test file to encode.");
			return -1;
		}
		
		String newfile = file + ".enc1";
		File f = new File(newfile);
		
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(IOException e){
				System.err.println("Error: Can't open new file to write output.");
				return -1;
			}
		}
		
		FileWriter fw; 
		
		try{
			fw = new FileWriter(f.getAbsoluteFile());
		}catch(IOException e){
			System.err.println("Error: cannot write output to file.");
			return -1;
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		int c;
		while((c = in.read()) != -1){
			String temp = key.get("" + (char)c);
			bw.write(temp);
			totalBits += temp.length();
		}
		
		bw.close();
		
		return (double) totalBits / size;
	}
	
	// ----------
	// decodeFile
	// ----------
	
	public static void decodeFile(HashMap<String, String> valueMap, String file) throws IOException{
		BufferedReader in;
		try{
			in = new BufferedReader(new FileReader(file + ".enc1"));
		}catch(FileNotFoundException e){
			System.out.println("Error: can't open test file to encode.");
			return;
		}
		
		String newfile = file + ".dec1";
		File f = new File(newfile);
		
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(IOException e){
				System.err.println("Error: Can't open new file to write output.");
				return;
			}
		}
		
		FileWriter fw; 
		
		try{
			fw = new FileWriter(f.getAbsoluteFile());
		}catch(IOException e){
			System.err.println("Error: cannot write output to file.");
			return;
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		String val = "";
		int c;
		while((c = in.read()) != -1){
			c -= 48;
			val = val + c;
			if(valueMap.containsKey(val)){
				bw.write(valueMap.get(val));
				val = "";
			}
		}
		
		bw.close();
		
	}
	
	// ----------------
	// generateTestFile
	// ----------------
	
	public static void generateTestFile(int size, int numSymbols, ArrayList<TreeNode> symbolList) throws IOException{
		File f = new File("testText");
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(IOException e){
				System.err.println("Error: Can't open new file to write output.");
				return;
			}
		}
		
		FileWriter fw = null;
		try{
			fw = new FileWriter(f.getAbsoluteFile());
		}catch(IOException e){
			System.err.println("Error: cannot write output to file.");
			return;
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(int i = 0; i < size; ++i){
			Random r = new Random();
			int temp = r.nextInt(100);
			int c = '0';
			double count = 0;
			for(int j = 0; j < numSymbols; ++j){
				count += symbolList.get(j).probability;
				if((double)temp < (count * 100)){
					c = (char)(j + 65);
					break;
				}
			}
			bw.write(c);
		}
		
		bw.close();
	}
	
	// --------------------------------------
	// Comparator for PriorityQueue<TreeNode>
	// --------------------------------------
	
	static class TreeNodeComparator implements Comparator<TreeNode>{
		public int compare(TreeNode a, TreeNode b){
			if(a.probability - b.probability >= 0){
				return 1;
			}
			return -1;
		}
	}	
}
