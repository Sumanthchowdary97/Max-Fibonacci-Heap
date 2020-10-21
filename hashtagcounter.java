import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class hashtagcounter {

    private static BufferedReader br; //to read input from file
    private static PrintWriter out;	// to write program output to a particular file

    static class Node {
        int key, degree; //key indicates the vlaue on which the heap is built and degree is the num of children a node holds
	/*
	left node is the previous node to the current node in the circular linkedlist
	right node is the next node in the circular linked list
	child is childnode
	parent is the node that is parent to the current node 
	*/
        Node left, right, child, parent;   
        boolean mark = false; //mark is true is a child has been cut off from the current node
        private String hashTag; // hashtag is the data that a node holds. 

	//Constructor that takes the hashtag and key and instantiates a new node
        Node(String hashTag, int key) {
            this.left = this.right = this;
            degree = 0;
            this.hashTag = hashTag;
            this.parent = null;
            this.key = key;
        }

    }

    static class maxFibonacciHeap {
        int numNodes; //num of nodes that a heap currently holds
        Node maxNode; //points to the max node in the heap

	/*inserts the node next to the max pointer in the heap*/
        void insertNode(Node node) {
            if (maxNode == null) {
                maxNode = node;
            } else {
                node.left = maxNode;
                node.right = maxNode.right;
                maxNode.right = node;
                if (node.right != null) {
                    node.right.left = node;
                } else {
                    node.right = maxNode;
                    maxNode.left = node;
                }
                if (node.key > maxNode.key) {
                    maxNode = node;
                }

            }
            numNodes++; //increase the number of nodes by one after the insertion
        }
	/*
	setKeyValue sets a node a particular value. once it sets the value,
	it checks if the value of the node is greater than the parent.
	if so, it calls the cut and cascade cut operation to adjust the heap

	*/
        void setKeyValue(Node a, int val) {
            if (val < a.key) {
                return;
            }
            a.key = val;
            Node b = a.parent;
            if (b != null && a.key > b.key) {
                cut(a, b);
                cascadeCut(b);
            }
            if (a.key > maxNode.key) {
                maxNode = a;
            }
        }
	/*popMax function removes an element from the heap.
	once an element is removed, we perform degreewise merging
	and decrease the sizeof the heap by one.	
	*/
        Node popMax() {
            Node c = maxNode;
            if (c != null) {
                int children = c.degree;
                Node a = c.child;
                Node tempRight;
                while (children-- > 0) {
                    tempRight = a.right;
                    a.left.right = a.right;
                    a.right.left = a.left;

                    a.left = maxNode;
                    a.right = maxNode.right;
                    maxNode.right = a;
                    a.right.left = a;

                    a.parent = null;
                    a = tempRight;
                }
                c.left.right = c.right;
                c.right.left = c.left;
                if (c == c.right) {
                    maxNode = null;
                } else {
                    maxNode = c.right;
                    mergeByDegree(); //degreewisemerging
                }
                numNodes--;
                return c;
            }
            return null; //null if heap is empty
        }
	/*
	cut the node a from node b.
	once a child is removed, a parents next child will be 
	the node right to the current child node
	*/
        void cut(Node a, Node b) { 
            a.left.right = a.right;
            a.right.left = a.left;
            b.degree--;

            if (b.child == a) {
                b.child = a.right;
            }
            if (b.degree == 0) {
                b.child = null;
            }
            a.left = maxNode;
            a.right = maxNode.right;
            maxNode.right = a;
            a.right.left = a;

            a.parent = null;
            a.mark = false;
        }
	/*
	cascadeCut cuts the node and continues on its path till 
	it reaches a node with mark = false or it hits the roots list
	*/
        void cascadeCut(Node b) {
            Node a = b.parent;
            if (a != null) {
                if (b.mark == false) {
                    b.mark = true;
                } else {
                    cut(b, a);
                    cascadeCut(a);
                }
            }
        }
	/*
	mergebydegree merges the nodes in the root list by its degree
	if two nodes have the same degree they are merged such that node with 
	the highest value is made parent to the other node.
	*/
        void mergeByDegree() {
            int degreeTableSize = 45; //can easily calculate from CLRS book for optimum performance
            Node degreeTable[] = new Node[degreeTableSize];	//degreeTable used to keep track of nodes with the same degree
            int numRoots = 0; //holds number of nodes in the root list
            Node a = maxNode;
            if (a != null) {
                numRoots++;
                a = a.right;
                while (a != maxNode) {
                    numRoots++;
                    a = a.right;
                }
            }
            while (numRoots-- > 0) {
                int deg = a.degree;
                Node next = a.right;
                while (true) {
                    Node b = degreeTable[deg];
                    if (b == null) break;
                    if (a.key < b.key) {
                        Node tmp = b;
                        b = a;
                        a = tmp;
                    }
                    makeChild(b, a);
                    degreeTable[deg] = null;
                    deg++;
                }
                degreeTable[deg] = a;
                a = next;
            }
		//merge operation and resetting the max node 
            maxNode = null;
            for (int i = 0; i < degreeTableSize; i++) {
                Node b = degreeTable[i];
                if (b == null) continue;
                if (maxNode != null) {
                    b.left.right = b.right;
                    b.right.left = b.left;

                    b.left = maxNode;
                    b.right = maxNode.right;
                    maxNode.right = b;
                    b.right.left = b;

                    if (b.key > maxNode.key) {
                        maxNode = b;
                    }
                } else {
                    maxNode = b;
                }
            }
        }
	//makechild merges two nodes, makes the one with largest value the parent and other the child
        void makeChild(Node b, Node a) {
            b.left.right = b.right;
            b.right.left = b.left;

            b.parent = a;
            if (a.child == null) {
                a.child = b;
                b.right = b;
                b.left = b;
            } else {
                b.left = a.child;
                b.right = a.child.right;
                a.child.right = b;
                b.right.left = b;
            }
            a.degree++;
            b.mark = false;
        }
    }
	/*main function to process the input data and to write the program output to a file*/
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis(); //used to calculate the program running time
        maxFibonacciHeap heap = new maxFibonacciHeap();	//heap object to perform all the heap operations
        Map<String, Node> map = new HashMap<>();	//map to keep track of the hashtag and the corresponding node
        br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));	//args[0] contians input file
        if (args.length > 1) //if provided, takes output file to write
            out = new PrintWriter(new FileWriter(args[1]));
        else out = new PrintWriter(System.out);	//otherwise writes to the standard output
        try {
            while (true) {
                String vars = br.readLine();
                if (vars == null || vars.equalsIgnoreCase("stop")) break; //break if it encounters eof character or "STOP"
                String params[] = vars.split(" ");
                if (params.length == 2) {	//increase operation 
                    String hashTag = params[0].substring(1);
                    int val = Integer.parseInt(params[1]);
                    if (map.containsKey(hashTag)) { //if hashtag already exists in the heap then simple increase the node value
                        int newKey = map.get(hashTag).key + val;
                        heap.setKeyValue(map.get(hashTag), newKey);
                    } else {	//otherwise add new node to the heap
                        Node node = new Node(hashTag, val);
                        heap.insertNode(node);
                        map.put(hashTag, node);
                    }
                } else {
                    int cnt = Integer.parseInt(params[0]); //check top 'cnt' hashtags 
                    Node to_add[] = new Node[cnt];	//first remove and keep them in the array so that we can push it again after checking
                    for (int i = 0; i < cnt; i++) {
                        Node node = heap.popMax();
                        map.remove(node.hashTag);
                        Node newNode = new Node(node.hashTag, node.key);
                        to_add[i] = newNode;
                        out.print(node.hashTag); //write the output to a file
                        if (i != cnt - 1) out.print(",");
                    }
                    for (Node a : to_add) { // add them back the heap
                        heap.insertNode(a);
                        map.put(a.hashTag, a);
                    }
                    out.println();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long totalTime = end - start;
        System.out.println("Total time in Milli Seconds: " + totalTime); //calculate program running time
	out.close();
    }
}
