#!-*- coding:utf-8 -*-
import os
import re
import codecs
LINE_PATTERN = re.compile(r'\A\s*(?P<key>.+?)\s*=\s*(?P<val>.*?)\s*\Z')


def load_config(config_file_path):
    if not os.path.isfile(config_file_path):
        raise ValueError('Error: file not found %s' % config_file_path)
    entries = {}
    line_num = 0
    for line in codecs.open(config_file_path, mode='r', encoding='utf_8', errors='ignore'):
        line_num += 1
        line = line.strip()
        if not line:
            continue     # skip empty line
        if line[0] == ';' or line[0] == '#':
            continue    # skip comment line
        match = LINE_PATTERN.match(line)
        if not match:
            print('Warning(file %s, line %d): invalid entry' % (config_file_path, line_num))
            continue
        key = match.group('key')
        if key in entries:
            print('Warning(file %s, line %d): duplicated entry ignored for "%s"' % (config_file_path, line_num, key))
            continue
        entries[key] = match.group('val')
    return entries
