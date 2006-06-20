package ncsa.d2k.modules.projects.dtcheng.matrix;

//import java.lang.Math.*;
import ncsa.d2k.core.modules.*;

//import Jama.Matrix;

public class SplitToRowChunks extends ComputeModule {

	public String getModuleName() {
		return "SplitToRowChunks";
	}

	public String getModuleInfo() {
		return "This module will pull in a matrix and then slice and dice it. " +
		"into a specified number of chunks. That is, the columns will be " +
		"kept together, but the rows will be pushed out in blocks along with " +
		"a numbered string and such. This is meant for cutting up datasets into " +
		"chunks for parallel processing and use with Write2DMFM. The chunks will " +
		"be sequentially pushed out of the output pipes.";
	}

	public String getInputName(int i) {
		switch (i) {
	      case 0:
	        return "Matrix";
	      case 1:
	      	return "nChunks";
	      case 2:
	      	return "BufferSize";
	      case 3:
	        return "FileName";
	      case 4:
	      	return "nElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String getInputInfo(int i) {
		switch (i) {
	      case 0:
	        return "Matrix";
	      case 1:
	      	return "nChunks: number of chunks that should be pushed out";
	      case 2:
	      	return "BufferSize: for use by the writer, will not be use here";
	      case 3:
	        return "FileName: the base filename which will have the chunk number appended";
	      case 4:
	      	return "nElementsThreshold";
		default:
			return "Error!  No such input.  ";
		}
	}

	public String[] getInputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Integer", "java.lang.Integer", "java.lang.String",
				"java.lang.Long", };
		return types;
	}

	public String getOutputName(int i) {
		switch (i) {
	      case 0:
	        return "ChunkMatrix";
	      case 1:
	      	return "BufferSize";
	      case 2:
	        return "FileName";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String getOutputInfo(int i) {
		switch (i) {
	      case 0:
	        return "ChunkMatrix: a piece of the original matrix";
	      case 1:
	      	return "BufferSize: a repeat of the input";
	      case 2:
	        return "FileName: the base file name with the chunk number appended";
		default:
			return "Error!  No such output.  ";
		}
	}

	public String[] getOutputTypes() {
		String[] types = {
				"ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
				"java.lang.Integer", "java.lang.String",
				};
		return types;
	}

	public void doit() throws Exception {
		MultiFormatMatrix X = (MultiFormatMatrix) this.pullInput(0);
		int nChunks = (int)((Integer)this.pullInput(1)).intValue();
		Integer BufferSize = (Integer)this.pullInput(2);
		String BaseFileName = (String)this.pullInput(3);
		long nElementsThreshold = ((Long)this.pullInput(4)).longValue();

		long nRowsTotal = X.getDimensions()[0];
		long nCols = X.getDimensions()[1];

		MultiFormatMatrix thisChunk = null;
		
		// do a modulus to figure out how bad the remainder is
		
		
		long leftOver = -9;
		long nRowsChunk = -5;
		long nRowsRemaining = -6;
		int formatIndex = -1;
		long nElements = -3;
		long sourceRow = -5;

		nRowsRemaining = nRowsTotal;
		sourceRow = 0;
		
		for (int chunkIndex = 0; chunkIndex < nChunks; chunkIndex++){
			leftOver = nRowsRemaining % (long)(nChunks - chunkIndex);
			if (leftOver == 0) {
				// it's all good, chunk away
				nRowsChunk = nRowsTotal / (long) nChunks;
			} else {
				nRowsChunk = nRowsTotal/ (long)nChunks + 1;
			}

			nElements = nRowsChunk * nCols;
			if (nElements < nElementsThreshold) {
				formatIndex = 1;
			} else {
				formatIndex = 3;
			}
			
			thisChunk = new MultiFormatMatrix(formatIndex, new long[] {nRowsChunk,nCols});
//			System.out.println("starting to transcribe... nRowsChunk = " + nRowsChunk);
			for (long rowIndex = 0; rowIndex < nRowsChunk; rowIndex ++) {
				for (long colIndex = 0; colIndex < nCols; colIndex++) {
/*					System.out.println("sourceRow = " + sourceRow + "; rowIndex = " + rowIndex +
							"; colIndex = " + colIndex);
*/
					thisChunk.setValue(rowIndex,colIndex,
							X.getValue(sourceRow,colIndex));
				}
				sourceRow++;
			}
			
			nRowsRemaining -= nRowsChunk;

/*			System.out.println("done transcribing chunk [" + chunkIndex +
					"] nRowsRemaining = " + nRowsRemaining);
*/		
		this.pushOutput(thisChunk, 0);
		this.pushOutput(BufferSize, 1);
		this.pushOutput(new String(BaseFileName + chunkIndex + "_"), 2);
		
		}
		
		
		
		
		
		
	}
}






