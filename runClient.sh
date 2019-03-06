#!/bin/bash
cd bin/
echo Type in server IP (leave blank for default)
read ipaddress
echo Type port number (leave blank for default)
read port
java Client $ipaddress $port
