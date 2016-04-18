
import java.io.*;
import java.util.*;


/*
 * Author: Giridhar
 */

public class bbst {

	private RedBlackNode head;
	
	//constructor only to initialize tree root node to null.
	public bbst(){
		head = null;
	}

	/*
	 * Build a red black tree with inputs from inputfile 
	 * in sorted order of id and perform other operations 
	 * such as increase, reduce, next, previous, count, inrange.
	 */
	public static void main(String[] args){
		bbst T = new bbst();
		Scanner sc = new Scanner(System.in);
		try {
			List<tuple> idList = T.prepareList(args[0]);
			T.head = T.construct(0,0,idList.size()-1,T.getRedLevel(idList.size()), 
					idList.iterator());
			String s = "";

			while((s = sc.nextLine())!=null) {
				String[] arr = s.split("\\s+");
				switch(commands.valueOf(arr[0])) {
				case increase : 
					RedBlackNode ni = T.increase(Integer.parseInt(arr[1]),
							Integer.parseInt(arr[2]));
					if (null!=ni)
						System.out.println(ni.getCount());
					else
						System.out.println(0);
					break;
				case reduce : 
					RedBlackNode nr = T.reduce(Integer.parseInt(arr[1]),
							Integer.parseInt(arr[2]));
					if (null!=nr)
						System.out.println(nr.getCount());
					else
						System.out.println(0);
					break;
				case count : RedBlackNode nf = T.find(Integer.parseInt(arr[1]));
				if (null!=nf)
					System.out.println(nf.getCount());
				else
					System.out.println(0);
				break;
				case inrange : 
					int val =T.inrange(Integer.parseInt(arr[1]),
							Integer.parseInt(arr[2]));
					System.out.println(val);
					break;
				case next : RedBlackNode nn = T.next(Integer.parseInt(arr[1]));
				if (null!=nn)
					System.out.println(nn.getId() + " " + nn.getCount());
				else
					System.out.println("0" + " " + "0");
				break;
				case previous : RedBlackNode np = T.previous(Integer.parseInt(arr[1]));
				if (null!=np)
					System.out.println(np.getId() + " " + np.getCount());
				else
					System.out.println("0" + " " + "0");
				break;
				case quit : System.exit(0);
				default: System.out.println("Incorrect operation");
				break;
				}			
			}
		} 
		catch(Exception e){
			System.out.println("input exception");
		}
		finally {
			sc.close();
		}

	}
	
	/*
	 * enum values for switch
	 */
	static enum commands {
		increase, reduce, count, inrange, next, previous, quit
	}

	/*
	 * just a wrapper for main inrange function
	 */
	public int inrange (int low, int high) {
    	return inrange(head, low, high);
    }
	
	/*
	 * Read entire input file into ArrayList as tuples.
	 */
	public List<tuple> prepareList (String input) {
		BufferedReader in = null;
		List<tuple> idList = null;

		try {
			String line = "";
			int inputSize=0;
			in = new BufferedReader(new FileReader(input));
			if((line = in.readLine()) != null) {
				inputSize = Integer.parseInt(line);
				idList = new ArrayList<tuple>(inputSize);
			} else {
				System.out.println("input file error");
			}

			int i=0;
			while((line = in.readLine()) != null) {
				String[] arr = line.split("\\s+");
				idList.add(i, new tuple(Integer.parseInt(arr[0]), 
						Integer.parseInt(arr[1])));
				i++;
			}
		} catch (Exception e) {
			System.out.println("Error while reading the input");
		}
		finally {
			try {
				in.close();
			} catch (IOException e) {
				System.out.println("Could not close input reader");
			}
		}
		return idList;
	}

	/*
	 * get the level where red needs to be painted.
	 */
	public int getRedLevel(int size) {
		int redLevel = 0;
		for (int i = size-1;i>=0;i = ((i/2)-1))
			redLevel++;
		return redLevel;
	}
	
