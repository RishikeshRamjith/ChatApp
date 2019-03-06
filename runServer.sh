#!/bin/bash
cd bin/
echo Type port number - leave blank for default
read port
java Server $port
