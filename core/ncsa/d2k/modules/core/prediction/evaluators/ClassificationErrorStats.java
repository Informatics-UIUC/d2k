package ncsa.d2k.modules.core.prediction.evaluators;

import java.util.*;
import ncsa.d2k.modules.core.datatype.table.PredictionTable;

/**
 * A simple structure passed between the ClassificationErrorEvaluator and the
 * ClassificationErrorReport modules.  This module has information about the
 * models built by each set of testing and training data.
 */
public final class ClassificationErrorStats implements java.io.Serializable {

	private ArrayList testingSize;
	private ArrayList trainingSize;
	private ArrayList correctPreds;
	private ArrayList percentageCorrect;
	private ArrayList tables;

	ClassificationErrorStats() {
		testingSize = new ArrayList();
		trainingSize = new ArrayList();
		correctPreds = new ArrayList();
		percentageCorrect = new ArrayList();
		tables = new ArrayList();
	}

	void addFold(int train, int test, int correct, double per, PredictionTable table) {
		trainingSize.add(new Integer(train));
		testingSize.add(new Integer(test));
		correctPreds.add(new Integer(correct));
		percentageCorrect.add(new Double(per));
		tables.add(table);
	}

	int getTrainingSize(int fold) {
		Integer i = (Integer)trainingSize.get(fold);
		return i.intValue();
	}

	int getTestingSize(int fold) {
		Integer i = (Integer)testingSize.get(fold);
		return i.intValue();
	}

	int getCorrectPredictions(int fold) {
		Integer i = (Integer)correctPreds.get(fold);
		return i.intValue();
	}

	double getPercentageCorrect(int fold) {
		Double d = (Double)percentageCorrect.get(fold);
		return d.doubleValue();
	}

	PredictionTable getTable(int fold) {
		PredictionTable pt = (PredictionTable)tables.get(fold);
		return pt;
	}
}