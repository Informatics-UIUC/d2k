#! /bin/tcsh

# path to the automated itineraries that run without use input 
setenv SCRIPTS_DIR /home/anca/projects/testing/basic4Scripts/pred-disc

# path to the directory containing the data files for testing
# all itineraries will be run with all data file
setenv DATA_DIR /home/anca/data/UCI

# path to the d2k instalation, lib directory contains necessary jars to run d2k
setenv D2K_BASE $HOME/projects/d2kv4
# set the classpath for make

unsetenv CLASSPATH

setenv CLASSPATH .
foreach i ($D2K_BASE/lib/*.jar)
  setenv CLASSPATH ${i}:${CLASSPATH}
end
foreach i ($HOME/lib/*.jar)
  setenv CLASSPATH ${CLASSPATH}:${i}
end

echo $CLASSPATH

foreach bench ($SCRIPTS_DIR/*.zip)
    foreach dataFile ($DATA_DIR/*.csv)
	set dataOut = ${dataFile:t:r} 
	set dataDir = ${bench:t:r}
	set dataLog = ${SCRIPTS_DIR}/$dataDir/$dataOut.log
	mkdir -p $SCRIPTS_DIR/$dataDir
	echo 'set "Input File Name" fileName "' $dataFile '"' > scr
	echo 'set "Input File Name1" fileName "' ${SCRIPTS_DIR}/$dataDir/$dataOut.res '"' >> scr
	echo "executing $dataDir for $dataOut" 
	java -server -Xmx236M -Xms236M ncsa.d2k.D2K -nogui -load ${bench} -script scr |& tee $dataLog
	echo "execution of $bench ended"
    end
end

#foreach i (*.txt)
#echo "executing " ${i}
#    diff ${i} ../Test/${i} 
#echo "diff of " ${i} " ended"
#end