	/*
	 * adds up count of every node with id 
	 * between low and high recursively and returns.
	 */
	public int inrange(RedBlackNode current, int low, int high) {
    	if(current==null)
    		return 0;
    	if(current.getId()>=low && current.getId()<=high)
    		return  current.getCount() + inrange(current.left, low, high) + inrange(current.right, low, high);
    	else if (current.getId() < low)
    		return inrange(current.right, low, high);
    	else
    		return inrange(current.left, low, high);
    }
    
    
    /*
     * arrange the nodes in each level and paint red in the given redlevel
     * passing iterator instead of list has improved the performance a bit.
     */
	public RedBlackNode construct(int startLevel, int low, int high,int redLevel, Iterator<tuple> iter) {

		if (low > high) 
			return null;
		int middle = (low + high)>>>1;
		RedBlackNode left = null;
		if (low < middle)
			left = construct(startLevel+1, low, middle-1, redLevel,
					iter);

		tuple r = iter.next();
		int id = r.getId();
		int count = r.getCount();

		RedBlackNode midNode =  new RedBlackNode(id, count, null);

		if (startLevel == redLevel)
			midNode.color = true;

		if (left != null) {
			midNode.left = left;
			left.parent = midNode;
		}

		if (middle < high) {
			RedBlackNode right = construct(startLevel+1, middle+1, high, redLevel, iter);
			midNode.right = right;
			right.parent = midNode;
		}

		return midNode;
	}
	
	public boolean isRed(RedBlackNode r) {
        return (r == null ? false : r.color);
    }
	
	/*
	 * Increments count of an event id if it is present
	 * in the red black tree. If not, inserts the id  
	 * into the tree.
	 */
	public RedBlackNode increase(int id, int count){
    	
        if (head == null) {
            head = new RedBlackNode(id, count, null);
            return head;
        }
        RedBlackNode temp = head;
        RedBlackNode parent = null;
        while (temp != null) {
            parent = temp;
            if (id < temp.getId())
                temp = temp.left;
            else if (id > temp.getId())
                temp = temp.right;
            else {
                temp.setCount(count+temp.getCount());
                return temp;
            }
        }
        RedBlackNode newNode = new RedBlackNode(id, count, null);
        return insert(newNode,parent);
    }
	
	/*
	 * inserts the given node into red black tree and fixes 
	 * the violation.
	 */
	public RedBlackNode insert (RedBlackNode newNode, RedBlackNode parent) {
        if (newNode.getId() <  parent.getId())
            parent.left = newNode;
        else
            parent.right = newNode;
        newNode.parent = parent;
        insertFixUp(newNode);
        
        return newNode;
    }
	
	/*
	 * Fixes the violation present in the red black tree 
	 * after insert.
	 */
	public void insertFixUp (RedBlackNode r) {
        r.color = true;
        while (r != null && r != head && r.parent.color == true) {
            if (getParent(r) == getLeft(getParent(getParent(r)))) {
                RedBlackNode m = getRight(getParent(getParent(r)));
                if (isRed(m) == true) {
                	RedBlackNode temp = getParent(r);
                	if (temp != null)
                        temp.color = false;
                    if(m!=null)
                    	m.color = false;
                    r = getParent(getParent(r));
                    if (r != null)
                        r.color = true;
                    
                } else {
                    if (r == getRight(getParent(r))) {
                        r = getParent(r);
                        leftRotate(r);
                    }
                    RedBlackNode temp = getParent(r);
                    if (temp != null)
                        temp.color = false;
                    temp = getParent(getParent(r));
                    if (temp != null)
                        temp.color = true;
                    rightRotate(getParent(getParent(r)));
                }
            } else { 
                RedBlackNode m = getLeft(getParent(getParent(r)));
                if (isRed(m) == true) {
                	RedBlackNode temp = getParent(r);
                	if (temp != null)
                        temp.color = false;
                	if (m != null)
                        m.color = false;
                	
                    r = getParent(getParent(r));
                    if (r != null)
                        r.color = true;
                } else {
                    if (r == getLeft(getParent(r))) {
                        r = getParent(r);
                        rightRotate(r);
                    }
                    RedBlackNode temp = getParent(r);
                	if (temp != null)
                        temp.color = false;
                	temp = getParent(getParent(r));
                    if (temp != null)
                        temp.color = true;
                    leftRotate(temp);
                }
            }
        }
        head.color = false;
    }
	
