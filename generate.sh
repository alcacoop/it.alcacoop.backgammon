#!/bin/bash
echo { > pos.json
inkscape -S $1 |grep pos|cut -d, -f 1,2| sed 's/,/: /'|sed 's/$/,/'|sort >> pos.json
inkscape -S $1 |grep dice1|cut -d, -f 1,2| sed 's/,/: /'|sed 's/$/,/' >> pos.json
inkscape -S $1 |grep dice0|cut -d, -f 1,2| sed 's/,/: /'|sed 's/$/,/' >> pos.json
inkscape -S $1 |grep up|cut -d, -f 1,3| sed 's/,/: /'|sed 's/$/,/' >> pos.json
inkscape -S $1 |grep down|cut -d, -f 1,3| sed 's/,/: /'|sed 's/$/,/' >> pos.json
inkscape -S $1 |grep down|cut -d, -f 1,4| sed 's/down/pos/' |sed 's/,/: /' >> pos.json
echo } >> pos.json
