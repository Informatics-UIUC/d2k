package ncsa.d2k.modules.core.prediction.LWR;

import ncsa.d2k.util.splaytree.*;

public class DoubleNode extends BNode {

public double value;

public DoubleNode(double x){
	super();
	value = x;
}

public int compareTo( DoubleNode node) {

	if ( this.value < node.value)
		return 1;
	else if (this.value > node.value)
		return -1;
		else
			return 0;
}

}
