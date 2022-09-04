#!/bin/sh
for FILE in *-in.txt

do
	echo $FILE
	base=${FILE%-in.txt}
    java -cp '..'matching $FILE > $base.groupO.out.txt # replace with your command!
    diff $base.groupO.out.txt $base-out.txt
done
$SHELL