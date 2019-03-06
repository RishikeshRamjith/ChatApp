#!/bin/bash
cd bin/
echo Type in server IP
read ipaddress
java Client $ipaddress
