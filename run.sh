#!/usr/bin/env bash
export MPJ_HOME=`pwd`/mpj-v0_44/
cd out/production/mpj/pancake/
$MPJ_HOME/bin/mpjrun.sh -np 2 Network
