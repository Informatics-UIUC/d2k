#! /bin/tcsh

unsetenv CLASSPATH


# set the classpath 
# !!!! MAKE SURE .d2k.props path for modules is right too!!

setenv D2K_BASE $HOME/projects/d2kv4

# set the classpath for make

setenv CLASSPATH .
foreach i ($D2K_BASE/lib/*.jar)
  setenv CLASSPATH ${i}:${CLASSPATH}
end
foreach i ($HOME/lib/*.jar)
  setenv CLASSPATH ${CLASSPATH}:${i}
end

echo $CLASSPATH


#setenv CLASSPATH /opt/JBuilder6/jdk1.3.1/lib:${D2K_BASE_CLASSES}


setenv SCRIPTS_DIR /home/anca/projects/testing/clustering/pred-disc

#foreach bench ($SCRIPTS_DIR/*.zip)
#    foreach dataFile ($SCRIPTS_DIR/*.csv)
#	set dataOut = ${dataFile:t:r} 
#	set dataDir = ${bench:t:r}
#	set dataLog = ${SCRIPTS_DIR}/$dataDir/$dataOut.log
#	mkdir -p $SCRIPTS_DIR/$dataDir
#	echo 'set "Input File Name" fileName "' $dataFile '"' > scr
#	echo 'set "Input File Name1" fileName "' ${SCRIPTS_DIR}/$dataDir/$dataOut.res '"' >> scr
#	echo "executing $dataDir for $dataOut" 
#	java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load ${bench} -script scr |& tee $dataLog
#	echo "execution of $bench ended"
#    end
#end


# only iris and waverform datasets for kmeans, BuckShot, Coverage, Fractionation, Hierarchical
cd ${SCRIPTS_DIR}/KMeansTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/iris.csv"' > scr
echo 'set "Input File Name1" fileName "iris.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load KMeansTest.zip -script scr |& tee iris.log


cd ${SCRIPTS_DIR}/BuckShotTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/iris.csv"' > scr
echo 'set "Input File Name1" fileName "iris.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load BuckShotTest.zip -script scr |& tee iris.log


cd ${SCRIPTS_DIR}/FractionationTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/iris.csv"' > scr
echo 'set "Input File Name1" fileName "iris.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load FractionationTest.zip -script scr |& tee iris.log

cd ${SCRIPTS_DIR}/CoverageTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/iris.csv"' > scr
echo 'set "Input File Name1" fileName "iris.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load CoverageTest.zip -script scr |& tee iris.log

cd ${SCRIPTS_DIR}/HierachicalTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/iris.csv"' > scr
echo 'set "Input File Name1" fileName "iris.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load HierachicalTest.zip -script scr |& tee iris.log


cd ${SCRIPTS_DIR}/KMeansTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/waveform-5000.csv"' > scr
echo 'set "Input File Name1" fileName "waveform-5000.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load KMeansTest.zip -script scr |& tee waveform-5000.log


cd ${SCRIPTS_DIR}/BuckShotTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/waveform-5000.csv"' > scr
echo 'set "Input File Name1" fileName "waveform-5000.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load BuckShotTest.zip -script scr |& tee waveform-5000.log


cd ${SCRIPTS_DIR}/FractionationTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/waveform-5000.csv"' > scr
echo 'set "Input File Name1" fileName "waveform-5000.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load FractionationTest.zip -script scr |& tee waveform-5000.log

cd ${SCRIPTS_DIR}/CoverageTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/waveform-5000.csv"' > scr
echo 'set "Input File Name1" fileName "waveform-5000.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load CoverageTest.zip -script scr |& tee waveform-5000.log

cd ${SCRIPTS_DIR}/HierachicalTest
echo 'set "Input File Name" fileName "'${SCRIPTS_DIR}'/waveform-5000.csv"' > scr
echo 'set "Input File Name1" fileName "waveform-5000.res"' >> scr
java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load HierachicalTest.zip -script scr |& tee waveform-5000.log





#foreach i (*.txt)
#echo "executing " ${i}
#    diff ${i} ../Test/${i} 
#echo "diff of " ${i} " ended"
#end


