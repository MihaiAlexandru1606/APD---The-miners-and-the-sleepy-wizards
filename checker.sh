#!/bin/bash

numberHash=100
numberNode=(50 100 200 400 500 1000)
numberMiner=(1 2 4 8)
numberWizar=(1 2 4 8)
pathFile="./test/test"

#rm -fr ./test &> /dev/null
#mkdir ./test 
#make -f Makefile.generator build &> /dev/null
make build &> /dev/null

for j in ${numberNode[*]}
do
	#make -f Makefile.generator run ARGS="$pathFile $j $numberHash" &> /dev/null
	
	for wizar in ${numberWizar[*]}
	do
		for miner in ${numberMiner[*]}
		do
			make run ARGS="$pathFile$j $numberHash $wizar $miner" &> /dev/null
			if [ $? -eq 0 ]; then
				tput setaf 2
				echo "Node : $j Wizar : $wizar Miner : $miner .....PASS"

			else
				tput setaf 1
				echo "Node : $j Wizar : $wizar Miner : $miner .....FAIL"
			fi
		done
	done 
done

make clean &> /dev/null
#make -f Makefile.generator clean &> /dev/null
