#!/bin/bash
cd ../fire_logs/
cat * | python ../tools/log_parser_access.py
