all : 
	 javac -classpath ".:common-math.jar" src/timeSeriesPredection/DataPredectionAlgorithm.java -d .
	 java -classpath ".:common-math.jar" timeSeriesPredection.DataPredectionAlgorithm
	 $(RM) -r timeSeriesPredection	