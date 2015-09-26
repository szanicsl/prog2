import java.io.PrintStream;
import java.io.PrintWriter;

public class LZWBinaryTree {
	
	public LZWBinaryTree(){
		
		tree = root;
		
	}
	
	public void investigateBit(char data){
		if (data == '0'){
			if (tree.zeroChild() == null){
				Node newnode = new Node('0',null,null);
				
				tree.newzeroChild(newnode);
				tree = root;
			}
			else{
				tree = tree.zeroChild();
			}
		}
		else{
			if (tree.oneChild() == null){
				Node newnode = new Node('1',null,null);
				
				tree.newoneChild(newnode);
				tree = root;
			}
			else{
				tree = tree.oneChild();
			}
		}
	}
	
	public void writeout(){
		depth = 0;
		writeout(root, new java.io.PrintWriter(System.out));
	}
	
	public void writeout(java.io.PrintWriter os){
		depth = 0;
		writeout(root, os);
	}
	
	class Node {
		public Node (char data, Node rightOne, Node leftZero){
			this.data = data;
			this.rightOne = rightOne;
			this.leftZero = leftZero;
		};
		public Node zeroChild(){
			return leftZero;
		}
		public Node oneChild(){
			return rightOne;
		}
		public void newzeroChild(Node child){
			leftZero = child;
		}
		public void newoneChild(Node child){
			rightOne = child;
		}
		private char data;
		private Node rightOne;
		private Node leftZero;
	};
	private Node tree = null;
	private int avgsum, avgnum, i;
	private double dispsum;
	private int maxDepth = 0;
	private int depth = 0;
	
	public void writeout(Node onenode, PrintWriter os){
		if (onenode != null){
			depth++;
			if(maxDepth<depth){
				maxDepth = depth-1;
			}
			writeout(onenode.oneChild(), os);
			for (i = 0; i<depth; i++){
				os.print("---");
			}
			os.print(onenode.data);
			os.print("(");
			os.print(depth-1);
			os.println(")");
			writeout(onenode.zeroChild(), os);
			
			depth--;
		}
	}
	
	protected Node root = new Node('/',null,null);
	
	double avg, disp;
	
	public double getAvg(){
		depth = avgsum = avgnum = 0;
		rAvg(root);
		avg = ((double) avgsum)/ avgnum;
		return avg;
	}
	
	public double getDisp(){
		avg = getAvg();
		dispsum = 0.0;
		depth = avgnum = 0;
		rDisp(root);
		if (avgnum - 1 > 0){
			disp = Math.sqrt(avgsum/ (avgnum-1));
		}
		else{
			disp = Math.sqrt(avgsum);
		}
		
		return disp;
	}
	
	public void rAvg(Node onenode){
		if(onenode != null){
			depth++;
			rAvg(onenode.oneChild());
			rAvg(onenode.zeroChild());
			depth--;
			if (onenode.oneChild() == null && onenode.zeroChild() == null){
				avgnum++;
				avgsum += depth;
			}
		}
	}
	
	public void rDisp(Node onenode){
		if (onenode != null){
			depth++;
			rDisp(onenode.oneChild());
			rDisp(onenode.zeroChild());
			depth--;
			if (onenode.oneChild() == null && onenode.zeroChild() == null){
				avgnum++;
				dispsum += ((depth - avg) * (depth - avg));
			}
		}
	}
	
	public static void main(String args[]){
		boolean comment=false;
		int i;
		
		try{
			java.io.FileInputStream inFile = 
					new java.io.FileInputStream(new java.io.File(args[0]));
			
			java.io.PrintWriter outFile =
					new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(args[2])));
		
			byte[] b = new byte[1];
			LZWBinaryTree binaryTree = new LZWBinaryTree();
			while(inFile.read(b) != -1){
				if (b[0] == 0x0a){
					break;
				}
			}
			while(inFile.read(b) != -1){
				if(b[0] == 0x3e){ //
					comment = true;
					continue;
				}
				if(b[0] == 0x0a){
					comment = false;
					continue;
				}
				if(comment){
					continue;
				}
				if(b[0] == 0x4e){
					continue;
				}
				for(i=0;i<8;i++){
					if((b[0] & 0x80) != 0){
						binaryTree.investigateBit('1');
					}
					else{
						binaryTree.investigateBit('0');
					}
					b[0] <<= 1;
				}
			}
		
			binaryTree.writeout(outFile);
			outFile.println("depth = " + binaryTree.maxDepth);
		    outFile.println("mean = " + binaryTree.getAvg());
		    outFile.println("var = " + binaryTree.getDisp());
		
			outFile.close();
			inFile.close();
		} catch (java.io.FileNotFoundException fnfException) {
			fnfException.printStackTrace();
		} catch (java.io.IOException ioException) {
			ioException.printStackTrace();
		}
	} 
}