	/*
	 * performs the right rotation.
	 */
	public void rightRotate (RedBlackNode node) {
        if (node != null) {
            RedBlackNode lt = node.left;
            node.left = lt.right;
            if (lt.right != null) lt.right.parent = node;
            lt.parent = node.parent;
            if (node.parent == null)
                head = lt;
            else if (node.parent.right == node)
                node.parent.right = lt;
            else node.parent.left = lt;
            lt.right = node;
            node.parent = lt;
        }
    }
	
	/*
	 * returns parent of given node.
	 */
	public RedBlackNode getParent(RedBlackNode r) {
        return (r == null ? null: r.parent);
    }
	
	/*
	 * returns left node of given node.
	 */
	public RedBlackNode getLeft(RedBlackNode r) {
        return (r == null) ? null: r.left;
    }
	
	/*
	 * return right node of given node.
	 */
	public RedBlackNode getRight(RedBlackNode r) {
        return (r == null) ? null: r.right;
    }
	
	/*
	 * performs left rotation on the given node.
	 */
	public void leftRotate (RedBlackNode node) {
        if (node != null) {
            RedBlackNode rt = node.right;
            node.right = rt.left;
            if (rt.left != null)
                rt.left.parent = node;
            rt.parent = node.parent;
            if (node.parent == null)
                head = rt;
            else if (node.parent.left == node)
                node.parent.left = rt;
            else
                node.parent.right = rt;
            rt.left = node;
            node.parent = rt;
        }
    }
	
	/*
	 * decrements count of an event id by given value
	 * if the id is present in the red black tree. 
	 * If count becomes 0 or lesser, removes the node. 
	 * If the event is not present, returns null.
	 */
	public RedBlackNode reduce(int id, int count) {
        RedBlackNode r = find(id);
        if (r == null)
            return null;
        if(count<r.getCount()) {
        	r.setCount(r.getCount()-count);
        	return r;
        }	
        delete(r);
        return null;
	}
	
	/*
	 * Finds the node for a given event id in 
	 * the red black tree.
	 */
	public RedBlackNode find(int id) {
        RedBlackNode temp = head;
        while (temp != null) {
            if (id < temp.getId())
                temp = temp.left;
            else if (id > temp.getId())
                temp = temp.right;
            else
                return temp;
        }
        return null;
    }
	
	/*
	 * removes the given node from red black tree.
	 */
	public void delete (RedBlackNode node) {
        if (node.left != null && node.right != null) {
            RedBlackNode succ = getSuccessor(node);
            node.setId(succ.getId());
            node.setCount(succ.getCount());
            node = succ;
        } 
        RedBlackNode replacement = (node.left != null ? node.left : node.right);
        if (replacement != null) {
            replacement.parent = node.parent;
            if (node.parent == null)
                head = replacement;
            else if (node == node.parent.left)
                node.parent.left  = replacement;
            else
                node.parent.right = replacement;
            node.left = node.right = node.parent = null;
            if (node.color == false)
            	deleteFixUp(replacement);
        } else if (node.parent == null) { 
            head = null;
        } else { 
            if (node.color == false)
            	deleteFixUp(node);
            if (node.parent != null) {
                if (node == node.parent.left)
                    node.parent.left = null;
                else if (node == node.parent.right)
                    node.parent.right = null;
                node.parent = null;
            }
        }
        
    }
	
	/*
	 * returns successor of given node.
	 */
	public RedBlackNode getSuccessor(RedBlackNode node) {
        if (node == null)
            return null;
        else if (node.right != null) {
            RedBlackNode p = node.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            RedBlackNode p = node.parent;
            RedBlackNode child = node;
            while (p != null && child == p.right) {
                child = p;
                p = p.parent;
            }
            return p;
        }
    }
	
	/*
	 * fixes the violations occurred after removal of
	 * a node in red black tree.
	 */
	public void deleteFixUp(RedBlackNode node) {
        while (node != head && isRed(node) == false) {
            if (node == getLeft(getParent(node))) {
                RedBlackNode sib = getRight(getParent(node));

                if (isRed(sib) == true) {
                	
                	if (sib != null)
                        sib.color = false;
                	RedBlackNode temp = getParent(node);
                	if (temp != null)
                        temp.color = true;
                    
                    leftRotate(getParent(node));
                    sib = getRight(getParent(node));
                }

                if (isRed(getLeft(sib))  == false &&
                		isRed(getRight(sib)) == false) {
                	if (sib != null)
                        sib.color = true;
                    node = getParent(node);
                } else {
                    if (isRed(getRight(sib)) == false) {
                    	RedBlackNode temp = getLeft(sib);
                    	if (temp != null)
                            temp.color = false;
                    	
                        if (sib != null)
                            sib.color = true;
                        rightRotate(sib);
                        sib = getRight(getParent(node));
                    }
                    if (sib != null)
                        sib.color = isRed(getParent(node));
                    RedBlackNode temp = getParent(node);
                	if (temp != null)
                        temp.color = false;
                	temp = getRight(sib);
                	if (temp != null)
                        temp.color = false;
                    
                    leftRotate(getParent(node));
                    node = head;
                }
            } else { 
                RedBlackNode sib = getLeft(getParent(node));

                if (isRed(sib) == true) {
                	if (sib != null)
                        sib.color = false;
                	RedBlackNode temp = getParent(node);
                	if (temp != null)
                        temp.color = true;
                   
                    rightRotate(getParent(node));
                    sib = getLeft(getParent(node));
                }

                if (isRed(getRight(sib)) == false &&
                		isRed(getLeft(sib)) == false) {
                	if (sib != null)
                        sib.color = true;
                	
                    node = getParent(node);
                } else {
                    if (isRed(getLeft(sib)) == false) {
                    	RedBlackNode temp = getRight(sib);
                    	if (temp != null)
                            temp.color = false;
                    	if (sib != null)
                            sib.color = true;
                        
                        leftRotate(sib);
                        sib = getLeft(getParent(node));
                    }
                    if (sib != null)
                        sib.color = isRed(getParent(node));
                    RedBlackNode temp = getParent(node);
                	if (temp != null)
                        temp.color = false;
                	temp = getLeft(sib);
                	if (temp != null)
                        temp.color = false;
                   
                    rightRotate(getParent(node));
                    node = head;
                }
            }
        }
        
        if (node != null)
            node.color = false;
        
    }
	
	/*
	 * returns a node with id next to the given
	 * id in sorted order (or inorder) of the 
	 * red black tree.
	 */
	public RedBlackNode next (int id) {
        RedBlackNode temp = head;
        while (temp != null) {
        	if(id >= temp.getId()){
        		if (temp.right != null) {
                    temp = temp.right;
                } else {
                    RedBlackNode parent = temp.parent;
                    RedBlackNode current = temp;
                    while (parent != null && current == parent.right) {
                    	current = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
        	}
        	else {
                if (temp.left == null)
                	return temp;
                else
                	temp = temp.left;                    
            }
            
        }
        return null;
    }
	
	/*
	 * returns a node with id prior to the given 
	 * id in sorted order (or inorder) of the
	 * red black tree.
	 */
	public RedBlackNode previous(int id) {
        RedBlackNode temp = head;
        while (temp != null) {
        	if(id <= temp.getId()){
        		if (temp.left == null) {
                    RedBlackNode parent = temp.parent;
                    RedBlackNode current = temp;
                    while (parent != null && current == parent.left) {
                        current = parent;
                        parent = parent.parent;
                    }
                    return parent;
                } else
                	temp = temp.left;
        	}
        	else{
        		if (temp.right == null)
        			return temp;
                else
                	temp = temp.right;
                    
        	}
            
        }
        return null;
    	
    }
	
	/*
	 * class for a node in red black tree.
	 */
	class RedBlackNode {
	    private int id;
	    private int count;
	    public RedBlackNode left;
	    public RedBlackNode right;
	    public RedBlackNode parent;
	    public boolean color = false;

	    RedBlackNode(int id, int count, RedBlackNode parent) {
	        this.id = id;
	        this.count = count;
	        this.parent = parent;
	    }

	    public int getId() {
	        return id;
	    }
	    
	    public void setId(int id) {
	        this.id = id;
	    }

	    public int getCount() {
	        return count;
	    }

	    public void setCount(int count) {
	        this.count = count;
	    }

	}
	
	/*
	 * class for id,count format to read
	 * input file.
	 */
	class tuple {
		private int id;
		private int count;
		public tuple(int id, int count) {
			this.id = id;
			this.count = count;
		}
		public int getId(){
			return this.id;
		}
		public int getCount(){
			return this.count;
		}
		
	}

}

 
 
 
